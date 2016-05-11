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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.sun.istack.internal.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class PackageElement implements Comparable<PackageElement> {

    public enum Type {
        PACKAGE, CLASS, RESOURCE;

        public boolean isMember() {
            return this != PACKAGE;
        }
    }

    private static final char PATH_SEPARATOR = '/';
    private static final Joiner PATH_JOINER = Joiner.on(PATH_SEPARATOR);
    private static final Splitter PATH_SPLITTER = Splitter.on('/');

    private static final Joiner PACKAGE_JOINER = Joiner.on('.');
    private static final String DEFAULT_PACKAGE = "(Default package)";

    private final Type type;

    private final ImmutableList<String> packageElements;
    private final String packageName;

    private final Optional<String> memberName;
    private final String path;

    public PackageElement(Type type, ImmutableList<String> packageElements) {
        this(type, packageElements, null);
    }

    public PackageElement(Type type, ImmutableList<String> packageElements, @Nullable String memberName) {
        this.type = checkNotNull(type, "type");
        checkNotNull(packageElements, "packageElements");
        this.packageElements = packageElements;
        this.packageName = packageElements.isEmpty() ? DEFAULT_PACKAGE : PACKAGE_JOINER.join(packageElements);

        if (memberName != null) {
            this.memberName = Optional.of(memberName);

            StringBuilder builder = new StringBuilder();
            for (String element : packageElements) {
                builder.append(element).append(PATH_SEPARATOR);
            }
            builder.append(memberName);

            this.path = builder.toString();
        } else {
            checkArgument(this.type == Type.PACKAGE, "Member name must be specified");
            this.memberName = Optional.empty();
            this.path = PATH_JOINER.join(packageElements);
        }
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return this.memberName.orElse(this.packageName);
    }

    public Optional<String> getMemberName() {
        return this.memberName;
    }

    public String getPath() {
        return this.path;
    }

    public ImmutableList<String> getPackage() {
        return this.packageElements;
    }

    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public int compareTo(PackageElement element) {
        if (this.type != element.type) {
            return this.type.compareTo(element.type);
        }

        return this.path.compareTo(element.path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PackageElement)) {
            return false;
        }

        PackageElement that = (PackageElement) o;
        return this.packageElements.equals(that.packageElements)
                && this.memberName.equals(that.memberName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.packageElements, this.memberName);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static PackageElement fromPath(Type type, String path) {
        int pos = path.lastIndexOf('/');
        if (pos >= 0) {
            String pack = path.substring(0, pos);
            return new PackageElement(type, ImmutableList.copyOf(PATH_SPLITTER.split(pack)), path.substring(pos + 1));
        } else {
            return new PackageElement(type, ImmutableList.of(), path);
        }
    }

}
