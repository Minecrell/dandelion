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

import static com.google.common.base.Strings.emptyToNull;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static net.minecrell.dandelion.Dandelion.NAME;
import static net.minecrell.dandelion.Dandelion.VERSION;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import net.minecrell.dandelion.Dandelion;
import net.minecrell.dandelion.decompiler.DandelionDecompiler;
import net.minecrell.dandelion.tree.ClassElement;
import net.minecrell.dandelion.tree.ClassTreeElement;
import net.minecrell.dandelion.ui.syntax.JavaSyntaxHighlighting;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public final class MainController  {

    private static final String CLASS_EXTENSION = ".class";

    private Dandelion dandelion;

    private DandelionDecompiler decompiler;

    @FXML private TreeView<ClassTreeElement> packageView;
    @FXML private TreeItem<ClassTreeElement> packageRoot;
    @FXML private TabPane tabs;

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
        closeFile();

        // Create Fernflower context
        this.decompiler = new DandelionDecompiler(openedPath);

        // TODO: Optimize class scanning (Don't code when tired)
        Multimap<String, String> classes = HashMultimap.create();

        this.decompiler.getClasses().forEach(name -> {
            int pos = name.lastIndexOf('/');
            if (pos >= 0) {
                String pack = name.substring(0, pos).replace('/', '.');
                name = name.substring(pos + 1);
                if (name.indexOf('$') >= 0) {
                    return;
                }

                System.out.println("Found " + pack + ": " + name);
                classes.put(pack, name);
            } else if (name.indexOf('$') == -1) {
                System.out.println("Found in root: " + name);
                classes.put("", name);
            }
        });

        packageRoot.getChildren().addAll(classes.keySet().stream()
                .sorted()
                .map(pack -> {
                    TreeItem<ClassTreeElement> root = new TreeItem<>(new ClassTreeElement(emptyToNull(pack)));
                    root.getChildren().addAll(classes.get(pack).stream()
                            .sorted()
                            .map(className -> new TreeItem<ClassTreeElement>(new ClassElement(emptyToNull(pack), className)))
                            .collect(Collectors.toList()));
                    return root;
                })
                .collect(Collectors.toList()));


    }

    @FXML
    private void selectClass(MouseEvent event) throws IOException {
        if (event.getClickCount() >= 2) {
            TreeItem<ClassTreeElement> item = packageView.getSelectionModel().getSelectedItem();
            if (item != null && (item.getValue() instanceof ClassElement)) {
                ClassElement element = (ClassElement) item.getValue();
                String path = element.getPath();

                System.out.println("Open: " + path);
                openClass(element, path);
            }
        }
    }

    private void openClass(ClassElement element, String path) throws IOException {
        final String id = element.getQualifiedName().replace('.', '_');

        tabs.getSelectionModel().select(
                this.tabs.getTabs().stream().filter(t -> t.getId().equals(id)).findFirst().orElseGet(() -> {
                    String text = this.decompiler.decompile(path);

                    CodeArea code = new CodeArea(text);
                    code.setParagraphGraphicFactory(LineNumberFactory.get(code));
                    code.setEditable(false);
                    JavaSyntaxHighlighting.highlight(code);

                    Tab tab = new Tab(element.getClassName());
                    tab.setId(id);
                    tab.setTooltip(new Tooltip(element.getQualifiedName()));
                    tab.setContent(code);

                    tabs.getTabs().add(tab);
                    return tab;
                })
        );
    }

    @FXML
    private void closeFile() {
        if (this.decompiler != null) {
            this.decompiler.close();
            this.decompiler = null;
        }

        this.packageRoot.getChildren().clear();
        this.tabs.getTabs().clear();
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
