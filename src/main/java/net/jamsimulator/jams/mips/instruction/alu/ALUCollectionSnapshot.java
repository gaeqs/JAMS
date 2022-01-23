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

package net.jamsimulator.jams.mips.instruction.alu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ALUCollectionSnapshot extends AbstractList<ALU> {

    private final List<ALU> alus;

    public ALUCollectionSnapshot() {
        alus = List.of();
    }

    public ALUCollectionSnapshot(Collection<ALU> raw) {
        alus = List.copyOf(raw);
    }

    public ALUCollectionSnapshot(String json) {
        var list = new ArrayList<ALU>();
        var array = new JSONArray(json);

        array.forEach(it -> {
            if (it instanceof JSONObject o) {
                list.add(ALU.fromJSON(o));
            }
        });

        alus = Collections.unmodifiableList(list);
    }

    public JSONArray toJSON() {
        var list = new JSONArray();
        alus.forEach(it -> list.put(it.toJSON()));
        return list;
    }

    @Override
    public ALU get(int index) {
        return alus.get(index);
    }

    @Override
    public int size() {
        return alus.size();
    }
}
