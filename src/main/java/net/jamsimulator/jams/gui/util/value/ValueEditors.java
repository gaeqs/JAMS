/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.util.value;

import javafx.scene.text.Font;
import net.jamsimulator.jams.gui.mips.editor.MIPSSpaces;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ValueEditors {

	private static final Map<String, ValueEditor.Builder<?>> editorByName = new HashMap<>();
	private static final Map<Class<?>, ValueEditor.Builder<?>> editorByType = new HashMap<>();

	static {

		//ARCHITECTURE
		var architectureEditor = new ArchitectureValueEditor.Builder();
		editorByName.put(ArchitectureValueEditor.NAME, architectureEditor);
		editorByType.put(Architecture.class, architectureEditor);

		//BOOLEAN
		var booleanEditor = new BooleanValueEditor.Builder();
		editorByName.put(BooleanValueEditor.NAME, booleanEditor);
		editorByType.put(Boolean.class, booleanEditor);
		editorByType.put(boolean.class, booleanEditor);

		//DOUBLE
		var doubleEditor = new DoubleValueEditor.Builder();
		editorByName.put(DoubleValueEditor.NAME, doubleEditor);
		editorByType.put(Double.class, doubleEditor);
		editorByType.put(double.class, doubleEditor);

		//FLOAT
		var floatEditor = new FloatValueEditor.Builder();
		editorByName.put(FloatValueEditor.NAME, floatEditor);
		editorByType.put(Float.class, floatEditor);
		editorByType.put(float.class, floatEditor);

		//FONT
		var fontEditor = new FontValueEditor.Builder();
		editorByName.put(FontValueEditor.NAME, fontEditor);
		editorByType.put(Font.class, fontEditor);

		//INTEGER
		var integerEditor = new IntegerValueEditor.Builder();
		editorByName.put(IntegerValueEditor.NAME, integerEditor);
		editorByType.put(Integer.class, integerEditor);
		editorByType.put(int.class, integerEditor);

		//LANGUAGES
		var languageEditor = new LanguageValueEditor.Builder();
		editorByName.put(LanguageValueEditor.NAME, languageEditor);
		editorByType.put(Language.class, languageEditor);

		//MEMORY BUILDER
		var memoryBuilderEditor = new MemoryBuilderValueEditor.Builder();
		editorByName.put(MemoryBuilderValueEditor.NAME, memoryBuilderEditor);
		editorByType.put(MemoryBuilder.class, memoryBuilderEditor);

		//MIPS SPACES
		var mipsSpacesEditor = new MIPSSpacesValueEditor.Builder();
		editorByName.put(MIPSSpacesValueEditor.NAME, mipsSpacesEditor);
		editorByType.put(MIPSSpaces.class, mipsSpacesEditor);

		//POSITIVE INTEGER
		var positiveIntegerEditor = new PositiveIntegerValueEditor.Builder();
		editorByName.put(PositiveIntegerValueEditor.NAME, positiveIntegerEditor);

		//STRING
		var stringEditor = new StringValueEditor.Builder();
		editorByName.put(StringValueEditor.NAME, stringEditor);
		editorByType.put(String.class, stringEditor);

		//SYSCALL EXECUTION BUILDER BUNDLE
		var syscallExecutionBuilderBundleEditor = new SyscallExecutionBuilderBundleValueEditor.Builder();
		editorByName.put(SyscallExecutionBuilderBundleValueEditor.NAME, syscallExecutionBuilderBundleEditor);
		editorByType.put(SyscallExecutionBuilderBundle.class, syscallExecutionBuilderBundleEditor);

		//SYSCALL EXECUTION BUILDER
		var syscallExecutionBuilderEditor = new SyscallExecutionBuilderValueEditor.Builder();
		editorByName.put(SyscallExecutionBuilderValueEditor.NAME, syscallExecutionBuilderEditor);
		editorByType.put(SyscallExecutionBuilder.class, syscallExecutionBuilderEditor);

		//THEME
		var themeEditor = new ThemeValueEditor.Builder();
		editorByName.put(ThemeValueEditor.NAME, themeEditor);
		editorByType.put(Theme.class, themeEditor);
	}


	public static Optional<ValueEditor.Builder<?>> getByName(String name) {
		if (name == null) return Optional.empty();
		return Optional.ofNullable(editorByName.get(name.toLowerCase()));
	}

	public static <T> Optional<ValueEditor.Builder<T>> getByType(Class<T> clazz) {
		if (clazz == null) return Optional.empty();
		return Optional.ofNullable((ValueEditor.Builder<T>) editorByType.get(clazz));
	}

	public static <T> ValueEditor.Builder<T> getByTypeUnsafe(Class<T> clazz) {
		return getByType(clazz).orElse(null);
	}

	public static boolean add(Class<?> clazz, ValueEditor.Builder<?> editor) {
		return editorByType.putIfAbsent(clazz, editor) == null;
	}
}
