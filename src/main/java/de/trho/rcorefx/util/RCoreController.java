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

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class RCoreController implements IFxmlLoader {

  public static String AppName = "";

  private RCoreController() {
    this.config = new RCoreCfg(AppName);
    this.rejectedExec = Executors.newSingleThreadExecutor();
    this.exec = new ScheduledThreadPoolExecutor(
        config.getTypedProperty("rcore.executor.poolsize", Integer.class),
        (RejectedExecutionHandler) (r, executor) -> {
          this.rejectedExec.execute(r);
        });
    this.loader = new FXMLLoader();
    this.stages = new Vector<>();
    this.testing = config.getTypedProperty("rcore.testing", Boolean.class);
  }

  private static RCoreController Instance = new RCoreController();

  public static RCoreController instance() {
    if (Instance == null)
      Instance = new RCoreController();
    return Instance;
  }

  private static final ClassLoader       cLoader = RCoreController.class.getClassLoader();
  private final Boolean                  testing;
  private final FXMLLoader               loader;
  private final RCoreCfg                 config;
  private final ScheduledExecutorService exec;
  private final ExecutorService          rejectedExec;
  private final List<Stage>              stages;
  private volatile Stage                 mStage;

  public String getConfigValue(final String key) {
    return config.getProperty(key);
  }

  public String setConfigValue(final String key, final String value) {
    return String.valueOf(config.setProperty(key, value));
  }

  public Future<Boolean> runTask(final Runnable r) {
    return exec.submit(Executors.callable(r, true));
  }

  public <T> Future<T> runTask(final Callable<T> c) {
    return exec.submit(c);
  }

  public <T> Future<Boolean> runTask(final Callable<T> c, final Property<T> valueTarget) {
    return exec.submit(() -> {
      try {
        final T result = c.call();
        valueTarget.setValue(result);
        return true;
      } catch (Exception e) {
        return false;
      }
    });
  }

  /**
   * 
   * @param stage Application's primaryStage
   */
  @SuppressWarnings("unchecked")
  public void initUi(final Stage stage) {
    this.mStage = stage;
    mStage.setOnCloseRequest(event -> {
      try {
        this.terminate();
      } catch (Exception e) {
        Platform.exit();
      }
    });
    try {

      String rootClass = testing ? config.getProperty("rcore.testing.root.class")
          : config.getProperty("rcore.root.class");

      final Class<?> clazz = Class.forName(rootClass);
      if (clazz == null || !Parent.class.isAssignableFrom(clazz)) {
        throw new IllegalArgumentException("Configuration inapropiate. Class " + rootClass
            + " does not inherit from javafx.scene.Parent.");
      }

      final Parent root = loadFxml((Class<? extends Parent>) clazz,
          testing ? config.getProperty("rcore.testing.root.fxml")
              : config.getProperty("rcore.root.fxml"));
      final Scene scene = new Scene(root);
      scene.getStylesheets().add(getStylesheetURL());

      final String iconPath = config.getProperty("rcore.iconpath");
      if (iconPath != null) {
        mStage.getIcons().add(new Image(cLoader.getResourceAsStream(iconPath)));
      }

      mStage.setTitle(config.getProperty("rcore.stage.title"));
      mStage.setWidth(config.getTypedProperty("rcore.stage.width", Integer.class));
      mStage.setMinWidth(config.getTypedProperty("rcore.stage.minwidth", Integer.class));
      mStage.setHeight(config.getTypedProperty("rcore.stage.height", Integer.class));
      mStage.setMinHeight(config.getTypedProperty("rcore.stage.minheight", Integer.class));
      mStage.setScene(scene);
      mStage.show();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      mStage.close();
    }

  }

  public synchronized <T extends Parent> T loadFxml(final Class<T> controllerClass,
      String fxmlLocation) {
    T viewController = null;
    try {
      viewController = controllerClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return loadFxml(viewController, viewController, fxmlLocation);
  }

  @Override
  public synchronized <T extends Parent> T loadFxml(final T controller, final Parent root,
      String fxmlLocation) {
    try {
      loader.setLocation(cLoader.getResource(fxmlLocation));
      loader.setRoot(root);
      if (controller != null) {
        loader.setController(controller);
      }
      loader.load();
      return controller;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized <T extends Parent> T loadFxml(final String fxmlLocation,
      final Class<T> rootClass) {
    try {
      loader.setLocation(cLoader.getResource(fxmlLocation));
      final Object view = loader.load();
      return (T) view;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public void serveContent(final Scene scene, final boolean show, final double x, final double y) {
    final Stage stage = new Stage();
    stage.setScene(scene);
    this.stages.add(stage);
    stage.setOnCloseRequest(event -> {
      stage.setScene(null);
      stage.close();
      stages.remove(stage);
    });
    if (show)
      stage.show();
  }

  public void serveContent(final Parent content, final boolean show, final double x,
      final double y) {
    this.serveContent(new Scene(content), show, x, y);
  }

  public String getStylesheetURL() {
    return cLoader.getResource(config.getProperty("rcore.css.file")).toExternalForm();
  }

  public List<Stage> stages() {
    return Collections.unmodifiableList(stages);
  }

  public void terminate() throws Exception {
    stages.forEach(s -> s.close());
    this.exec.shutdown();
    this.rejectedExec.shutdown();
    Platform.exit();
  }

}
