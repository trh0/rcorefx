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
package de.trho.rcorefx.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.reactfx.EventStreams;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class RCoreUtils {
  private RCoreUtils() {}

  public static void anchorConstaint(Double value, javafx.scene.Node... nodes) {
    if (nodes != null)
      for (javafx.scene.Node n : nodes) {
        AnchorPane.setBottomAnchor(n, value);
        AnchorPane.setTopAnchor(n, value);
        AnchorPane.setLeftAnchor(n, value);
        AnchorPane.setRightAnchor(n, value);
      }
  }

  public static void gridConstraints(Priority prio, VPos vpos, HPos hpos,
      javafx.scene.Node... nodes) {
    if (nodes != null)
      for (javafx.scene.Node n : nodes) {
        GridPane.setVgrow(n, prio);
        GridPane.setHgrow(n, prio);
        GridPane.setValignment(n, vpos);
        GridPane.setHalignment(n, hpos);
      }
  }

  /**
   * Utility method to ensure running stuff on the JavaFX application thread
   * 
   * @param r The runnable to run
   */
  public static void onFxThread(final Runnable r) {
    if (Platform.isFxApplicationThread()) {
      r.run();
    } else {
      Platform.runLater(r);
    }
  }

  /**
   * Loads a file located inside the classpath.
   * 
   * @param resource
   * @return The files content as a String.
   * @throws IOException
   * @throws IllegalArgumentException
   */
  public static String fromClasspath(String resource) throws IOException, IllegalArgumentException {
    if (resource == null || resource.isEmpty())
      throw new IllegalArgumentException("fileName='" + resource + "'");
    InputStream file = RCoreUtils.class.getClassLoader().getResourceAsStream(resource);
    Scanner sc = null;
    if (file != null) {
      try {
        StringBuilder sb = new StringBuilder();
        sc = new Scanner(file);
        while (sc.hasNext()) {
          sb.append(sc.nextLine());
        }
        return sb.toString();
      } finally {
        if (sc != null)
          sc.close();
      }
    } else {
      throw new IllegalArgumentException("fileName='" + resource + "' not found.");
    }
  }

  /**
   * Regular expression matching positive decimal numbers.
   */
  public static final Pattern PosDecimalPattern = Pattern.compile("^[0-9]+([\\.\\,][0-9]{1,})?$");
  /**
   * Regular expression matching integer numbers.
   */
  public static final Pattern IntNumberPattern  = Pattern.compile("^[0-9]+$");

  /**
   * Will set the TextField's style class depending on the value and the given regex.
   * 
   * @param regex Regular expression the input has to match.
   * @param target The textField to add validation to.
   * @param validClass CSS class to be used for valid values.
   * @param invalidClass CSS class to be used for invalid values.
   */
  public static void addValidator(final String regex, final TextField target,
      final String validClass, final String invalidClass) {
    final Pattern p = Pattern.compile(regex);
    EventStreams.valuesOf(target.textProperty()).successionEnds(Duration.ofMillis(250))
        .subscribe((value) -> {
          if (value != null && !value.isEmpty()) {
            if (p.matcher(value).matches()) {
              System.out.println(value + " maches");
              target.setStyle("");
              target.getStyleClass().remove(invalidClass);
              target.getStyleClass().add(validClass);
            } else {
              System.out.println(value + " no match, si ja");
              target.getStyleClass().remove(validClass);
              target.getStyleClass().add(invalidClass);
            }
          } else {
            target.getStyleClass().remove(validClass);
            target.getStyleClass().remove(invalidClass);
          }
        });
  }

  /**
   * Will set the textFields text-fill red if invalid, green if valid or black if empty.
   * 
   * @param regex Regular expression the input has to match.
   * @param target The textField to add validation to.
   */
  public static void addValidator(final String regex, final TextField target) {
    final Pattern p = Pattern.compile(regex);
    EventStreams.valuesOf(target.textProperty()).successionEnds(Duration.ofMillis(250))
        .subscribe((value) -> {
          if (value != null && !value.isEmpty()) {
            if (p.matcher(value).matches()) {
              target.setStyle("-fx-text-fill: green;");
            } else {
              target.setStyle("-fx-text-fill: red;");
            }
          } else {
            target.setStyle("-fx-text-fill: black;");
          }
        });
  }

  /**
   * 
   * @param pairs Key-Value pairs to be converted to a {@linkplain Map}
   * @return A map containing the Key-Value-Pairs.
   */
  public static Map<String, StringProperty> asMap(final String... pairs) {
    final Map<String, StringProperty> map;
    final int len = pairs.length;
    if (pairs == null || len < 2) {
      map = new HashMap<>();
    } else {
      map = new HashMap<>();
      for (int i = 0; i < len; i = i + 2) {
        if (i + 1 < len) {
          map.put(pairs[i], new SimpleStringProperty(pairs[i + 1]));
        }
      }
    }
    return map;
  }

  /**
   * 
   * @param base The base-array to join with the given values.
   * @param values Values to join to the given array.
   * @return Concatenated array containing all given values.
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] join(final T[] base, final T... values) {
    if (base == null) {
      return null;
    }
    final int blen = base.length;
    final int vlen = (values == null) ? 0 : values.length;
    if (vlen == 0) {
      return base;
    }
    final List<T> tees = new ArrayList<>();
    int i;
    for (i = 0; i < blen; i++) {
      tees.add(base[i]);
    }
    for (i = 0; i < vlen; i++) {
      tees.add(values[i]);
    }
    return tees.toArray(base);
  }

  /**
   * 
   * @param value A value to hash
   * @return A SHA256 hash of the given value.
   */
  public static final String sha256(final Object value) {
    if (value == null) {
      return null;
    }
    try {
      final MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return new BigInteger(1, digest.digest(value.toString().getBytes(StandardCharsets.UTF_8)))
          .toString(16).toUpperCase();
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * 
   * @param value A value to hash.
   * @return A MD5 hash of the given value.
   */
  public static final String md5(final Object value) {
    if (value == null) {
      return null;
    }
    try {
      final MessageDigest digest = MessageDigest.getInstance("MD5");
      return new BigInteger(1, digest.digest(value.toString().getBytes(StandardCharsets.UTF_8)))
          .toString(16).toUpperCase();
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * TODO return string
   * 
   * @param arr
   */
  public static <T> void arrayToString(final T[][] arr) {
    if (arr == null) {
      throw new IllegalArgumentException("Why toString() a null-array?");
    }
    final int sizex = arr.length;
    final int sizey = arr[0].length;
    for (int x = 0; x < sizex; x++) {
      for (int y = 0; y < sizey; y++) {
        System.out.print(arr[x][y] + " ");
      }
      System.out.println();
    }
  }

  /**
   * 
   * @param control A control to enable 'autoselect:all' on focus for.
   */
  public static void enableAutoSelectAll(final TextInputControl control) {
    control.focusedProperty()
        .addListener((ObservableValue<? extends Boolean> o, Boolean oldValue, Boolean newValue) -> {
          if (newValue) {
            Platform.runLater(() -> {
              control.selectAll();
            });
          }
        });
  }

  /**
   * Array containing Java's numeric types.
   */
  public static final Class<?>[] NumericTypes = new Class[] {byte.class, Byte.class, short.class,
      Short.class, int.class, Integer.class, long.class, Long.class, float.class, Float.class,
      double.class, Double.class, BigInteger.class, BigDecimal.class

  };

  /**
   * {@linkplain #NumericTypes}
   * 
   * @param type A type to check.
   * @return <code>true</code> if and only if the given type is a default numeric type.
   */
  public static boolean isNumber(Class<?> type) {
    if (type == null)
      return false;
    for (Class<?> cls : NumericTypes) {
      if (type == cls)
        return true;
    }
    return false;

  }

  /**
   * Constructs a generic StringConverter for a type extending
   * 
   * @return
   */
  public static final <T extends Number> StringConverter<T> NumberConverter() {
    return new StringConverter<T>() {
      @Override
      public String toString(final T o) {
        // formatter:off
        return (o instanceof Integer || o instanceof Byte || o instanceof Short || o instanceof Long
            || o instanceof AtomicInteger || o instanceof AtomicLong)
                ? String.valueOf(o.longValue())
                : (o instanceof Double || o instanceof Float || o instanceof BigDecimal)
                    ? String.valueOf(o.doubleValue())
                    : String.valueOf(o);
        // formatter:on
      }

      @SuppressWarnings("unchecked")
      @Override
      public T fromString(final String s) {
        if (s == null || s.isEmpty()) {
          return null;
        } else
          try {
            final Number val = Double.parseDouble(s);
            return (T) val;
          } catch (Exception e) {
            e.printStackTrace();
            return null;
          }
      }
    };
  }

  /**
   * 
   */
  public static final StringConverter<Number> NumConverter = new StringConverter<Number>() {
    final Pattern p = Pattern.compile("([\\+\\-]{0,1}[0-9]+)?([\\.\\,]{0,1}[0-9]+)?");

    @Override
    public String toString(Number num) {
      return num == null ? null : String.valueOf(num.doubleValue());
    }

    @Override
    public Number fromString(String str) {
      if (str != null && p.matcher(str).matches()) {
        try {
          return Double.parseDouble(str);
        } catch (Exception e) {
        }
      }
      return null;
    }
  };

  /**
   * Finds a {@linkplain TreeItem} that is a child of the provided <code>itm</code> with a certain
   * <code>value</code>ue.
   * 
   *
   * @param value The value to be contained by the itm.
   * @param itm The itm to look in.
   * @return Either null if not found or a param is null, or the {@linkplain TreeItem}
   */
  public static <T> TreeItem<T> ChildForValue(T value, TreeItem<T> itm) {
    TreeItem<T> res = null;
    if (itm != null && value != null) {
      res = (itm.getValue().equals(value)) ? itm : res;
      if (res == null) {
        for (TreeItem<T> it : itm.getChildren()) {
          if (value.equals(it.getValue())) {
            res = it;
            break;
          }
        }
      }
    }
    return res;
  }

  /**
   * 
   * @param xml
   * @return
   */
  public static String FormatXML(String xml) {
    String formatted = null;
    try {
      final InputSource src = new InputSource(new StringReader(xml));
      final org.w3c.dom.Node document =
          DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
      final Boolean keepDeclaration = Boolean.valueOf(xml.startsWith("<?xml"));

      // May need this:
      // System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");

      final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
      final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
      final LSSerializer writer = impl.createLSSerializer();

      writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
      writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);

      formatted = writer.writeToString(document);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return formatted;
  }

  /**
   * Using regex for recognizing the bundle keys and putting them into a {@linkplain TreeItem}
   * 
   * @author trh0 - TKoll
   *
   * @param theBundle A bundle to display the keys in a TreeView from.
   * @param rootName The bundleBaseName (will be used to name the root object). May be null or
   *        empty.
   * @param sepChar The seperator_char used to seperate bundleKeys.
   * @return A {@linkplain TreeItem} containing the bundle-key-hierarchiy.<br>
   */
  public static TreeItem<String> BundleToTreeView(ResourceBundle theBundle, String rootName,
      String sepChar) {
    if (theBundle == null) {
      return null;
    } else if (sepChar == null || sepChar.isEmpty()) {
      sepChar = ".";
    }
    if (rootName == null || rootName.isEmpty()) {
      rootName = theBundle.getBaseBundleName();
      rootName = (rootName == null) ? "---" : rootName;
    }

    TreeItem<String> root = new TreeItem<>(rootName);
    TreeItem<String> tmp = null;
    String keyPattern = "[a-zA-Z%s0-9]+((?=%s)|(?!%s))";
    String cons = "\\+\\-\\_\\.\\*";
    sepChar = "\\" + sepChar;
    cons = cons.replace(sepChar, "");
    keyPattern = String.format(keyPattern, cons, sepChar, sepChar);
    Pattern p = Pattern.compile(keyPattern);
    sepChar = sepChar.replace("\\", "");
    Set<String> keys = theBundle.keySet();

    Matcher m;

    for (String k : keys) {
      m = p.matcher(k);
      tmp = root;
      while (m.find()) {
        String match = m.group();
        TreeItem<String> itm = ChildForValue(match, tmp);
        if (itm == null) {
          itm = new TreeItem<String>(match);
          tmp.getChildren().add(itm);
        }
        tmp = itm;
      }
    }
    return root;
  }
}
