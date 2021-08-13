/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents an integer array list that tries to avoid any heap memory allocation.
 * <p>
 * This class doesn't implement {@link Collection} because of the generic methods that cause integers
 * to be wrapped into {@link Integer}s, allocating memory in the heap.
 * <p>
 * This class implements {@link Iterable}. This allows to use the for syntax with this list.
 * The method {@link #spliterator()} and the methods that requires collections cause heap allocation.
 * (They wrap integers.)
 */
public class LowHeapIntArrayList implements Iterable<Integer> {

    private static final int DEFAULT_SIZE = 10;

    private int[] data;
    private int size;

    /**
     * Creates a new integer list.
     */
    public LowHeapIntArrayList() {
        data = null;
        size = 0;
    }

    /**
     * Creates a new integer list with the contents of the given list.
     *
     * @param list the list containing the data to copy.
     */
    public LowHeapIntArrayList(LowHeapIntArrayList list) {
        data = new int[list.size];
        size = list.size;
        System.arraycopy(list.data, 0, data, 0, size);
    }

    /**
     * Creates a new integer list with all the contents of the given array.
     *
     * @param data the array with the data.
     */
    public LowHeapIntArrayList(int... data) {
        this.data = new int[data.length];
        size = data.length;
        System.arraycopy(data, 0, this.data, 0, size);
    }

    /**
     * Returns the amount of elements inside this list.
     *
     * @return the amount of elements.
     */
    public int size() {
        return size;
    }

    /**
     * Returns whether this list is empty.
     *
     * @return whether this list has no elements.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns whether this list contains the given element.
     *
     * @param o the element.
     * @return whether this list contains the element.
     */
    public boolean contains(int o) {
        for (int i = 0; i < size; i++) {
            if (o == data[i]) return true;
        }
        return false;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new LowHeapIterator();
    }

    /**
     * Creates a new array containing all elements of this list.
     * <p>
     * This method allocates memory in the heap.
     *
     * @return the new array.
     */
    public int[] toArray() {
        var array = new int[size];
        System.arraycopy(data, 0, array, 0, size);
        return array;
    }

    /**
     * Copies the contents of this list into the given array if there's enough space.
     * If there's not enough space, this method creates a new array with the contents of the list,
     * allocating heap memory.
     *
     * @param a the given array.
     * @return the given array or the new array.
     */
    public int[] toArray(int[] a) {
        if (a.length >= size) {
            System.arraycopy(data, 0, a, 0, size);
            return a;
        } else {
            var array = new int[size];
            System.arraycopy(data, 0, array, 0, size);
            return array;
        }
    }

    /**
     * Inserts the given element into this list.
     * This method allocates heap memory if the array is not big enough.
     *
     * @param e the element to add.
     */
    public void add(int e) {
        if (data == null || data.length == size) recreate(size + 1);
        data[size++] = e;
    }

    /**
     * Removes the given element from the list.
     * <p>
     * If the elements is present several times in this list, this method removes only the first ocurrence.
     * Use {@link #removeAll(int)} to remove all ocurrences.
     *
     * @param o the element to remove.
     * @return whether any element has been removed.
     */
    public boolean remove(int o) {
        if (size == 0) return false;

        for (int i = 0; i < size; i++) {
            if (data[i] == o) {
                removeAt(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Removes the given element from the list.
     * Unlike {@link #remove(int)}, this method removes all ocurrences from this list.
     *
     * @param o the element to remove.
     * @return the amount elements that have been removed.
     */
    public int removeAll(int o) {
        if (size == 0) return 0;
        var amount = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] == o) {
                removeAt(i);
                amount++;
            }
        }
        return amount;
    }

    /**
     * Returns whether this list contains all the elements of the given collection.
     *
     * @param c the collection with the elements.
     * @return whether this list contains all the elements.
     */
    public boolean containsAll(Collection<Integer> c) {
        for (int o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    /**
     * Returns whether this list contains all the elements of the given list.
     *
     * @param list the list with the elements.
     * @return whether this list contains all the elements.
     */
    public boolean containsAll(LowHeapIntArrayList list) {
        for (int i = 0; i < list.size; i++) {
            if (!contains(list.data[i])) return false;
        }
        return true;
    }

    /**
     * Adds all elements of the given collection to this list.
     * <p>
     * The elements are added at the end of this list.
     *
     * @param c the collection.
     * @return whether this list was modified. (Whether the given collection is not empty.)
     */
    public boolean addAll(Collection<Integer> c) {
        if (c.isEmpty()) return false;
        if (data == null || data.length < size + c.size()) recreate(size + c.size());
        for (int e : c) {
            data[size++] = e;
        }
        return true;
    }

    /**
     * Adds all elements of the given list to this list.
     * <p>
     * The elements are added at the end of this list.
     *
     * @param list the list.
     * @return whether this list was modified. (Whether the given list is not empty.)
     */
    public boolean addAll(LowHeapIntArrayList list) {
        if (list.isEmpty()) return false;
        if (data == null || data.length < size + list.size) recreate(size + list.size);
        System.arraycopy(list.data, 0, data, size, list.size);
        size += list.size;
        return true;
    }

    /**
     * Adds all elements of the given collection to this list.
     * <p>
     * The elements are added at the given index.
     *
     * @param c     the collection.
     * @param index the index where the elements will be placed.
     * @return whether this list was modified. (Whether the given collection is not empty.)
     * @throws IndexOutOfBoundsException whether the given index is out of bounds.
     */
    public boolean addAll(int index, Collection<Integer> c) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(index);
        if (c.isEmpty()) return false;
        if (data == null || data.length < size + c.size()) recreate(size + c.size());

        // Make space
        System.arraycopy(data, index, data, index + c.size(), size - index);

        for (int e : c) {
            data[index++] = e;
        }
        size += c.size();
        return true;
    }

    /**
     * Adds all elements of the given list to this list.
     * <p>
     * The elements are added at the given index.
     *
     * @param list  the list.
     * @param index the index where the elements will be placed.
     * @return whether this list was modified. (Whether the given list is not empty.)
     * @throws IndexOutOfBoundsException whether the given index is out of bounds.
     */
    public boolean addAll(int index, LowHeapIntArrayList list) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(index);
        if (list.isEmpty()) return false;
        if (data == null || data.length < size + list.size) recreate(size + list.size);

        // Make space
        System.arraycopy(data, index, data, index + list.size, size - index);

        // Add data
        System.arraycopy(list.data, 0, data, index, list.size);

        size += list.size();
        return true;
    }

    /**
     * Removes all elements inside the given collection from this list.
     * <p>
     * Just like {@link #remove(int)}, this method removes only the first ocurrence of each element.
     *
     * @param c the collection.
     * @return whether this list was modified.
     */
    public boolean removeAll(Collection<Integer> c) {
        if (size == 0) return false;
        var oldSize = size;
        for (int o : c) {
            remove(o);
        }
        return size != oldSize;
    }


    /**
     * Removes all elements inside the given list from this list.
     * <p>
     * Just like {@link #remove(int)}, this method removes only the first coincidence of each element.
     *
     * @param list the given list.
     * @return whether this list was modified.
     */
    public boolean removeAll(LowHeapIntArrayList list) {
        if (size == 0) return false;
        var oldSize = size;
        for (int o : list.data) {
            remove(o);
        }
        return size != oldSize;
    }

    /**
     * Removes all elements of this list that are not present in the given collection.
     *
     * @param c the collection.
     * @return whether this list was modified.
     */
    public boolean retainAll(Collection<?> c) {
        if (size == 0) return false;
        var oldSize = size;
        for (int i = size - 1; i >= 0; i--) {
            if (!c.contains(data[i])) {
                removeAt(i);
            }
        }

        return oldSize != size;
    }


    /**
     * Removes all elements of this list that are not present in the given list.
     *
     * @param list the given list.
     * @return whether this list was modified.
     */
    public boolean retainAll(LowHeapIntArrayList list) {
        if (size == 0) return false;
        var oldSize = size;
        for (int i = size - 1; i >= 0; i--) {
            if (!list.contains(data[i])) {
                removeAt(i);
            }
        }

        return oldSize != size;
    }

    /**
     * Clears this list.
     * This method frees the managed array inside this instance.
     */
    public void clear() {
        data = null;
        size = 0;
    }

    /**
     * Returns the element in this list located at the given index.
     *
     * @param index the index.
     * @return the element.
     * @throws IndexOutOfBoundsException whether the given index is out of bounds.
     */
    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
        return data[index];
    }

    /**
     * Changes the value of the element at the given index to the given value.
     *
     * @param index   the index.
     * @param element the element.
     * @return the old value.
     * @throws IndexOutOfBoundsException whether the given index is out of bounds.
     */
    public int set(int index, int element) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
        var old = data[index];
        data[index] = element;
        return old;
    }

    /**
     * Adds the given value at the given index, shifting all values following it.
     * <p>
     * If the given index is equals to the size of the list, this method calls {@link #add(int)}.
     *
     * @param index   the index.
     * @param element the element.
     * @throws IndexOutOfBoundsException whether the given index is out of bounds.
     */
    public void add(int index, int element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(index);
        if (index == size) add(element);
        else {
            if (data.length == size) recreate(size + 1);
            System.arraycopy(data, index, data, index + 1, size - index);
            data[index] = element;
            size++;
        }
    }

    /**
     * Removes the element located at the given index.
     *
     * @param index the index.
     * @return the removed element.
     * @throws IndexOutOfBoundsException whether the given index is out of bounds.
     */
    public int removeAt(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
        var old = data[index];
        if (size - 2 - index >= 0) System.arraycopy(data, index + 1, data, index, size - 1 - index);
        size--;
        return old;
    }

    /**
     * Returns the index of the given element.
     * <p>
     * If the element is present several times in the list, this method returns the first ocurrence.
     * <p>
     * If the element is not found, this method returns -1.
     *
     * @param o the element.
     * @return the index or -1 if not found.
     */
    public int indexOf(int o) {
        for (int i = 0; i < size; i++) {
            if (data[i] == o) return i;
        }
        return -1;
    }


    /**
     * Returns the index of the given element.
     * <p>
     * If the element is present several times in the list, this method returns the last ocurrence.
     * <p>
     * If the element is not found, this method returns -1.
     *
     * @param o the element.
     * @return the index or -1 if not found.
     */
    public int lastIndexOf(int o) {
        for (int i = size - 1; i >= 0; i--) {
            if (data[i] == o) return i;
        }
        return -1;
    }

    private void recreate(int required) {
        if (data == null) {
            data = new int[Math.max(DEFAULT_SIZE, required << 1)];
        } else {
            var array = new int[Math.max(DEFAULT_SIZE, required << 1)];
            System.arraycopy(data, 0, array, 0, size);
            data = array;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (LowHeapIntArrayList) o;
        return size == that.size && Arrays.equals(data, 0, size, that.data, 0, size);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < size; i++) {
            hashCode = 31 * hashCode + data[i];
        }
        return hashCode;
    }

    private class LowHeapIterator implements Iterator<Integer> {

        private int index = 0;

        public boolean hasNext() {
            return index < size;
        }

        public Integer next() {
            if (index >= size) throw new IndexOutOfBoundsException(index);
            return data[index++];
        }

        @Override
        public void remove() {
            removeAt(index);
        }
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        var builder = new StringBuilder("[");
        for (int i = 0; i < size - 1; i++) {
            builder.append(data[i]).append(", ");
        }
        builder.append(data[size - 1]).append("]");
        return builder.toString();
    }
}
