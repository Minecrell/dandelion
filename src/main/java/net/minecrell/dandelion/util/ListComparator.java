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
