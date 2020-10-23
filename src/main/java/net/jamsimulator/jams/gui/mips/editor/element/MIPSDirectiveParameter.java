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
import net.jamsimulator.jams.mips.directive.defaults.DirectiveExtern;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveLab;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.project.mips.MIPSFilesToAssemble;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MIPSDirectiveParameter extends MIPSCodeElement {

	protected final MIPSDirective directive;
	protected final int index;
	private final boolean string;
	private boolean registeredLabel, globalLabel;

	public MIPSDirectiveParameter(MIPSLine line, MIPSDirective directive, int index, int startIndex, int endIndex, String text) {
		super(line, startIndex, endIndex, text);
		this.directive = directive;
		this.index = index;
		this.string = StringUtils.isStringOrChar(text);
		this.registeredLabel = false;
		registerLabelsIfRequired();
	}

	public int getIndex() {
		return index;
	}

	public DirectiveParameterType getType() {
		if (directive != null) {
			return directive.getDirective().getParameterTypeFor(index);
		}
		return DirectiveParameterType.getAllCandidates(text).stream().findAny().orElse(null);
	}

	public MIPSDirective getDirective() {
		return directive;
	}

	@Override
	public String getSimpleText() {
		return text;
	}

	@Override
	public List<String> getStyles() {
		String style;

		if (registeredLabel) {
			style = globalLabel ? "mips-global-label" : "mips-label";
		} else {
			style = string ? "mips-directive-parameter-string" : "mips-directive-parameter";
		}

		if (hasErrors()) return Arrays.asList(style, "mips-error");
		return Collections.singletonList(style);
	}

	@Override
	public void refreshMetadata(MIPSFileElements elements) {
		errors.clear();
		var directive = this.directive.getDirective();
		if (directive == null) return;

		if (!directive.isParameterValidInContext(index, text, elements)) {
			errors.add(MIPSEditorError.INVALID_DIRECTIVE_PARAMETER);
		}

		if (registeredLabel) {
			if (elements.getLabels().amount(text) > 1) {
				errors.add(MIPSEditorError.DUPLICATE_LABEL);
			} else {
				MIPSFilesToAssemble filesToAssemble = elements.getFilesToAssemble().orElse(null);
				if (filesToAssemble == null) return;

				globalLabel = elements.getSetAsGlobalLabel().contains(text);

				int amount = filesToAssemble.getGlobalLabels().amount(text);
				if (globalLabel) amount--;
				if (amount > 0) {
					errors.add(MIPSEditorError.DUPLICATE_GLOBAL_LABEL);
				}
			}
		}
	}

	private void registerLabelsIfRequired() {
		var directive = this.directive.getDirective();
		if (directive instanceof DirectiveLab || directive instanceof DirectiveExtern) {
			if (index == 0) {
				globalLabel = directive instanceof DirectiveExtern;
				registerLabel(text, globalLabel);
				registeredLabel = true;
			}
		} else {
			var type = directive.getParameterTypeFor(index);
			if (type != null && type.mayBeLabel()) {
				markUsedLabel(text);
			}
		}
	}
}
