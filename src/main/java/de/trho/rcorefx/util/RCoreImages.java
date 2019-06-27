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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXCore standard images in an enum.
 * 
 * @author trh0 - TKoll
 *
 */
public enum RCoreImages {
  //@formatter:off
  add("add.png"), 
  check_empty("check-empty.png"), 
  check("check.png"), 
  close("close.png"), 
  copy("copy.png"), 
  cut("cut.png"), 
  delete("delete.png"), 
  edit("edit.png"), 
  filter("filter.png"), 
  folder_filled_closed("folder-filled-closed.png"), 
  folder_filled_open("folder-filled-open.png"), 
  folder_line_closed("folder-line-closed.png"),
  folder_line_open("folder-line-open.png"), 
  hide("hide.png"), 
  new_file_filled("new-file-filled.png"), 
  new_file_shape("new-file-shape.png"), 
  pref_closed("pref-closed.png"), 
  pref_open("pref-open.png"), 
  redo("redo.png"), 
  refresh("refresh.png"), 
  remove("remove.png"), 
  save("save.png"), 
  show("show.png"), 
  sort_az("sort-az.png"), 
  sort_za("sort-za.png"), 
  text_filled("text-filled.png"), 
  text_shape("text-shape.png"), 
  undo("undo.png"), 
  zoom_in("zoom-in.png"), 
  zoom_out("zoom-out.png"), 
  FILE_ICON("icon/file.png"), 
  FOLDER_COLLAPSED("folder_closed.png"), 
  FOLDER_EXPANDED("folder_open.png"), 
  HOST("root.png");
  //@formatter:on
  private final String location;

  /**
   * 
   * @author trh0 - TKoll
   *
   * @param width The width the image will be loaded with.
   * @param height The height the image will be loaded with.
   * @return The {@link javafx.scene.image.Image} loaded from enum's String.
   */
  public Image getImage(Number width, Number height) {
    Image image = null;
    try (InputStream is =
        RCoreImages.class.getClassLoader().getResourceAsStream("img/" + this.location)) {
      if (width != null && height != null) {
        image = new Image(is, width.doubleValue(), height.doubleValue(), true, true);
      } else {
        image = new Image(is);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return image;
  }

  /**
   * 
   * @return the selected image in the original dimensions.
   */
  public Image getImage() {
    return this.getImage(null, null);
  }

  /**
   * 
   * @author trh0 - TKoll
   *
   * @param width The width the image will be loaded with.
   * @param height The height the image will be loaded with.
   * @return The {@link javafx.scene.image.Image} saved in the assets wrapped into a
   *         {@link javafx.scene.image.ImageView}.
   */
  public ImageView getImageView(Number width, Number height) {
    ImageView iv = new ImageView();
    iv.setImage(this.getImage(width, height));
    iv.setPreserveRatio(true);
    return iv;
  }

  /**
   * 
   * @author trh0 - TKoll
   *
   * @param fitHeight Will be the value for {@link ImageView#setFitHeight(double)}<br>
   *        {@link ImageView#setPreserveRatio(boolean)} will be true.
   * @return
   */
  public ImageView getImageView(Number fitHeight) {
    ImageView iv = new ImageView();
    iv.setImage(this.getImage());
    iv.setPreserveRatio(true);
    iv.setFitHeight(fitHeight.doubleValue());
    return iv;
  }

  private RCoreImages(String locaiton) {
    this.location = locaiton;
  }

}
