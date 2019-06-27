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
package de.trho.rcorefx.gui.tree;

import de.trho.rcorefx.util.RCoreImages;
import java.io.File;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

/**
 * A {@link javafx.scene.control.TreeItem} containing path values.
 * 
 * @author trh0 - TKoll
 *
 */
public class RCoreFileTreeItem extends TreeItem<String> {

  // this stores the full path to the file or directory
  private String             fullPath;
  private File               file;
  public static final Number size = 8;

  public String getFullPath() {
    return this.fullPath;
  }

  private boolean wasExpanded = false;

  public File getFile() {
    return this.file;
  }

  private boolean isDirectory;

  public boolean isDirectory() {
    return this.isDirectory;
  }

  public RCoreFileTreeItem(File aFile, boolean pragmaOnce) {
    super(aFile.toPath().toString());
    this.file = aFile;
    init(file, pragmaOnce);
    addHandlers();
  }

  public RCoreFileTreeItem(File file) {
    super(file.toPath().toString());
    this.file = file;
    init(file, true);
    addHandlers();
  }

  /**
   * Init 1 - Building the TreeItem and searching for children.
   * 
   * @author trh0 - TKoll
   *
   * @param file Path to aquire children from.
   * @param once NOT recursively, if TRUE.
   */
  private void init(File file, boolean once) {
    this.fullPath = file.toPath().toString();

    // test if this is a directory and set the icon
    if (file.isDirectory()) {
      this.isDirectory = true;
      this.setGraphic(RCoreImages.FOLDER_COLLAPSED.getImageView(size, size));
    } else {
      this.isDirectory = false;
      this.setGraphic(RCoreImages.FILE_ICON.getImageView(size, size));
      // if you want different icons for different file types this is
      // where you'd do it
    }
    // set the value
    if (!fullPath.endsWith(File.separator)) {
      // set the value (which is what is displayed in the tree)
      String value = file.toString();
      int indexOf = value.lastIndexOf(File.separator);
      if (indexOf > 0) {
        this.setValue(value.substring(indexOf + 1));
      } else {
        this.setValue(value);
      }
    }
    if (this.isDirectory && file.canRead() && !once) {
      for (File f : file.listFiles()) {
        this.getChildren().add(new RCoreFileTreeItem(f));
      }
    }

  }

  /**
   * Init 2 - Adding EventHandlers to the TreeItem.
   * 
   * @author trh0 - TKoll
   *
   */
  private void addHandlers() {
    this.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
      Platform.runLater(() -> {
        RCoreFileTreeItem source = RCoreFileTreeItem.class.cast(e.getTreeItem());
        if (source.isDirectory() && source.isExpanded()) {
          ImageView iv = (ImageView) source.getGraphic();
          iv.setImage(RCoreImages.FOLDER_EXPANDED.getImage(size, size));
        }
        ObservableList<TreeItem<String>> children = source.getChildren();
        synchronized (children) {
          if (!children.isEmpty() && !source.wasExpanded) {
            source.wasExpanded = true;
            try {
              children.forEach(child -> {
                RCoreFileTreeItem it = (RCoreFileTreeItem) child;
                File f = it.getFile();
                if (f.isDirectory() && f.canRead()) {
                  File[] fls = f.listFiles();
                  if (fls != null) {
                    for (File fl : fls) {
                      RCoreFileTreeItem treeNode = new RCoreFileTreeItem(fl);
                      ObservableList<TreeItem<String>> childsChilds = child.getChildren();
                      synchronized (childsChilds) {
                        childsChilds.add(treeNode);
                      }
                    }
                  }
                }
              });
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }

      });
    });

    this.addEventHandler(TreeItem.branchCollapsedEvent(), e -> {
      Platform.runLater(() -> {
        RCoreFileTreeItem source = RCoreFileTreeItem.class.cast(e.getTreeItem());
        if (source.isDirectory() && !source.isExpanded()) {
          ImageView iv = (ImageView) source.getGraphic();
          iv.setImage(RCoreImages.FOLDER_COLLAPSED.getImage(size, size));
        }
      });
    });
  }

}
