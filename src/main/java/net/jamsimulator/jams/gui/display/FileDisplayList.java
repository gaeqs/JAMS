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

package net.jamsimulator.jams.gui.display;

import javafx.scene.control.TabPane;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileDisplayList extends TabPane {

	private WorkingPane workingPane;
	private final List<FileDisplayTab> displays;

	public FileDisplayList(WorkingPane workingPane) {
		this.workingPane = workingPane;
		this.displays = new ArrayList<>();
		setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		getStyleClass().add("file-display-list");
	}

	public void setWorkingPane(WorkingPane workingPane) {
		this.workingPane = workingPane;
	}

	public WorkingPane getWorkingPane() {
		return workingPane;
	}

	public Optional<FileDisplayTab> getFileDisplayTab(File file) {
		return displays.stream().filter(target -> target.getFile().equals(file)).findAny();
	}

	public Optional<FileDisplayTab> getSelected() {
		if (displays.isEmpty()) return Optional.empty();
		return Optional.of((FileDisplayTab) getSelectionModel().getSelectedItem());
	}


	public void selectNext() {
		getSelectionModel().selectNext();
	}

	public void selectPrevious() {
		getSelectionModel().selectPrevious();
	}

	public boolean isFileOpen(File file) {
		return displays.stream().anyMatch(target -> target.getFile().equals(file));
	}

	public boolean openFile(File file) {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");
		Validate.isTrue(file.isFile(), "File must be a file!");

		Optional<FileDisplayTab> optional = getFileDisplayTab(file);

		if (optional.isPresent()) {
			getSelectionModel().select(optional.get());
			return false;
		}

		FileDisplayTab tab = new FileDisplayTab(this, file);
		displays.add(tab);
		getTabs().add(tab);
		getSelectionModel().select(tab);
		return true;
	}

	public int closeFile(File file) {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");
		Validate.isTrue(file.isFile(), "File must be a file!");

		List<FileDisplayTab> tabs = displays.stream().filter(target -> target.getFile().equals(file)).collect(Collectors.toList());
		for (FileDisplayTab tab : tabs) {
			displays.remove(tab);
			getTabs().remove(tab);
		}
		return tabs.size();
	}

	void closeFileInternal(FileDisplayTab tab) {
		displays.remove(tab);
	}

	public void closeAll() {
		displays.clear();
		getTabs().clear();
	}
}
