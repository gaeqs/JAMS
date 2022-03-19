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

package net.jamsimulator.jams.mips.label;

import net.jamsimulator.jams.mips.assembler.AssemblerScope;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;
import java.util.function.Consumer;

public class Label {

    private final String key;
    private final AssemblerScope scope;

    private final String originFile;
    private final int originLine;

    private final Set<LabelReference> references;
    private int address;

    public Label(String key, AssemblerScope scope, int address, String originFile, int originLine) {
        Validate.notNull(key, "Key cannot be null!");
        Validate.notNull(scope, "Scope cannot be null!");
        Validate.notNull(originFile, "Origin file cannot be null!");

        this.key = key;
        this.scope = scope;
        this.address = address;
        this.originFile = originFile;
        this.originLine = originLine;
        this.references = new HashSet<>();
    }

    public Label(String key, AssemblerScope scope, int address, String originFile,
                 int originLine, Collection<LabelReference> references) {
        Validate.notNull(key, "Key cannot be null!");
        Validate.notNull(scope, "Scope cannot be null!");
        Validate.notNull(originFile, "Origin file cannot be null!");

        this.key = key;
        this.scope = scope;
        this.address = address;
        this.originFile = originFile;
        this.originLine = originLine;
        this.references = new HashSet<>(references);
    }

    public String getKey() {
        return key;
    }

    public AssemblerScope getScope() {
        return scope;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getOriginFile() {
        return originFile;
    }

    public int getOriginLine() {
        return originLine;
    }

    public Set<LabelReference> getReferences() {
        return Collections.unmodifiableSet(references);
    }

    public boolean addReference(LabelReference reference) {
        return references.add(reference);
    }

    public boolean removeReference(LabelReference reference) {
        return references.remove(reference);
    }

    public void forEachReference(Consumer<LabelReference> consumer) {
        references.forEach(consumer);
    }

    public Label withScope(AssemblerScope scope) {
        return new Label(key, scope, address, originFile, originLine, references);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return key.equals(label.key) && scope.equals(label.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, scope);
    }

    @Override
    public String toString() {
        return "Label{" +
                "key='" + key + '\'' +
                ", originFile='" + originFile + '\'' +
                ", address=" + address +
                '}';
    }
}
