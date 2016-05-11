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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
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
import javafx.stage.StageStyle;
import net.minecrell.dandelion.Dandelion;
import net.minecrell.dandelion.decompiler.DandelionDecompiler;
import net.minecrell.dandelion.tree.PackageElement;
import net.minecrell.dandelion.ui.syntax.JavaSyntaxHighlighting;
import net.minecrell.dandelion.util.ListComparator;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class MainController  {

    private static final String CLASS_EXTENSION = ".class";

    private Dandelion dandelion;

    private DandelionDecompiler decompiler;

    @FXML private TreeView<PackageElement> packageView;
    @FXML private TreeItem<PackageElement> packageRoot;
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

        Multimap<ImmutableList<String>, PackageElement> elements = TreeMultimap.create(ListComparator.get(), Ordering.natural());

        this.decompiler.getClasses().forEach(path -> {
            if (path.lastIndexOf('$') >= 0) {
                return;
            }

            PackageElement element = PackageElement.fromPath(PackageElement.Type.CLASS, path);
            System.out.println("Found class: " + element.getPath());
            elements.put(element.getPackage(), element);
        });

        for (String path : this.decompiler.getResources()) {
            if (path.endsWith(CLASS_EXTENSION)) {
                continue;
            }

            PackageElement element = PackageElement.fromPath(PackageElement.Type.RESOURCE, path);
            System.out.println("Found resource: " + element.getPath());
            elements.put(element.getPackage(), element);
        }

        elements.asMap().forEach((pack, packageElements) -> {
            TreeItem<PackageElement> root = new TreeItem<>(new PackageElement(PackageElement.Type.PACKAGE, pack));
            for (PackageElement element : packageElements) {
                root.getChildren().add(new TreeItem<>(element));
            }
            this.packageRoot.getChildren().add(root);
        });
    }

    @FXML
    private void selectClass(MouseEvent event) throws IOException {
        if (event.getClickCount() >= 2) {
            TreeItem<PackageElement> item = this.packageView.getSelectionModel().getSelectedItem();
            if (item != null && item.getValue().getType().isMember()) {
                PackageElement element = item.getValue();
                if (selectExistingTab(element)) {
                    return;
                }

                switch (element.getType()) {
                    case CLASS:
                        System.out.println("Open class: " + element.getPath());
                        openClass(element);
                        break;
                    case RESOURCE:
                        System.out.println("Open resource: " + element.getPath());
                        openResource(element);
                        break;
                }
            }
        }
    }

    private boolean selectExistingTab(PackageElement element) {
        final String id = Integer.toString(element.hashCode()); // TODO
        Optional<Tab> tab = this.tabs.getTabs().stream().filter(t -> t.getId().equals(id)).findFirst();
        if (tab.isPresent()) {
            this.tabs.getSelectionModel().select(tab.get());
            return true;
        } else {
            return false;
        }
    }

    private static CodeArea createCodeArea(String text) {
        CodeArea code = new CodeArea(text);
        code.setParagraphGraphicFactory(LineNumberFactory.get(code));
        code.setEditable(false);
        return code;
    }

    private void openClass(PackageElement element) throws IOException {
        String text = this.decompiler.decompile(element.getPath());
        CodeArea code = createCodeArea(text);
        JavaSyntaxHighlighting.highlight(code);
        addTab(element, code);
    }

    private void openResource(PackageElement element) throws IOException {
        // TODO: Detect text
        String text = new String(this.decompiler.getResource(element.getPath()), StandardCharsets.UTF_8);
        addTab(element, createCodeArea(text));
    }

    private void addTab(PackageElement element, CodeArea code) {
        Tab tab = new Tab(element.getName());
        tab.setId(Integer.toString(element.hashCode())); // TODO
        tab.setTooltip(new Tooltip(element.getPath()));
        tab.setContent(code);

        this.tabs.getTabs().add(tab);
        this.tabs.getSelectionModel().select(tab);
    }

    @FXML
    private void closeFile() throws IOException {
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
            aboutDialog.initStyle(StageStyle.UTILITY);

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
