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

package net.jamsimulator.jams.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

    @SuppressWarnings("unchecked")
    public static <T, K> Map<T, K> deepCopy(Map<T, K> original) {
        Map<T, K> map = new HashMap<>();
        original.forEach((key, value) -> {
            if (value instanceof List) {
                map.put(key, (K) deepCopy((List<?>) value));
            } else if (value instanceof Map) {
                map.put(key, (K) deepCopy((Map<?, ?>) value));
            } else map.put(key, value);
        });
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> deepCopy(List<T> original) {
        List<T> list = new LinkedList<>();
        original.forEach(value -> {
            if (value instanceof List) {
                list.add((T) deepCopy((List<?>) value));
            } else if (value instanceof Map) {
                list.add((T) deepCopy((Map<?, ?>) value));
            } else list.add(value);
        });
        return list;
    }
}
