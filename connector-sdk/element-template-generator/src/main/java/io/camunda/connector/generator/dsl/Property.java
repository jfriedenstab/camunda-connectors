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
package io.camunda.connector.generator.dsl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Objects;

@JsonInclude(Include.NON_NULL)
public abstract sealed class Property
    permits BooleanProperty, DropdownProperty, HiddenProperty, StringProperty, TextProperty {

  protected final String id;
  protected final String label;
  protected final String description;
  protected final Boolean optional;
  protected final String value;
  protected final PropertyConstraints constraints;
  protected final FeelMode feel;
  protected final String group;
  protected final PropertyBinding binding;
  protected final PropertyCondition condition;

  protected final String type;

  public enum FeelMode {
    optional,
    required,
    @JsonIgnore
    disabled
  }

  public Property(
      String id,
      String label,
      String description,
      Boolean optional,
      String value,
      PropertyConstraints constraints,
      FeelMode feel,
      String group,
      PropertyBinding binding,
      PropertyCondition condition,
      String type) {
    this.id = id;
    this.label = label;
    this.description = description;
    this.optional = optional;
    this.value = value;
    this.constraints = constraints;
    this.feel = feel;
    this.group = group;
    this.binding = binding;
    this.condition = condition;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  public Boolean isOptional() {
    return optional;
  }

  public String getValue() {
    return value;
  }

  public PropertyConstraints getConstraints() {
    return constraints;
  }

  public FeelMode getFeel() {
    if (feel == FeelMode.disabled) {
      return null;
    }
    return feel;
  }

  public String getGroup() {
    return group;
  }

  public PropertyBinding getBinding() {
    return binding;
  }

  public String getType() {
    return type;
  }

  public PropertyCondition getCondition() {
    return condition;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Property property = (Property) o;
    return optional == property.optional
        && Objects.equals(id, property.id)
        && Objects.equals(label, property.label)
        && Objects.equals(description, property.description)
        && Objects.equals(value, property.value)
        && Objects.equals(constraints, property.constraints)
        && feel == property.feel
        && Objects.equals(group, property.group)
        && Objects.equals(binding, property.binding)
        && Objects.equals(type, property.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, label, description, optional, value, constraints, feel, group, binding, type);
  }

  @Override
  public String toString() {
    return "Property{"
        + "name='"
        + id
        + '\''
        + ", label='"
        + label
        + '\''
        + ", description='"
        + description
        + '\''
        + ", optional="
        + optional
        + ", value='"
        + value
        + '\''
        + ", constraints="
        + constraints
        + ", feel="
        + feel
        + ", group='"
        + group
        + '\''
        + ", binding="
        + binding
        + ", type='"
        + type
        + '\''
        + '}';
  }
}
