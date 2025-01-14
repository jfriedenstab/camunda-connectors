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
package io.camunda.connector.api.inbound.correlation;

/**
 * Base class for a unique set of properties of a single inbound Connector usage in the business
 * process definition.
 *
 * <p>Comparable interface defines the priorities among inbound connector execution (suitable
 * inbound candidates are executed in the natural order).
 */
public sealed interface ProcessCorrelationPoint extends Comparable<ProcessCorrelationPoint>
    permits MessageCorrelationPoint, StartEventCorrelationPoint {

  /**
   * Returns the ID of the correlation point, which also serves as a deduplication key. Correlation
   * points with the same ID logically represent the same inbound connector execution.
   */
  String getId();
}
