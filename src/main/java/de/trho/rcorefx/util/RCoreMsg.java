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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javafx.util.Pair;

/**
 * Utility-class for handling various problems with {@linkplain ResourceBundle}s.<br>
 * It also contains a Bundle for the application. <br>
 * TODO fix this mess
 * 
 * @author Tobias Koll - QuinScape GmbH
 */
public class RCoreMsg {

  private static ResourceBundle bundle = null;

  public static ResourceBundle appBundle() {
    if (bundle == null) {
      try {
        init();
      } catch (Exception e) {
        e.printStackTrace();
        bundle = new ResourceBundle() {
          @Override
          protected Object handleGetObject(String key) {
            return null;
          }

          @Override
          public Enumeration<String> getKeys() {
            return null;
          }
        };
      }
    }
    return bundle;
  }

  private static void init() throws IOException {
    final InputStream in =
        RCoreMsg.class.getClassLoader().getResourceAsStream("fxml/messages.properties");
    if (in != null) {
      bundle = new PropertyResourceBundle(in);
      in.close();
    }
  }

  /**
   * @author trh0 - TKoll
   * @param basePath Valid path to a bundle.
   * @return A {@linkplain Pair} containing the bundleBaseName and the bundle, if loadable.
   */
  public static Pair<String, ResourceBundle> loadBundle(String basePath) {
    ResourceBundle curBundle = null;
    String bundleBaseName = null;
    Pair<String, ResourceBundle> mapping = null;
    if (basePath == null || basePath.isEmpty()) {
      return mapping;
    }
    File f = new File(basePath);
    try (InputStream in = Files.newInputStream(f.toPath())) {
      curBundle = new PropertyResourceBundle(in);
      bundleBaseName = curBundle.getBaseBundleName();
      bundleBaseName = (bundleBaseName == null)
          ? f.getName().replaceAll("(?![a-zA-Z]+)((_|-)[A-Za-z]{2}){1,2}(?=\\.properties)", "")
          : bundleBaseName;
    } catch (Exception e) {
      e.printStackTrace();
      return mapping;
    }
    mapping = new Pair<>(bundleBaseName, curBundle);
    return mapping;
  }

}
