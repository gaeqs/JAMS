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

package net.jamsimulator.jams.gui.editor;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a list of {@link FileEditorTab}s.
 * This list should be handled by a {@link FileEditorHolder}.
 */
public class FileEditorTabList extends TabPane {

	private FileEditorHolder holder;
	private double relativeDragPosition;

	/**
	 * Creates the list.
	 *
	 * @param holder the {@link FileEditorHolder} that handles this list.
	 */
	public FileEditorTabList(FileEditorHolder holder) {
		this.holder = holder;
		setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		getStyleClass().add("file-editor-list");

		Platform.runLater(this::checkIfDraggable);

		getTabs().addListener((ListChangeListener<Tab>) change -> {
			while (change.next()) {
				if (change.wasRemoved()) {
					Platform.runLater(this::refreshList);
				}
				if (change.wasAdded()) {
					for (Tab tab : change.getAddedSubList()) {
						if (tab instanceof FileEditorTab) {
							((FileEditorTab) tab).setList(this);
						}
					}
				}
			}
		});

		setOnMousePressed(event -> relativeDragPosition = event.getY());
		setOnMouseDragged(event -> {
			FileEditorHolder holderToMove = getHolder();
			Region child = this;
			while (holderToMove != null && (holderToMove.getItems().size() < 2 || holderToMove.getOrientation() != Orientation.VERTICAL)) {
				child = holderToMove;
				holderToMove = holderToMove.getParentHolder();
			}
			if (holderToMove == null || holderToMove.getOrientation() != Orientation.VERTICAL) return;

			int index = holderToMove.getItems().indexOf(child);
			if (index < 1) return;

			double absolute = event.getSceneY();
			double min = holderToMove.getLocalToSceneTransform().getTy();
			double max = min + holderToMove.getHeight();
			double relative = (absolute - min - relativeDragPosition) / (max - min);
			if (relative > 0.9) relative = 0.9;
			holderToMove.setDividerPosition(index - 1, relative);
		});
	}


	/**
	 * Returns the {@link FileEditorHolder} handling this list.
	 *
	 * @return the {@link FileEditorHolder}.
	 */
	public FileEditorHolder getHolder() {
		return holder;
	}

	/**
	 * Sets the {@link FileEditorHolder} that handles this list.
	 * This method should be used only by the {@link FileEditorHolder}.
	 *
	 * @param holder the {@link FileEditorHolder}.
	 */
	void setHolder(FileEditorHolder holder) {
		this.holder = holder;
		Platform.runLater(this::checkIfDraggable);
	}

	/**
	 * Returns the {@link WorkingPane} of the handling {@link FileEditorHolder}.
	 *
	 * @return the {@link WorkingPane}.
	 */
	public WorkingPane getWorkingPane() {
		return holder.getWorkingPane();
	}

	/**
	 * Returns the {@link FileEditorTab} that matches the given {@link File}, if present.
	 *
	 * @param file the {@link File}.
	 * @return the {@link FileEditorTab}, if present.
	 */
	public Optional<FileEditorTab> getFileDisplayTab(File file) {
		return getTabs().stream().filter(target -> target instanceof FileEditorTab)
				.filter(target -> ((FileEditorTab) target).getFile().equals(file))
				.map(target -> (FileEditorTab) target)
				.findAny();
	}

	/**
	 * Returns the selected {@link FileEditorTab} of this list, if present.
	 *
	 * @return the selected {@link FileEditorTab}.
	 */
	public Optional<FileEditorTab> getSelected() {
		if (getTabs().isEmpty()) return Optional.empty();
		return Optional.of((FileEditorTab) getSelectionModel().getSelectedItem());
	}

	/**
	 * Selects the {@link FileEditorTab} at the right of the selected one.
	 */
	public void selectNext() {
		getSelectionModel().selectNext();
	}

	/**
	 * Selects the {@link FileEditorTab} at the left of the selected one.
	 */
	public void selectPrevious() {
		getSelectionModel().selectPrevious();
	}

	/**
	 * Returns whether this list is empty.
	 *
	 * @return whether this list is empty.
	 */
	public boolean isEmpty() {
		return getTabs().isEmpty();
	}

	/**
	 * Returns the amount of {@link FileEditorTab} inside this list.
	 *
	 * @return the amount.
	 */
	public int size() {
		return getTabs().size();
	}

