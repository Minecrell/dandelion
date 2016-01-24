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
package net.minecrell.dandelion.ui.control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Hyperlink;
import net.minecrell.dandelion.Dandelion;

import java.io.IOException;

public class WebLink extends Hyperlink {

    public WebLink() {
        setOnAction(event -> {
            String url = getUrl();

            try { // First try the JavaFX way to open URLs (doesn't seem to be supported everywhere)
                Dandelion.getInstance().getHostServices().showDocument(url);
                return;
            } catch (Throwable ignored) {
            }

            try { // Try the Linux way instead
                new ProcessBuilder("xdg-open", url).inheritIO().start();
            } catch (IOException ignored) {
            }
        });
    }

    // urlProperty
    private final StringProperty urlProperty = new SimpleStringProperty(this, "url");
    public final StringProperty urlProperty() {
       return urlProperty;
    }
    public final String getUrl() {
       return urlProperty.get();
    }
    public final void setUrl(String value) {
        urlProperty.set(value);
    }

}
