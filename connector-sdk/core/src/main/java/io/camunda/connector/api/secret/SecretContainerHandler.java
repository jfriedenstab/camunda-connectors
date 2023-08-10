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
package io.camunda.connector.api.secret;

/**
 * Handler for secret containers. This allows to define a strategy for traversing the elements in a
 * secret container, e.g. using an iterator or for loop to handle all elements.
 *
 * <p><b>Deprecated:</b> Secret support is now available all fields during the binding process
 * without the need to annotate individual fields.
 */
@Deprecated(forRemoval = true)
public interface SecretContainerHandler {

  /**
   * Handles the container of potential secret elements. The handler decides how to iterate the
   * elements to handle them.
   *
   * @param value the container to handle
   * @param elementHandler the handler that can be called for each element in the container
   */
  void handleSecretContainer(Object value, SecretElementHandler elementHandler);
}