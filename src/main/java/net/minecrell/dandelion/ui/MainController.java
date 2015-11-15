/*
 * Copyright (c) 2015, Minecrell <https://github.com/Minecrell>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.minecrell.dandelion.ui;

import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static net.minecrell.dandelion.Dandelion.NAME;
import static net.minecrell.dandelion.Dandelion.VERSION;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import net.minecrell.dandelion.Dandelion;

import java.io.IOException;

public final class MainController  {

    private Dandelion dandelion;
    private Dialog<?> aboutDialog;

    @FXML
    private void initialize() {
        dandelion = Dandelion.getInstance();
    }

    @FXML
    private void showAbout() throws IOException {
        if (aboutDialog == null) {
            aboutDialog = new Alert(INFORMATION);
            aboutDialog.initOwner(dandelion.getPrimaryStage());

            aboutDialog.setTitle("About " + NAME);
            aboutDialog.setHeaderText(NAME + ' ' + VERSION);

            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/about.fxml"));
            Node content = loader.load();

            DialogPane pane = aboutDialog.getDialogPane();
            pane.getStyleClass().add("about");
            pane.getStylesheets().add("css/dandelion.css");

            pane.setContent(content);
        }

        aboutDialog.showAndWait();
    }

    @FXML
    private void close() {
        dandelion.getPrimaryStage().close();
    }

}
