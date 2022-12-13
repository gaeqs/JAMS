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

package net.jamsimulator.jams.gui.util.value;

import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.util.Optional;

public class ValueEditorBuilderManager extends Manager<ValueEditor.Builder> {

    public static final String NAME = "value_editor_builder";
    public static final ValueEditorBuilderManager INSTANCE = new ValueEditorBuilderManager(ResourceProvider.JAMS, NAME);

    public ValueEditorBuilderManager(ResourceProvider provider, String name) {
        super(provider, name, ValueEditor.Builder.class, false);
    }


    @SuppressWarnings("unchecked")
    public <T> Optional<ValueEditor.Builder<T>> getByType(Class<T> clazz) {
        if (clazz == null) return Optional.empty();
        return (Optional<ValueEditor.Builder<T>>) stream().filter(it ->
                        it.getManagedType() != null && it.getManagedType().equals(clazz))
                .map(it -> (T) it).findAny();
    }

    public <T> ValueEditor.Builder<T> getByTypeUnsafe(Class<T> clazz) {
        return getByType(clazz).orElse(null);
    }

    @Override
    protected void loadDefaultElements() {
        add(new ALUCollectionSnapshotValueEditor.Builder());
        add(new ALUTypeValueEditor.Builder());
        add(new ALUValueEditor.Builder());
        add(new ArchitectureValueEditor.Builder());
        add(new BooleanValueEditor.Builder());
        add(new CacheBuilderValueEditor.Builder());
        add(new DoubleValueEditor.Builder());
        add(new FloatValueEditor.Builder());
        add(new FontValueEditor.Builder());
        add(new HexadecimalIntegerValueEditor.Builder());
        add(new IntegerValueEditor.Builder());
        add(new LanguageValueEditor.Builder());
        add(new MemoryBuilderValueEditor.Builder());
        add(new MIPSAssembledInstructionViewerOrderValueEditor.Builder());
        add(new MIPSSpacesValueEditor.Builder());
        add(new PositiveIntegerValueEditor.Builder());
        add(new Pow2ValueEditor.Builder());
        add(new RangedIntegerValueEditor.Builder());
        add(new StringValueEditor.Builder());
        add(new SyscallExecutionBuilderBundleValueEditor.Builder());
        add(new SyscallExecutionBuilderValueEditor.Builder());
        add(new ThemeValueEditor.Builder());
    }
}
