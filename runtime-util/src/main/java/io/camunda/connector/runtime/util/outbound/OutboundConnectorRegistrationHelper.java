/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.camunda.connector.runtime.util.outbound;

import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.impl.ConnectorUtil;
import io.camunda.connector.impl.outbound.OutboundConnectorConfiguration;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Configuration class holding information of a connector. */
public class OutboundConnectorRegistrationHelper {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundConnectorRegistrationHelper.class);

  /** Pattern describing the connector env configuration pattern. */
  public static final Pattern CONNECTOR_FUNCTION_PATTERN =
      Pattern.compile("^CONNECTOR_(.*)_FUNCTION$");

  public static Map<String, String> hardwiredEnvironmentVariables;

  public static void addHardwiredEnvironmentVariable(String key, String value) {
    if (hardwiredEnvironmentVariables == null) {
      hardwiredEnvironmentVariables = new HashMap<>();
    }
    hardwiredEnvironmentVariables.put(key, value);
  }

  public static void clearHardwiredEnvironmentVariable() {
    hardwiredEnvironmentVariables = null;
  }

  public static Map<String, String> getEnvironmentVariables() {
    if (hardwiredEnvironmentVariables != null) {
      HashMap<String, String> result = new HashMap<>();
      result.putAll(System.getenv());
      result.putAll(hardwiredEnvironmentVariables);
      return result;
    }
    return System.getenv();
  }

  public static boolean isEnvConfigured() {
    return getEnvironmentVariables().entrySet().stream()
        .anyMatch(entry -> CONNECTOR_FUNCTION_PATTERN.matcher(entry.getKey()).matches());
  }

  /**
   * Parses the connector registrations from the environment.
   *
   * @return the list of registrations
   */
  public static List<OutboundConnectorConfiguration> parse() {
    if (isEnvConfigured()) {
      LOGGER.info("Reading environment variables to find connectors that are not Spring beans");
      return parseFromEnv();
    } else {
      LOGGER.info("Parsing SPI to find connectors that are not Spring beans");
      return parseFromSPI();
    }
  }

  /**
   * Parses connectors registered via SPI ("auto discovery")
   *
   * @return the list of registrations
   */
  public static List<OutboundConnectorConfiguration> parseFromSPI() {

    return ServiceLoader.load(OutboundConnectorFunction.class).stream()
        .map(
            functionProvider -> {
              var function = functionProvider.get();

              return ConnectorUtil.getOutboundConnectorConfiguration(function.getClass())
                  .map(
                      cfg ->
                          new OutboundConnectorConfiguration(
                              cfg.getName(), cfg.getType(), cfg.getInputVariables(), function))
                  .orElseThrow(
                      () ->
                          new RuntimeException(
                              String.format(
                                  "OutboundConnectorFunction %s is missing @OutboundConnector annotation",
                                  function.getClass())));
            })
        .collect(Collectors.toUnmodifiableList());
  }

  /**
   * Parses the connector registrations configured through via environment variables.
   *
   * @return the list of registrations
   */
  public static List<OutboundConnectorConfiguration> parseFromEnv() {

    var connectors = new ArrayList<OutboundConnectorConfiguration>();

    for (var entry : getEnvironmentVariables().entrySet()) {

      var key = entry.getKey();

      var match = CONNECTOR_FUNCTION_PATTERN.matcher(key);

      if (match.matches()) {
        connectors.add(parseConnector(match.group(1)));
      }
    }

    return connectors;
  }

  private static OutboundConnectorConfiguration parseConnector(final String name) {

    var function =
        getEnv(name, "FUNCTION")
            .map(OutboundConnectorRegistrationHelper::loadConnectorFunction)
            .orElseThrow(() -> envMissing("No function specified", name, "FUNCTION"));

    var config = ConnectorUtil.getOutboundConnectorConfiguration(function.getClass());

    if (config.isEmpty()) {
      LOGGER.warn(
          "OutboundConnectorFunction {} is missing @OutboundConnector annotation",
          function.getClass().getName());
    }

    return new OutboundConnectorConfiguration(
        name,
        getEnv(name, "TYPE")
            .or(() -> config.map(OutboundConnectorConfiguration::getType))
            .orElseThrow(() -> envMissing("Type not specified", name, "TYPE")),
        getEnv(name, "INPUT_VARIABLES")
            .map(variables -> variables.split(","))
            .or(() -> config.map(OutboundConnectorConfiguration::getInputVariables))
            .orElseThrow(() -> envMissing("Variables not specified", name, "INPUT_VARIABLES")),
        function);
  }

  private static Optional<String> getEnv(final String name, final String detail) {
    return Optional.ofNullable(getEnvironmentVariables().get("CONNECTOR_" + name + "_" + detail));
  }

  @SuppressWarnings("unchecked")
  private static OutboundConnectorFunction loadConnectorFunction(String clsName) {

    try {
      var cls = (Class<OutboundConnectorFunction>) Class.forName(clsName);

      return cls.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException
        | InvocationTargetException
        | InstantiationException
        | IllegalAccessException
        | ClassCastException
        | NoSuchMethodException e) {
      throw loadFailed("Failed to load " + clsName, e);
    }
  }

  private static RuntimeException loadFailed(String s, Exception e) {
    return new IllegalStateException(s, e);
  }

  private static RuntimeException envMissing(String message, String name, String envKey) {
    return new RuntimeException(
        String.format(
            "%s: Please configure it via CONNECTOR_%s_%s environment variable",
            message, name, envKey));
  }
}