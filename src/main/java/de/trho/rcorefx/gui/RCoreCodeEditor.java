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
import javafx.scene.Parent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class RCoreCodeEditor extends Parent {
  final WebView        webview = new WebView();

  private String       editingCode;

  private final String editingTemplate;
  private final VBox   box;
  private Mode         mode;

  private String applyEditingTemplate() {
    return editingTemplate.replace("${code}", editingCode).replace("${mode}", mode.mode());
  }

  public void setCode(String newCode) {
    this.editingCode = newCode;
    webview.getEngine().loadContent(applyEditingTemplate());
  }

  public String getCodeAndSnapshot() {
    this.editingCode = (String) webview.getEngine().executeScript("editor.getValue();");
    return editingCode;
  }

  public void revertEdits() {
    setCode(editingCode);
  }

  public Region getPane() {
    return this.box;

  }

  public enum Mode {
    C("text/x-csrc", "codemirror/clike_min.js"), CPP("text/x-c++src",
        "codemirror/clike_min.js"), KOTLIN("text/x-kotlin", "codemirror/clike_min.js"), SCALA(
            "text/scala",
            "codemirror/clike_min.js"), JAVA("text/x-java", "codemirror/clike_min.js"), XML(
                "application/xml",
                "codemirror/xml.js"), HTML("text/html", "codemirror/xml.js"), SQL("text/x-sql",
                    "codemirror/sql.js"), CSS("text/css", "codemirror/css.js"), JS(
                        "text/javascript", "codemirror/js.js"), JSON("application/json",
                            "codemirror/js.js"), TYPESCRIPT("application/typescript",
                                "codemirror/js.js"), RAW("text/plain", "codemirror/clike_min.js");
    private final String mode;
    private final String jsSource;

    private Mode(String s, String jsSource) {
      this.mode = s;
      this.jsSource = jsSource;
    }

    public String mode() {
      return this.mode;
    }

    public String source() {
      return this.jsSource;
    }
  }

  /**
   * 
   * @param editingCode
   * @param title
   */
  public RCoreCodeEditor(String editingCode, String title, Mode mode) {
    this.mode = (mode == null) ? Mode.XML : mode;
    this.editingTemplate = getTemplate();
    this.editingCode = (this.mode == Mode.XML) ? RCoreUtils.FormatXML(editingCode) : editingCode;
    this.box = new VBox();
    webview.getEngine().loadContent(applyEditingTemplate());
    box.setPrefWidth(VBox.USE_COMPUTED_SIZE);
    box.getChildren().add(webview);
    VBox.setVgrow(webview, Priority.ALWAYS);
    webview.prefWidthProperty().bind(box.widthProperty());
    webview.prefHeightProperty().bind(box.heightProperty());
    this.getChildren().add(box);
  }

  private String getTemplate() {
    try {
      return "<!doctype html>" + "<html>" + "<head>" + "  <style>"
          + RCoreUtils.fromClasspath("codemirror/codemirror_min.css") + "</style>" + "  <script>"
          + RCoreUtils.fromClasspath("codemirror/codemirror_min.js") + "</script>" + "  <script>"
          + RCoreUtils.fromClasspath(this.mode.source()) + "</script>" + "</head>" + "<body>"
          + "<textarea id=\"code\" name=\"code\">\n" + "${code}" + "</textarea>" + "<script>"
          + "  var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {"
          + "    lineNumbers: true," + "    matchBrackets: true," + "    mode: \"${mode}\""
          + "  });" + "</script>" + "</body>" + "</html>";
    } catch (Exception e) {
      e.printStackTrace();
      return "<!doctype html>" + "<html>" + "<head>"
          + "  <link rel=\"stylesheet\" href=\"http://codemirror.net/lib/codemirror.css\">"
          + "  <script src=\"http://codemirror.net/lib/codemirror.js\"></script>"
          + "  <script src=\"http://codemirror.net/mode/clike/clike.js\"></script>" + "</head>"
          + "<body>" + "<form><textarea id=\"code\" name=\"code\">\n" + "${code}"
          + "</textarea></form>" + "<script>"
          + "  var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {"
          + "    lineNumbers: true," + "    matchBrackets: true," + "    mode: \"${mode}\""
          + "  });" + "</script>" + "</body>" + "</html>";
    }
  }
}
