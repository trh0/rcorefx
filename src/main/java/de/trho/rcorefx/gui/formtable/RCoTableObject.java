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

import de.trho.rcorefx.util.RCoreUtils;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class RCoTableObject extends JFXRecursiveTreeObject<RCoTableObject> implements RCoIFormProperty {

  public static final String                  RGX_PREFIX = "validator_";
  public static final String                  LBL_PREFIX = "_lbl";

  final ObservableMap<String, StringProperty> values;

  public RCoTableObject(final String... values) {
    this.values = FXCollections.observableHashMap();
    this.values.putAll(RCoreUtils.asMap(values));
  }

  @Override
  public synchronized StringProperty getValue(String key) {
    StringProperty p = this.values.get(key);
    if (p == null) {
      p = new SimpleStringProperty();
      this.values.put(key, p);
    }
    return p;
  }

  @Override
  public synchronized void setValue(String key, String value) {
    StringProperty p = this.values.get(key);
    if (p == null) {
      p = new SimpleStringProperty();
    }
    p.setValue(value);
    this.values.put(key, p);
  }

  @Override
  public synchronized String getValidatorRegex(String key) {
    StringProperty p = this.values.get(RGX_PREFIX + key);
    if (p == null) {
      p = new SimpleStringProperty();
      this.values.put(RGX_PREFIX + key, p);
    }
    return p.get();
  }

  @Override
  public synchronized void setValidatorRegex(String key, String regex) {
    this.setValue(RGX_PREFIX + key, regex);
  }

  @Override
  public int getPropertiesCount() {
    return this.values.size();
  }

  @Override
  public synchronized List<String> getKeys() {
    return this.values.keySet().stream()
        .filter(key -> !key.contains(LBL_PREFIX) && !key.contains(RGX_PREFIX))
        .collect(Collectors.toList());
  }

  @Override
  public synchronized void setLabel(String key, String label) {
    this.setValue(LBL_PREFIX + key, label);
  }

  @Override
  public synchronized String getLabel(String key) {
    return this.getValue(LBL_PREFIX + key).get();
  }

}
