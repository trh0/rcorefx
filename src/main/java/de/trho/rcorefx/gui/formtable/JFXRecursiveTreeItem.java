/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
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
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;

/**
 * RecursiveTreeItem is used along with RecursiveTreeObject to build the data model for the
 * TreeTableView.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXRecursiveTreeItem<T extends JFXRecursiveTreeObject<T>> extends TreeItem<T> {

  private Callback<JFXRecursiveTreeObject<T>, ObservableList<T>> childrenFactory;

  /**
   * predicate used to filter nodes
   */
  private ObjectProperty<Predicate<TreeItem<T>>>              predicate     =
      new SimpleObjectProperty<>((TreeItem<T> t) -> true);

  /**
   * list of original items
   */
  ObservableList<TreeItem<T>>                                 originalItems =
      FXCollections.observableArrayList();

  /**
   * list of filtered items
   */
  FilteredList<TreeItem<T>>                                   filteredItems;

  /***************************************************************************
   * * Constructors * *
   **************************************************************************/

  /**
   * creates empty recursive tree item
   *
   * @param func is the callback used to retrieve the children of the current tree item
   */
  public JFXRecursiveTreeItem(Callback<JFXRecursiveTreeObject<T>, ObservableList<T>> func) {
    this(null, null, func);
  }

  /**
   * creates recursive tree item for a specified value
   *
   * @param value of the tree item
   * @param func is the callback used to retrieve the children of the current tree item
   */
  public JFXRecursiveTreeItem(final T value,
      Callback<JFXRecursiveTreeObject<T>, ObservableList<T>> func) {
    this(value, null, func);
  }

  /**
   * creates recursive tree item for a specified value and a graphic node
   *
   * @param value of the tree item
   * @param graphic node
   * @param func is the callback used to retrieve the children of the current tree item
   */
  public JFXRecursiveTreeItem(final T value, Node graphic,
      Callback<JFXRecursiveTreeObject<T>, ObservableList<T>> func) {
    super(value, graphic);
    this.childrenFactory = func;
    init(value);
  }

  /**
   * creates recursive tree item from a data list
   *
   * @param dataList of values
   * @param func is the callback used to retrieve the children of the current tree item
   */
  public JFXRecursiveTreeItem(ObservableList<T> dataList,
      Callback<JFXRecursiveTreeObject<T>, ObservableList<T>> func) {
    JFXRecursiveTreeObject<T> root = new JFXRecursiveTreeObject<>();
    root.setChildren(dataList);
    this.childrenFactory = func;
    init(root);
  }

  private void init(JFXRecursiveTreeObject<T> value) {

    if (value != null) {
      addChildrenListener(value);
    }
    valueProperty().addListener((o, oldValue, newValue) -> {
      if (newValue != null) {
        addChildrenListener(newValue);
      }
    });

    this.filteredItems.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      return new Predicate<TreeItem<T>>() {
        @Override
        public boolean test(TreeItem<T> child) {
          // Set the predicate of child items to force filtering
          if (child instanceof JFXRecursiveTreeItem) {
            if (!((JFXRecursiveTreeItem<?>) child).originalItems.isEmpty()) {
              JFXRecursiveTreeItem<T> filterableChild = (JFXRecursiveTreeItem<T>) child;
              filterableChild.setPredicate(JFXRecursiveTreeItem.this.predicate.get());
            }
          }
          // If there is no predicate, keep this tree item
          if (JFXRecursiveTreeItem.this.predicate.get() == null) {
            return true;
          }
          // If there are children, keep this tree item
          if (child.getChildren().size() > 0) {
            return true;
          }
          // If its a group node keep this item if it has children
          if (child.getValue() instanceof JFXRecursiveTreeObject
              && child.getValue().getClass() == JFXRecursiveTreeObject.class) {
            return child.getChildren().size() != 0;
          }
          // Otherwise ask the TreeItemPredicate
          return JFXRecursiveTreeItem.this.predicate.get().test(child);
        }
      };
    }, this.predicate));

    this.filteredItems.predicateProperty().addListener((o, oldVal, newVal) -> {
      RCoreUtils.onFxThread(() -> {
        getChildren().clear();
        getChildren().addAll(filteredItems);
      });
    });
  }

  private void addChildrenListener(JFXRecursiveTreeObject<T> value) {
    final ObservableList<T> children = childrenFactory.call(value);
    originalItems = FXCollections.observableArrayList();
    for (T child : children) {
      originalItems.add(new JFXRecursiveTreeItem<>(child, getGraphic(), childrenFactory));
    }

    filteredItems = new FilteredList<>(originalItems, (TreeItem<T> t) -> true);

    this.getChildren().addAll(originalItems);

    children.addListener((ListChangeListener<T>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(t -> {
            JFXRecursiveTreeItem<T> newItem =
                new JFXRecursiveTreeItem<>(t, getGraphic(), childrenFactory);
            JFXRecursiveTreeItem.this.getChildren().add(newItem);
            originalItems.add(newItem);
          });
        }
        if (change.wasRemoved()) {
          change.getRemoved().forEach(t -> {
            for (int i = 0; i < JFXRecursiveTreeItem.this.getChildren().size(); i++) {
              if (this.getChildren().get(i).getValue().equals(t)) {
                // remove the items from the current/original items list
                originalItems.remove(this.getChildren().remove(i));
                i--;
              }
            }
            // final List<TreeItem<T>> itemsToRemove = RecursiveTreeItem.this.getChildren().stream()
            // .filter(treeItem -> treeItem.getValue().equals(t)).collect(Collectors.toList());
            // RecursiveTreeItem.this.getChildren().removeAll(itemsToRemove);
          });
        }
      }
    });

  }

  public final ObjectProperty<Predicate<TreeItem<T>>> predicateProperty() {
    return this.predicate;
  }

  public final Predicate<TreeItem<T>> getPredicate() {
    return this.predicateProperty().get();
  }

  public final void setPredicate(final Predicate<TreeItem<T>> predicate) {
    this.predicateProperty().set(predicate);
  }

}
