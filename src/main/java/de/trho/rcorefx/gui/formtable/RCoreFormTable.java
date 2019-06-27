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
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class RCoreFormTable extends GridPane {

  @FXML
  private HBox                               c_ctrl;
  @FXML
  private TextField                          tx_filter;
  @FXML
  private Button                             bt_add, bt_remove;
  @FXML
  private TreeTableView<RCoTableObject>        table;
  private volatile boolean                   init    = false;
  private final ObservableList<RCoTableObject> records = FXCollections.observableArrayList();

  public RCoreFormTable() {}

  public RCoreFormTable(final String... keys) {

  }

  public void addItems(final List<RCoTableObject> itms) {
    this.records.addAll(itms);
    if (!init)
      initTable();
  }

  public void addItems(final RCoTableObject... objs) {
    this.records.addAll(objs);
    if (!init)
      initTable();
  }

  private void initTable() {
    final RCoTableObject rec = this.records.get(0);
    final List<RCoTableColumn> cols = new ArrayList<>();
    rec.getKeys().forEach(k -> {
      final RCoTableColumn col = new RCoTableColumn(rec.getLabel(k), k, rec.getValidatorRegex(k));
      cols.add(col);
    });
    this.table.getColumns().addAll(cols);
    final TreeItem<RCoTableObject> root =
        new JFXRecursiveTreeItem<RCoTableObject>(this.records, JFXRecursiveTreeObject::getChildren);
    table.setRoot(root);
  }

  @FXML
  void initialize() {
    this.records.addListener((ListChangeListener<RCoTableObject>) c -> {
    });
    String id = "id";
    String fn = "firstName";
    for (int i = 0; i < 74; i++) {
      RCoTableObject ob =
          new RCoTableObject(id, String.valueOf(i), fn, RCoreUtils.md5(String.valueOf(i * i)));
      ob.setLabel(id, "ID");
      ob.setValidatorRegex(id, "[a-z]+");
      ob.setLabel(fn, "FIRSTNAME");
      ob.setValidatorRegex(fn, "[a-z]+");
      this.records.add(ob);
    }
    table.setShowRoot(false);
    table.setEditable(true);
    initTable();

  }

}
