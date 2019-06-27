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

import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;

public class RCoTableColumn extends TreeTableColumn<RCoTableObject, String> {

  public String getLabel() {
    return this.label;
  }

  public String getKey() {
    return this.key;
  }

  private final String            label;
  private final String            key;
  private volatile StringProperty cellValue;

  public RCoTableColumn(final String label, final String key, final String regex) {
    super(label);
    this.label = label;
    this.key = key;
    this.setPrefWidth(250);
    this.setCellFactory(param -> new TreeTableCell<>());
    this.setCellValueFactory(param -> {
      if (param != null && param.getValue() != null && param.getValue().getValue() != null) {
        return (this.cellValue = param.getValue().getValue().getValue(this.key));
      } else
        return this.cellValue;
    });
    this.setOnEditCommit((val) -> {
      val.getRowValue().getValue().setValue(key, val.getNewValue());
    });
  }

}
