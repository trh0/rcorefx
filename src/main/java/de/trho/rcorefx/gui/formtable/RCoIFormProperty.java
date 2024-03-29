/*
 * Copyright 2018 trh0 - https://trho.de - https://github.com/trh0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.trho.rcorefx.gui.formtable;

import java.util.List;
import javafx.beans.property.StringProperty;

public interface RCoIFormProperty {

  StringProperty getValue(final String key);

  void setValue(final String key, final String value);

  String getValidatorRegex(final String key);

  void setValidatorRegex(final String key, final String regex);

  int getPropertiesCount();

  void setLabel(final String key, final String label);

  String getLabel(final String key);

  List<String> getKeys();

}
