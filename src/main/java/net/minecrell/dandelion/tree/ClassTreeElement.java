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
package net.minecrell.dandelion.tree;

import static com.google.common.base.Strings.emptyToNull;

import java.util.Optional;

public class ClassTreeElement {

    private static final String DEFAULT_PACKAGE = "(Default package)";

    protected final Optional<String> pack;

    public ClassTreeElement(String pack) {
        this.pack = Optional.ofNullable(emptyToNull(pack));
    }

    public Optional<String> getPackage() {
        return pack;
    }

    public String getPackageName() {
        return pack.orElse(DEFAULT_PACKAGE);
    }

    public String getPath() {
        if (pack.isPresent()) {
            return pack.get().replace('.', '/');
        } else {
            return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassTreeElement)) {
            return false;
        }

        ClassTreeElement that = (ClassTreeElement) o;
        return pack.equals(that.pack);
    }

    @Override
    public int hashCode() {
        return pack.hashCode();
    }

    @Override
    public String toString() {
        return getPackageName();
    }

}
