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

package net.jamsimulator.jams.gui.mips.editor.element;

import net.jamsimulator.jams.gui.mips.editor.MIPSEditorError;
import net.jamsimulator.jams.project.mips.MIPSFilesToAssemble;
import net.jamsimulator.jams.utils.LabelUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MIPSLabel extends MIPSCodeElement {

	private boolean global;

	public MIPSLabel(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
		global = false;
	}


	@Override
	public String getSimpleText() {
		return text;
	}

	public String getLabel() {
		return text.substring(0, text.length() - 1).trim();
	}

	@Override
	public List<String> getStyles() {
		String style = global ? "mips-global-label" : "mips-label";
		if (hasErrors()) return Arrays.asList(style, "mips-error");
		return Collections.singletonList(style);
	}

	@Override
	public void refreshMetadata(MIPSFileElements elements) {
		String label = getLabel();

		global = elements.getSetAsGlobalLabel().contains(label);
		errors.clear();

		if (!LabelUtils.isLabelLegal(label)) {
			errors.add(MIPSEditorError.ILLEGAL_LABEL);
		} else if (elements.getLabels().amount(label) > 1) {
			errors.add(MIPSEditorError.DUPLICATE_LABEL);
		} else {
			MIPSFilesToAssemble filesToAssemble = elements.getFilesToAssemble().orElse(null);
			if (filesToAssemble == null) return;

			int amount = filesToAssemble.getGlobalLabels().amount(label);
			if (global) amount--;
			if (amount > 0) {
				errors.add(MIPSEditorError.DUPLICATE_GLOBAL_LABEL);
			}
		}
	}

}
