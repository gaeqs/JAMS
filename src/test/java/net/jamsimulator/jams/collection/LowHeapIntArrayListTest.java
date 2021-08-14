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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LowHeapIntArrayListTest {

    @Test
    void constructor1() {
        var list = new LowHeapIntArrayList();
        assertTrue(list.isEmpty());
    }


    @Test
    void constructor2() {
        var list = new LowHeapIntArrayList(2, 4, 5, 6, 2);
        assertFalse(list.isEmpty());
        assertEquals(5, list.size());
    }


    @Test
    void constructor3() {
        var list = new LowHeapIntArrayList(2, 4, 5, 6, 2);
        var other = new LowHeapIntArrayList(list);
        assertFalse(other.isEmpty());
        assertEquals(5, other.size());
    }

    @Test
    void size() {
        var list = new LowHeapIntArrayList(2, 3, 4, 7, 3);
        assertEquals(5, list.size());
        list = new LowHeapIntArrayList();
        assertEquals(0, list.size());
        list = new LowHeapIntArrayList(new int[30]);
        assertEquals(30, list.size());
    }

    @Test
    void isEmpty() {
        var list = new LowHeapIntArrayList(2, 3, 4, 7, 3);
        assertFalse(list.isEmpty());
        list = new LowHeapIntArrayList();
        assertTrue(list.isEmpty());
        list = new LowHeapIntArrayList(new int[30]);
        assertFalse(list.isEmpty());
    }

    @Test
    void contains() {
        var list = new LowHeapIntArrayList(2, 3, 5, 6);
        assertTrue(list.contains(3));
        assertFalse(list.contains(10));
        list.add(10);
        assertTrue(list.contains(3));
        assertTrue(list.contains(10));
        list.remove(3);
        assertFalse(list.contains(3));
        assertTrue(list.contains(10));
        list.add(10);
        list.remove(10);
        assertTrue(list.contains(10));
        list.remove(10);
        assertFalse(list.contains(10));
    }

    @Test
    void iterator() {
        int[] array = new int[]{2, 4, 6, 2, 1, 10, 2};
        var list = new LowHeapIntArrayList(array);
        var i = 0;
        for (int value : list) {
            assertEquals(array[i++], value);
        }
    }

    @Test
    void toArray() {
        int[] array = new int[]{2, 4, 6, 2, 1, 10, 2};
        var list = new LowHeapIntArrayList(array);
        assertArrayEquals(array, list.toArray());
    }

    @Test
    void toArray1() {
        int[] array = new int[]{2, 4, 6, 2, 1, 10, 2};
        var list = new LowHeapIntArrayList(array);

        var newArray = list.toArray(new int[0]);
        assertArrayEquals(array, newArray);

        var intArray = new int[]{2, 4, 6, 2, 1, 10, 2};
        newArray = list.toArray(intArray);
        assertSame(intArray, newArray);

        intArray = new int[]{2, 3, 4};
        newArray = list.toArray(intArray);
        assertNotSame(intArray, newArray);
    }

    @Test
    void add() {
        var list = new LowHeapIntArrayList(2, 3, 5, 6);
        var expected = new LowHeapIntArrayList(2, 3, 5, 6, 10);
        list.add(10);
        assertEquals(expected, list);
    }


    @Test
    void add2() {
        var list = new LowHeapIntArrayList(2, 3, 5, 6);
        var expected = new LowHeapIntArrayList(10, 2, 3, 10, 5, 6, 10);
        list.add(10);
        list.add(0, 10);
        list.add(3, 10);
        assertEquals(expected, list);
    }

    @Test
    void remove() {
        var list = new LowHeapIntArrayList(2, 3, 5, 6, 3);
        var expected = new LowHeapIntArrayList(2, 5, 6, 3);
        assertTrue(list.remove(3));
        assertEquals(expected, list);
        assertFalse(list.remove(10));
        assertEquals(expected, list);
    }

    @Test
    void containsAll() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6);
        var other = new LowHeapIntArrayList(2, 3);
        var other2 = new LowHeapIntArrayList(3, 6);
        assertFalse(list.containsAll(other));
        assertTrue(list.containsAll(other2));
    }

    @Test
    void containsAll2() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6);
        var other = List.of(2, 3);
        var other2 = List.of(3, 6);
        assertFalse(list.containsAll(other));
        assertTrue(list.containsAll(other2));
    }

    @Test
    void addAll() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6);
        var other = new LowHeapIntArrayList(2, 3);
        var expected = new LowHeapIntArrayList(3, 4, 5, 6, 2, 3);
        assertTrue(list.addAll(other));
        assertEquals(expected, list);
    }

    @Test
    void addAll2() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6);
        var other = List.of(2, 3);
        var expected = new LowHeapIntArrayList(3, 4, 5, 6, 2, 3);
        assertTrue(list.addAll(other));
        assertEquals(expected, list);
    }

    @Test
    void addAll3() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6);
        var other = new LowHeapIntArrayList(2, 3);
        var expected = new LowHeapIntArrayList(3, 4, 2, 3, 5, 6);
        assertTrue(list.addAll(2, other));
        assertEquals(expected, list);

        // Test edge cases
        list = new LowHeapIntArrayList(3, 4, 5, 6);
        expected = new LowHeapIntArrayList(3, 4, 5, 6, 2, 3);
        assertTrue(list.addAll(4, other));
        assertEquals(expected, list);

        list = new LowHeapIntArrayList(3, 4, 5, 6);
        expected = new LowHeapIntArrayList(2, 3, 3, 4, 5, 6);
        assertTrue(list.addAll(0, other));
        assertEquals(expected, list);
    }

    @Test
    void addAll4() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6);
        var other = List.of(2, 3);
        var expected = new LowHeapIntArrayList(3, 4, 2, 3, 5, 6);
        assertTrue(list.addAll(2, other));
        assertEquals(expected, list);

        // Test edge cases
        list = new LowHeapIntArrayList(3, 4, 5, 6);
        expected = new LowHeapIntArrayList(3, 4, 5, 6, 2, 3);
        assertTrue(list.addAll(4, other));
        assertEquals(expected, list);

        list = new LowHeapIntArrayList(3, 4, 5, 6);
        expected = new LowHeapIntArrayList(2, 3, 3, 4, 5, 6);
        assertTrue(list.addAll(0, other));
        assertEquals(expected, list);
    }

    @Test
    void removeAll() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6, 3);
        var other = new LowHeapIntArrayList(2, 3);
        var expected = new LowHeapIntArrayList(4, 5, 6, 3);
        assertTrue(list.removeAll(other));
        assertEquals(expected, list);
    }

    @Test
    void removeAll2() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6, 3);
        var other = List.of(2, 3);
        var expected = new LowHeapIntArrayList(4, 5, 6, 3);
        assertTrue(list.removeAll(other));
        assertEquals(expected, list);
    }


    @Test
    void removeAll3() {
        var list = new LowHeapIntArrayList(3, 4, 3, 5, 6, 3);
        var expected = new LowHeapIntArrayList(4, 5, 6);
        assertEquals(3, list.removeAll(3));
        assertEquals(expected, list);
    }

    @Test
    void retainAll() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6, 4);
        var other = new LowHeapIntArrayList(6, 3);
        var expected = new LowHeapIntArrayList(3, 6);
        assertTrue(list.retainAll(other));
        assertEquals(expected, list);
    }

    @Test
    void retainAll2() {
        var list = new LowHeapIntArrayList(3, 4, 5, 6, 4);
        var other = List.of(6, 3);
        var expected = new LowHeapIntArrayList(3, 6);
        assertTrue(list.retainAll(other));
        assertEquals(expected, list);
    }

    @Test
    void clear() {
        var list = new LowHeapIntArrayList(2, 4, 6, 2);
        assertFalse(list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    void get() {
        var array = new int[]{2, 8, 2, 4, 1};
        var list = new LowHeapIntArrayList(array);
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], list.get(i));
        }
    }

    @Test
    void set() {
        var list = new LowHeapIntArrayList(2, 4, 6, 2);
        var expected = new LowHeapIntArrayList(2, 4, 20, 2);
        list.set(2, 20);
        assertEquals(expected, list);
    }

    @Test
    void removeAt() {
        var list = new LowHeapIntArrayList(2, 4, 6, 2);
        var expected = new LowHeapIntArrayList(2, 4, 2);
        assertEquals(6, list.removeAt(2));
        assertEquals(expected, list);
    }

    @Test
    void indexOf() {
        var list = new LowHeapIntArrayList(2, 4, 6, 2, 6);
        assertEquals(2, list.indexOf(6));
        assertEquals(-1, list.indexOf(10));
    }

    @Test
    void lastIndexOf() {
        var list = new LowHeapIntArrayList(2, 4, 6, 2, 6);
        assertEquals(4, list.lastIndexOf(6));
        assertEquals(-1, list.indexOf(10));
    }

    @Test
    void testEquals() {
        var list = new LowHeapIntArrayList(2, 4, 6, 2, 6);
        var other = new LowHeapIntArrayList(2, 4, 6, 2, 6);
        var other2 = new LowHeapIntArrayList(2, 4, 6, 2, 7);
        var other3 = new LowHeapIntArrayList(2, 4, 6, 2);
        var other4 = new LowHeapIntArrayList(2, 4, 6, 2, 6, 7);
        assertEquals(list, other);
        assertNotEquals(list, other2);
        assertNotEquals(list, other3);
        assertNotEquals(list, other4);
    }

    @Test
    void testHashCode() {
        var array = new int[]{2, 9, 3, 2, 10};
        var list = new LowHeapIntArrayList(array);
        list.add(20);
        list.remove(20);
        assertEquals(Arrays.hashCode(array), list.hashCode());
    }
}