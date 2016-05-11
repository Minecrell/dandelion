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

package net.minecrell.dandelion.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public final class ListComparator<T extends Comparable<T>> implements Comparator<Collection<T>> {

    private static final ListComparator INSTANCE = new ListComparator();

    private ListComparator() {
    }

    @Override
    public int compare(Collection<T> o1, Collection<T> o2) {
        Iterator<T> i1 = o1.iterator();
        Iterator<T> i2 = o2.iterator();

        while (true) {
            if (!i1.hasNext()) {
                return i2.hasNext() ? -1 : 0;
            } else if (!i2.hasNext()) {
                return 1;
            }

            int result = i1.next().compareTo(i2.next());
            if (result != 0) {
                return result;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> Comparator<Collection<T>> get() {
        return (ListComparator) INSTANCE;
    }

}
