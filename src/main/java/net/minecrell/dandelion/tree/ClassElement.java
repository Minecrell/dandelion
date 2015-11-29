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

import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class ClassElement extends ClassTreeElement {

    protected final String className;

    public ClassElement(String pack, String className) {
        super(pack);
        this.className = requireNonNull(className, "className");
    }

    public String getClassName() {
        return className;
    }

    public String getQualifiedName() {
        if (pack.isPresent()) {
            return pack.get() + '.' + className;
        } else {
            return className;
        }
    }

    @Override
    public String getPath() {
        String path = super.getPath();
        if (path.isEmpty()) {
            return getClassName();
        } else {
            return path + '/' + getClassName();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassElement)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ClassElement that = (ClassElement) o;
        return className.equals(that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pack, className);
    }

    @Override
    public String toString() {
        return getClassName();
    }

}
