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

import javafx.collections.ObservableList;
import javafx.util.Callback;

public class RCoTreeItem extends JFXRecursiveTreeItem<RCoTableObject> {

  public RCoTreeItem(ObservableList<RCoTableObject> dataList,
      Callback<JFXRecursiveTreeObject<RCoTableObject>, ObservableList<RCoTableObject>> func) {
    super(dataList, func);
  }

}
