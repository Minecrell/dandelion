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
import javafx.scene.control.DialogPane;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import net.minecrell.dandelion.Dandelion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class MainController  {

    private static final String CLASS_EXTENSION = ".class";

    private Dandelion dandelion;

    private Path openedPath;

    @FXML
    private TreeItem<String> packageRoot;

    private FileChooser openFileChooser;
    private Alert aboutDialog;

    @FXML
    private void initialize() {
        dandelion = Dandelion.getInstance();
    }

    @FXML
    private void openFile() throws IOException {
        if (openFileChooser == null) {
            openFileChooser = new FileChooser();
            openFileChooser.setTitle("Select JAR or class file");
            openFileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JAR file", "*.jar")
                    /*new FileChooser.ExtensionFilter("Class file", "*.class"),
                    new FileChooser.ExtensionFilter("All files", "*")*/
            );
        }

        File file = openFileChooser.showOpenDialog(dandelion.getPrimaryStage());
        if (file != null) {
            Path path = file.toPath();
            if (Files.exists(path)) {
                openFile(path);
            }
        }

    }

    private void openFile(Path openedPath) throws IOException {
        this.openedPath = openedPath;

        Map<String, TreeItem<String>> packages = new TreeMap<>();
        Set<TreeItem<String>> rootFiles = new TreeSet<>();

        System.out.println("Scanning " + openedPath.toAbsolutePath() + " for classes");

        try (ZipFile zip = new ZipFile(openedPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // TODO: Display all files
                if (!entry.getName().endsWith(CLASS_EXTENSION)) {
                    continue;
                }

                String name = entry.getName();

                int pos = name.lastIndexOf('/');
                if (pos >= 0) {
                    String pack = name.substring(0, pos).replace('/', '.');
                    name = name.substring(pos + 1, name.length() - CLASS_EXTENSION.length());

                    System.out.println("Found " + pack + ": " + name);

                    TreeItem<String> parent = packages.computeIfAbsent(pack, TreeItem::new);
                    parent.getChildren().add(new TreeItem<>(name));
                } else {
                    name = name.substring(0, name.length() - CLASS_EXTENSION.length());
                    System.out.println("Found in root: " + name);

                    rootFiles.add(new TreeItem<>(name));
                }
            }
        }

        packageRoot.getChildren().addAll(packages.values());
        packageRoot.getChildren().addAll(rootFiles);
    }

    @FXML
    private void closeFile() {
        this.openedPath = null;
        this.packageRoot.getChildren().clear();
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
