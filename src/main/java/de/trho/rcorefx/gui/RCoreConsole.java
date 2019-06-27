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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;

public class RCoreConsole {

  private final StringProperty property;
  private final PrintStream    err;
  private final PrintStream    out;
  private final OutputStream   newErr;
  private final OutputStream   newOut;
  private final PrintStream    relinkOut;
  private final PrintStream    relinkErr;
  private final StringBuilder  sbOut;
  private final Scene          scene;
  private final TextArea       console;

  public RCoreConsole(boolean captureNow) {
    this.console = new TextArea();
    this.property = new SimpleStringProperty();
    this.out = System.out;
    this.err = System.err;
    this.newErr = newErr();
    this.newOut = newOut();
    this.relinkOut = new PrintStream(newOut, true);
    this.relinkErr = new PrintStream(newErr, true);
    this.sbErr = new StringBuilder();
    this.sbOut = new StringBuilder();
    this.scene = new Scene(console);
    console.setPrefWidth(800);
    console.setPrefHeight(400);
    console.setEditable(false);
    console.textProperty().bind(property);
    console.textProperty().addListener((obs, ov, nv) -> {
      console.setScrollTop(Double.MAX_VALUE);
    });
    if (captureNow)
      this.startCapture();
  }

  public RCoreConsole() {
    this(false);
  }

  private final StringBuilder sbErr;
  char                        c = ',';

  public StringProperty binding() {
    return this.property;
  }

  public Scene scene() {
    return this.scene;
  }

  public void startCapture() {
    System.setOut(relinkOut);
    System.setErr(relinkErr);
  }

  private OutputStream newOut() {
    return new OutputStream() {

      @Override
      public void write(int b) throws IOException {
        if (b != -1) {
          c = (char) b;
          sbOut.append(c);
        }
      }

      @Override
      public void flush() throws IOException {
        String s = property.getValue();
        s = (s == null || s.isEmpty()) ? "" : s;
        property.setValue(s + sbOut.toString());
        out.print(sbOut.toString());
        out.flush();
        sbOut.delete(0, sbOut.length());
      };
    };
  }

  private OutputStream newErr() {
    return new OutputStream() {

      @Override
      public void write(int b) throws IOException {
        if (b != -1) {
          c = (char) b;
          sbErr.append(c);
        }
      }

      @Override
      public void flush() throws IOException {
        String s = property.getValue();
        s = (s == null || s.isEmpty()) ? "" : s;
        property.setValue(s + sbErr.toString());
        err.print(sbErr.toString());
        err.flush();
        sbErr.delete(0, sbErr.length());
      };
    };
  }

  public void reset() throws IOException {
    relinkOut.close();
    relinkErr.close();
    System.setOut(out);
    System.setErr(err);
  }
}
