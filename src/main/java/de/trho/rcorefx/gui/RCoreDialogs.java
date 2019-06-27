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

import de.trho.rcorefx.gui.tree.RCoreFileTreeItem;
import de.trho.rcorefx.util.RCoreImages;
import de.trho.rcorefx.util.RCoreMsg;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * 
 * @author trh0 - TKoll
 *
 */
public class RCoreDialogs {
  private RCoreDialogs() {}

  //@formatter:off
	public static final ButtonType TypeOk = 
			new ButtonType(RCoreMsg.appBundle().getString("alert.button.okay"), ButtonData.OK_DONE);
	public static final ButtonType TypeApply = 
			new ButtonType(RCoreMsg.appBundle().getString("alert.button.apply"), ButtonData.APPLY);
	public static final ButtonType TypeCancel = 
			new ButtonType(RCoreMsg.appBundle().getString("alert.button.cancel"), ButtonData.CANCEL_CLOSE);
	public static final ButtonType TypeClose = 
			new ButtonType(RCoreMsg.appBundle().getString("alert.button.close"), ButtonData.CANCEL_CLOSE);
	public static final ButtonType TypeYes = 
			new ButtonType(RCoreMsg.appBundle().getString("alert.button.yes"), ButtonData.YES);
	public static final ButtonType TypeNo = 
			new ButtonType(RCoreMsg.appBundle().getString("alert.button.no"),ButtonData.NO);
	public static final ButtonType TypeContinue = 
			new ButtonType(RCoreMsg.appBundle().getString("alert.button.continue"), ButtonData.NEXT_FORWARD);
	public static final ButtonType TypeNext = 
			new ButtonType(RCoreMsg.appBundle().getString("alert.button.next"), ButtonData.NEXT_FORWARD);
	public static final ButtonType TypeBack = 
			new ButtonType(RCoreMsg.appBundle().getString("alert.button.back"), ButtonData.BACK_PREVIOUS);
	//@formatter:on
  /**
   * @author trh0 - TKoll
   *
   * @param fromPath RootPath to select folders from.
   * @return A Dialog containing a fileBrowser.
   * @see {@link #filePathBrowser(Dialog, String)}
   */
  public static Dialog<File> openFileDialog(String fromPath) {
    Dialog<File> dialog = new Dialog<>();
    dialog.setResizable(true);
    dialog.getDialogPane().setContent(filePathBrowser(dialog, fromPath));
    dialog.getDialogPane().getButtonTypes().addAll(TypeOk, TypeCancel);

    return dialog;
  }

  /**
   * 
   * @author trh0 - TKoll
   * 
   * @param dialog The dialog the browser should be added to.
   * @param fromPath The rootPath to build the browser from. <br>
   *        Will be <code>FileSystems::getDefault()::getRootDirectories()</code> if null.
   * @return A {@link VBox} containing a {@link TreeView} containing all {@link File}s and folders
   *         wrapped into {@link TreeItem}s found recursively from the given path.<br>
   */
  public static VBox filePathBrowser(Dialog<File> dialog, String fromPath) {
    /**
     * Custom ChangeListener impl. for this scenario.
     * 
     * @author trh0 - TKoll
     *
     */
    class CListener implements ChangeListener<TreeItem<String>> {
      private File data = null;

      @Override
      public void changed(ObservableValue<? extends TreeItem<String>> observable,
          TreeItem<String> oldValue, TreeItem<String> newValue) {
        this.data = RCoreFileTreeItem.class.cast(observable.getValue()).getFile();
      }

    }
    CListener daListener = new CListener();
    dialog.setResultConverter(buttonType -> {
      if (buttonType == TypeOk)
        return daListener.data;
      else
        return null;
    });
    TreeView<String> treeView = null;
    String host = null;
    File rootFile = null;
    List<Path> rootDirectories = new ArrayList<>();
    if (fromPath != null) {
      rootFile = new File(fromPath);
      host = rootFile.getName();
      rootDirectories.add(rootFile.toPath());
    } else {
      try {
        host = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
      FileSystems.getDefault().getRootDirectories().forEach(rootDirectories::add);
    }
    TreeItem<String> rootNode = new TreeItem<>(host, RCoreImages.HOST.getImageView(16, 16));
    for (Path name : rootDirectories) {
      RCoreFileTreeItem treeNode = new RCoreFileTreeItem(new File(name.toUri()), false);
      rootNode.getChildren().add(treeNode);
    }
    rootNode.setExpanded(true);
    treeView = new TreeView<>(rootNode);
    treeView.getSelectionModel().selectedItemProperty().addListener(daListener);
    VBox v = new VBox();
    v.setPadding(new Insets(10, 10, 10, 10));
    v.setSpacing(10);
    v.getChildren().add(treeView);
    VBox.setVgrow(treeView, Priority.ALWAYS);
    return v;
  }

}