	/**
	 * Returns whether this list contains the given {@link FileEditorTab}.
	 *
	 * @param tab the {@link FileEditorTab} to check.
	 * @return whether this list contains the given {@link FileEditorTab}.
	 */
	public boolean contains(FileEditorTab tab) {
		return getTabs().contains(tab);
	}

	/**
	 * Returns if there's any {@link FileEditorTab} inside this list that matches the given {@link File}.
	 *
	 * @param file the {@link File}.
	 * @return if there's any {@link FileEditorTab} inside this list that matches the given {@link File}.
	 */
	public boolean isFileOpen(File file) {
		return getTabs().stream().anyMatch(target -> target instanceof FileEditorTab
				&& ((FileEditorTab) target).getFile().equals(file));
	}

	/**
	 * Closes all {@link FileEditorTab} inside this list.
	 */
	public void closeAll() {
		getTabs().stream().filter(target -> target instanceof FileEditorTab)
				.forEach(target -> ((FileEditorTab) target).getDisplay().save());
		getTabs().clear();
		refreshList();
	}

	/**
	 * Saves all {@link FileEditorTab} inside this list.
	 */
	public void saveAll() {
		getTabs().stream().filter(target -> target instanceof FileEditorTab)
				.forEach(display -> ((FileEditorTab) display).getDisplay().save());
	}

	/**
	 * Opens the given file.
	 *
	 * @param file the file.
	 * @return whether the file was not present already and could be open.
	 */
	boolean openFile(File file) {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");
		Validate.isTrue(file.isFile(), "File must be a file!");

		Optional<FileEditorTab> optional = getFileDisplayTab(file);

		if (optional.isPresent()) {
			getSelectionModel().select(optional.get());
			return false;
		}

		FileEditorTab tab = new FileEditorTab(this, file);
		getTabs().add(tab);
		getSelectionModel().select(tab);
		return true;
	}

	/**
	 * Closes all {@link FileEditorTab} that matches the given file.
	 *
	 * @param file the file.
	 * @return the amount of closed files. This value should be one.
	 */
	int closeFile(File file) {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");
		Validate.isTrue(file.isFile(), "File must be a file!");

		List<FileEditorTab> tabs = getTabs().stream()
				.filter(target -> target instanceof FileEditorTab)
				.filter(target -> ((FileEditorTab) target).getFile().equals(file))
				.map(target -> (FileEditorTab) target)
				.collect(Collectors.toList());
		for (FileEditorTab tab : tabs) {
			tab.getDisplay().save();
			getTabs().remove(tab);
		}
		return tabs.size();
	}

	/**
	 * This method should be called when the {@link FileEditorTab} is closed by the user.
	 *
	 * @param tab the {@link FileEditorTab}.
	 */
	void closeFileInternal(FileEditorTab tab) {
		tab.getDisplay().save();
		refreshList();
	}

	/**
	 * This method should be called when the {@link FileEditorTab} is moved to another {@link FileEditorHolder}.
	 *
	 * @param tab the {@link FileEditorTab}.
	 */
	void removeTabSimple(FileEditorTab tab) {
		getTabs().remove(tab);
	}

	/**
	 * This method should be called when the {@link FileEditorTab} is moved from another {@link FileEditorHolder}.
	 *
	 * @param tab the {@link FileEditorTab}.
	 */
	void addTabSimple(FileEditorTab tab) {
		getTabs().add(tab);
		tab.setList(this);
	}

	private void refreshList() {
		if (getTabs().isEmpty()) {
			holder.checkIfEmpty();
		}
	}

	private void checkIfDraggable() {
		FileEditorHolder holderToMove = holder;
		Region child = this;
		while (holderToMove != null && (holderToMove.getItems().size() < 2 || holderToMove.getOrientation() != Orientation.VERTICAL)) {
			child = holderToMove;
			holderToMove = holderToMove.getParentHolder();
		}

		if (holderToMove == null || holderToMove.getOrientation() != Orientation.VERTICAL) return;
		int index = holderToMove.getItems().indexOf(child);

		if (index >= 1) {
			if (!getStyleClass().contains("draggable-file-editor-list")) {
				getStyleClass().add("draggable-file-editor-list");
			}
			setCursor(Cursor.N_RESIZE);
		} else {
			getStyleClass().remove("draggable-file-editor-list");
			setCursor(Cursor.DEFAULT);
		}
	}
}
