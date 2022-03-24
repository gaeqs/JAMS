/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.utils;

import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionUtilsTest {

    @Test
    void zipTest1() {
        var keys = List.of(2, 3, 4, 6);
        var values = List.of("b", "c", "d", "f");
        List<Pair<Integer, String>> expected = List.of(
                new Pair<>(2, "b"),
                new Pair<>(3, "c"),
                new Pair<>(4, "d"),
                new Pair<>(6, "f")
        );

        var result = CollectionUtils.zip(keys, values).toList();
        assertEquals(expected, result);
    }

    @Test
    void zipTest2() {
        var keys = List.of(2, 3, 4, 6, 8, 9);
        var values = List.of("b", "c", "d");
        List<Pair<Integer, String>> expected = List.of(
                new Pair<>(2, "b"),
                new Pair<>(3, "c"),
                new Pair<>(4, "d")
        );

        var result = CollectionUtils.zip(keys, values).toList();
        assertEquals(expected, result);
    }

    @Test
    void zipTest3() {
        var keys = List.of(2, 3);
        var values = List.of("b", "c", "d", "f");
        List<Pair<Integer, String>> expected = List.of(
                new Pair<>(2, "b"),
                new Pair<>(3, "c")
        );

        var result = CollectionUtils.zip(keys, values).toList();
        assertEquals(expected, result);
    }

    @Test
    void zipTest4() {
        var keys = List.of();
        var values = List.of("b", "c", "d", "f");
        List<Pair<Integer, String>> expected = Collections.emptyList();

        var result = CollectionUtils.zip(keys, values).toList();
        assertEquals(expected, result);
    }


}