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
package de.trho.rcorefx.gui;

import de.trho.rcorefx.util.RCoreUtils;
import java.util.Arrays;
import java.util.Optional;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

/**
 * 
 * @see
 * 
 *      <a href=
 *      "https://stackoverflow.com/questions/24238858/property-sheet-example-with-use-of-a-propertyeditor-controlsfx"/>
 *      StackOverflow-post that helped alot.
 */

public class RCorePropertySheet extends ScrollPane {

  public class MultiFormItem implements PropertySheet.Item {
    private final StringProperty   group;
    private final StringProperty   name;
    private final Property<Object> value;
    private final StringProperty   descr;
    private volatile String        regex;
    private Class<?>               propertyClass;

    public MultiFormItem(final String name, final String group, final String regex,
        final Object value) {
      super();
      this.group = new SimpleStringProperty(group);
      this.name = new SimpleStringProperty(name);
      this.value = new SimpleObjectProperty<>(value);
      this.descr = new SimpleStringProperty();
      this.regex = regex;
      if (value != null) {
        this.propertyClass = value.getClass();
      } else {
        this.propertyClass = Object.class;
      }
    }

    @Override
    public Class<?> getType() {
      return this.propertyClass;
    }

    @Override
    public String getCategory() {
      return this.group.get();
    }

    @Override
    public String getName() {
      return this.name.get();
    }

    @Override
    public String getDescription() {
      return this.descr.get();
    }

    @Override
    public Object getValue() {
      return this.value.getValue();
    }

    @Override
    public void setValue(Object value) {
      if (propertyClass.isInstance(value)) {
        try {
          this.value.setValue(propertyClass.cast(value));
        } catch (Exception e) {
          this.setValue(value);
        }
      } else if (value != null) {
        this.propertyClass = value.getClass();
        this.setValue(value);
      } else if (value == null) {
        this.value.setValue(null);
      }
    }

    public <T> Optional<T> getTypedValue(final Class<T> clazz) {
      T value;
      try {
        value = clazz.cast(this.value.getValue());
      } catch (Exception e) {
        value = null;
      }
      return Optional.ofNullable(value);
    }

    @Override
    public Optional<ObservableValue<? extends Object>> getObservableValue() {
      return Optional.ofNullable(this.value);
    }

    public String getValidationRegex() {
      return this.regex;
    }

    public void setValidationRegex(final String regex) {
      this.regex = regex;
    }
  }

  @FXML
  private GridPane                                    c_grid;
  @FXML
  private HBox                                        c_top, c_buttom;
  @FXML
  private AnchorPane                                  c_anchor_props;
  @FXML
  private PropertySheet                               c_props;

  private volatile ObservableList<PropertySheet.Item> items;

  public <T> void addItem(final String key, final String group, final String descr,
      final String regex, final T value) {
    final Optional<MultiFormItem> item = getItem(key);
    final MultiFormItem it;
    if (item.isPresent()) {
      it = item.get();
      final int idx = this.items.indexOf(it);
      it.setValue(value);
      it.descr.set(descr);
      it.group.set(group);
      it.name.set(key);
      this.items.set(idx, it);
    } else {
      it = new MultiFormItem(key, group, regex, value);
      it.descr.set(descr);
      this.items.add(it);
    }
  }

  public void removeItem(final String name) {
    this.items.removeIf(it -> name.equals(it.getName()));
  }

  public Optional<MultiFormItem> getItem(final String name) {
    return this.items.stream().filter(it -> name.equals(it.getName())).map(el -> (MultiFormItem) el)
        .findFirst();
  }

  public <T> void setItem(final String key, final T value) {
    Optional<MultiFormItem> item = getItem(key);
    if (item.isPresent()) {
      item.get().setValue(value);
    }
  }

  public void setItemGroup(final String key, final String group) {
    Optional<MultiFormItem> item = getItem(key);
    if (item.isPresent()) {
      item.get().group.set(group);
    }
  }

  @FXML
  void initialize() {
    this.items = this.c_props.getItems();
    this.c_props.setPropertyEditorFactory(param -> {
      final Object value = param.getValue();
      final String rgx = ((MultiFormItem) param).getValidationRegex();
      if (value != null) {
        final Class<? extends Object> type = value.getClass();
        if (RCoreUtils.isNumber(type)) {
          return Editors.createNumericEditor(param);
        } else if (value instanceof Boolean || type == boolean.class) {
          return Editors.createCheckEditor(param);
        } else if (type.isAssignableFrom(Color.class)) {
          return Editors.createColorEditor(param);
        } else if (type.isEnum()) {
          return Editors.createChoiceEditor(param, Arrays.asList(type.getEnumConstants()));
        }
      }
      PropertyEditor<?> editor = Editors.createTextEditor(param);
      if (rgx != null) {
        RCoreUtils.addValidator(rgx, (TextField) editor.getEditor());
      }
      return editor;
    });
  }

}
