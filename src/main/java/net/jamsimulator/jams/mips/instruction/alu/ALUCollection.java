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

package net.jamsimulator.jams.mips.instruction.alu;

import javafx.util.Pair;

import java.util.*;

public class ALUCollection {

    private final List<ALU> alus = new ArrayList<>();
    private final SortedMap<Integer, ALU> availableALUs = new TreeMap<>();

    public ALUCollection(List<? extends ALU> alus) {
        this.alus.addAll(alus);

        int i = 0;
        for (ALU alu : alus) {
            availableALUs.put(i++, alu);
        }
    }

    private ALUCollection(List<? extends ALU> alus, SortedMap<Integer, ALU> available) {
        this.alus.addAll(alus);
        availableALUs.putAll(available);
    }

    public List<ALU> getAlus() {
        return alus;
    }

    public Optional<Pair<Integer, ALU>> requestALU(ALUType aluType) {
        Pair<Integer, ALU> result = null;
        for (var entry : availableALUs.entrySet()) {
            if (entry.getValue().type() == aluType) {
                result = new Pair<>(entry.getKey(), entry.getValue());
                break;
            }
        }

        if (result != null) {
            availableALUs.remove(result.getKey());
        }

        return Optional.ofNullable(result);
    }

    public void releaseALU(int index) {
        var alu = alus.get(index);
        availableALUs.put(index, alu);
    }

    public void reset() {
        int i = 0;
        for (ALU alu : alus) {
            availableALUs.put(i++, alu);
        }
    }

    public ALUCollection copy() {
        return new ALUCollection(alus, availableALUs);
    }

    public void restore(ALUCollection collection) {
        availableALUs.clear();
        availableALUs.putAll(collection.availableALUs);
    }
}
