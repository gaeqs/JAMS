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

package net.jamsimulator.jams.gui.mips.display.element;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.mips.display.MipsDisplayError;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.utils.LabelUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayLabel extends MipsCodeElement {

	private boolean global;

	public DisplayLabel(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
		global = false;
	}

	@Override
	public List<String> getStyles() {
		String style = global ? "mips-global-label" : "mips-label";
		if (hasErrors()) return Arrays.asList(style, "mips-error");
		return Collections.singletonList(style);
	}

	public String getLabel() {
		return text.substring(0, text.length() - 1).trim();
	}

	public boolean isGlobal() {
		return global;
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		errors.clear();

		String label = getLabel();
		//Illegal label
		if (label.isEmpty() || !LabelUtils.isLabelLegal(label)) {
			errors.add(MipsDisplayError.ILLEGAL_LABEL);
			return;
		}

		if (elements.labelCount(label) > 1) {
			errors.add(MipsDisplayError.DUPLICATE_LABEL);
		}
	}

	/**
	 * Searches for label error inside this element.
	 *
	 * @param labels       the labels declared in the file.
	 * @param globalLabels the global labels.
	 * @return whether the errors have been modified.
	 */
	public boolean searchLabelErrors(List<String> labels, List<String> globalLabels) {
		String label = getLabel();

		boolean changed = false;
		if (labels.stream().filter(target -> target.equals(label)).count() > 1) {
			if (!errors.contains(MipsDisplayError.DUPLICATE_LABEL)) {
				changed = errors.add(MipsDisplayError.DUPLICATE_LABEL);
			}
		} else {
			if (errors.contains(MipsDisplayError.DUPLICATE_LABEL)) {
				changed = errors.remove(MipsDisplayError.DUPLICATE_LABEL);
			}
		}

		if (globalLabels.stream().filter(target -> target.equals(label)).count() > 1) {
			if (!errors.contains(MipsDisplayError.DUPLICATE_GLOBAL_LABEL)) {
				changed = errors.add(MipsDisplayError.DUPLICATE_GLOBAL_LABEL);
			}
		} else {
			if (errors.contains(MipsDisplayError.DUPLICATE_GLOBAL_LABEL)) {
				changed = errors.remove(MipsDisplayError.DUPLICATE_GLOBAL_LABEL);
			}
		}
		return changed;
	}

	@Override
	public void populatePopup(VBox popup) {
		populatePopupWithErrors(popup);
	}

	@Override
	public void populatePopupWithErrors(VBox popup) {
		Language language = Jams.getLanguageManager().getSelected();

		errors.forEach(target -> {
			String message = language.getOrDefault("EDITOR_MIPS_ERROR_" + target);
			popup.getChildren().add(new Label(message.replace("{TEXT}", getLabel())));
		});
	}

	public boolean checkGlobalLabelsChanges(List<String> fileGlobalLabels) {
		boolean isNowGlobal = fileGlobalLabels.contains(getLabel());
		boolean hasGlobalChanged = isNowGlobal != global;
		global = isNowGlobal;
		return hasGlobalChanged;
	}
}
