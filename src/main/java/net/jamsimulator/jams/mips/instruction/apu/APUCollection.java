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

package net.jamsimulator.jams.mips.instruction.apu;

import java.util.*;

public class APUCollection {

    private final Set<APU> apus = new HashSet<>();
    private final SortedSet<APU> availableAPUs = new TreeSet<>();

    public APUCollection(Set<? extends APU> apus) {
        this.apus.addAll(apus);
        availableAPUs.addAll(apus);
    }

    private APUCollection(Set<? extends APU> apus, Set<APU> available) {
        this.apus.addAll(apus);
        availableAPUs.addAll(available);
    }

    public Set<APU> getApus() {
        return apus;
    }

    public Optional<APU> requestAPU(APUType apuType) {
        var optional = availableAPUs.stream()
                .filter(apu -> apu.type() == apuType)
                .findFirst();
        optional.ifPresent(availableAPUs::remove);
        return optional;
    }

    public void releaseAPU(APU apu) {
        if (apus.contains(apu)) {
            availableAPUs.add(apu);
        }
    }

    public void reset() {
        availableAPUs.clear();
        availableAPUs.addAll(apus);
    }

    public APUCollection copy() {
        return new APUCollection(apus, availableAPUs);
    }

    public void restore(APUCollection collection) {
        availableAPUs.clear();
        availableAPUs.addAll(collection.availableAPUs);
    }
}
