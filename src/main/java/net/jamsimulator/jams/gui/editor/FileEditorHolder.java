package net.jamsimulator.jams.gui.editor;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.utils.DraggingSupport;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.Optional;

/**
 * This class wraps {@link FileEditorTabList}s, allowing to split the editor
 * in different files. Instances of this class are structured using a tree composition.
 */
public class FileEditorHolder extends SplitPane {

	private final WorkingPane workingPane;
	private FileEditorHolder parent;

	private FileEditorTabList list;
	private FileEditorHolder first, second;
	private DraggingSupport draggingSupport;

	private FileEditor lastFocusedEditor;

	/**
	 * Creates the holder.
	 *
	 * @param workingPane the {@link WorkingPane}.
	 */
	public FileEditorHolder(WorkingPane workingPane) {
		this.list = new FileEditorTabList(this);
		getItems().add(list);
		this.workingPane = workingPane;
		SplitPane.setResizableWithParent(list, false);

		this.draggingSupport = new DraggingSupport();
		refreshSupport();
	}

	/**
	 * Creates the holder.
	 *
	 * @param workingPane the {@link WorkingPane}.
	 * @param list        the {@link FileEditorTabList list} of {@link FileEditorTab}.
	 */
	public FileEditorHolder(WorkingPane workingPane, FileEditorTabList list) {
		Validate.notNull(list, "List cannot be null!");
		this.list = list;
		this.workingPane = workingPane;
		list.setHolder(this);
		getItems().add(list);
		SplitPane.setResizableWithParent(list, false);

		this.draggingSupport = new DraggingSupport();
		refreshSupport();
	}

	/**
	 * Creates the holder.
	 *
	 * @param workingPane the {@link WorkingPane}.
	 * @param first       the first children.
	 * @param second      the second children.
	 * @param horizontal  whether the children should be split horizontally.
	 */
	public FileEditorHolder(WorkingPane workingPane, FileEditorHolder first, FileEditorHolder second, boolean horizontal) {
		Validate.notNull(first, "First cannot be null!");
		Validate.notNull(second, "Second cannot be null!");
		this.workingPane = workingPane;
		this.first = first;
		this.second = second;

		first.parent = this;
		second.parent = this;

		setOrientation(horizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL);
		getItems().add(first);
		getItems().add(second);
		SplitPane.setResizableWithParent(first, false);
		SplitPane.setResizableWithParent(second, false);
		refreshSupport();
	}

	/**
	 * Returns the {@link WorkingPane} this holder is inside of.
	 *
	 * @return the {@link WorkingPane}.
	 */
	public WorkingPane getWorkingPane() {
		return workingPane;
	}

	/**
	 * Returns the holder parent of this holder, or null.
	 *
	 * @return the parent or null.
	 */
	public FileEditorHolder getParentHolder() {
		return parent;
	}

	/**
	 * Returns the {@link FileEditorTabList} inside this holder, if present.
	 * This elements is not present if this holder has children.
	 *
	 * @return the {@link FileEditorTabList}, if present.
	 */
	public Optional<FileEditorTabList> getList() {
		return Optional.ofNullable(list);
	}

	/**
	 * Returns the first child, if present.
	 *
	 * @return the first child.
	 */
	public Optional<FileEditorHolder> getFirst() {
		return Optional.ofNullable(first);
	}

	/**
	 * Returns the second child, if present.
	 *
	 * @return the second child.
	 */
	public Optional<FileEditorHolder> getSecond() {
		return Optional.ofNullable(second);
	}

	/**
	 * Returns the last focused editor inside this holder.
	 *
	 * @return the last focused editor, if present.
	 */
	public Optional<FileEditor> getLastFocusedEditor() {
		if (parent != null) return parent.getLastFocusedEditor();
		return Optional.ofNullable(lastFocusedEditor);
	}

	/**
	 * Sets the last focused editor inside this holder. This editor may be null.
	 *
	 * @param lastFocusedEditor the editor or null.
	 */
	public void setLastFocusedEditor(FileEditor lastFocusedEditor) {
		if (parent != null) {
			parent.setLastFocusedEditor(lastFocusedEditor);
			return;
		}
		this.lastFocusedEditor = lastFocusedEditor;
	}

	/**
	 * Returns the {@link FileEditorTab} that matches the given {@link File}, if present.
	 *
	 * @param file    the {@link File}.
	 * @param fromTop whether this method should be executed from the root holder.
	 * @return the {@link FileEditorTab}, if present.
	 */
	public Optional<FileEditorTab> getFileDisplayTab(File file, boolean fromTop) {
		if (fromTop && parent != null) return parent.getFileDisplayTab(file, true);
		if (list != null) return list.getFileDisplayTab(file);


		Optional<FileEditorTab> optional = first == null ? Optional.empty() : first.getFileDisplayTab(file, false);
		return optional.isPresent() ? optional : second == null ? optional : second.getFileDisplayTab(file, false);
	}

	/**
	 * Opens the given file.
	 *
	 * @param file the file.
	 * @return whether the file was opened. This is {@code false} whether the file was already open in this holder, a children or a parent.
	 */
	public boolean openFile(File file) {
		if (list == null) return first.openFile(file);

		Optional<FileEditorTab> optional = getFileDisplayTab(file, true);
		if (optional.isPresent()) {
			optional.get().getList().getSelectionModel().select(optional.get());
			return false;
		}

		return list.openFile(file);
	}


	/**
	 * Closes the given file if a {@link FileEditorTab} representing it is inside this holder or inside
	 * one if its children.
	 *
	 * @param file    the file to close.
	 * @param fromTop if this method should be executed from the root holder.
	 * @return the amount of files closed. Usually one.
	 */
	public int closeFile(File file, boolean fromTop) {
		if (parent == null || !fromTop) {
			if (list == null) {
				int i = first == null ? 0 : first.closeFile(file, false);
				if (second == null) return i;
				return i + second.closeFile(file, false);
			}
			int i = list.closeFile(file);
			if (list.isEmpty() && parent != null) {
				parent.removeChild(this);
			}
			return i;
		}
		return parent.closeFile(file, true);
	}

	/**
	 * Closes all files from this holder and its children.
	 *
	 * @param fromTop if this method should be executed from the root holder.
	 */
	public void closeAll(boolean fromTop) {
		if (parent == null || !fromTop) {
			if (list != null) {
				list.closeAll();
				return;
			}
			if (first != null) {
				first.closeAll(false);
			}
			if (second != null) {
				second.closeAll(false);
			}
			if (parent != null) {
				parent.removeChild(this);
			}
		} else parent.closeAll(true);
	}

	/**
	 * Saves all files inside this holder and its children.
	 *
	 * @param fromTop if this method should be executed from the root holder.
	 */
	public void saveAll(boolean fromTop) {
		if (parent == null || !fromTop) {
			if (list != null) {
				list.saveAll();
				return;
			}
			if (first != null) {
				first.saveAll(false);
			}
			if (second != null) {
				second.saveAll(false);
			}
		} else parent.saveAll(true);
	}

	/**
	 * Opens the given display in a new holder.
	 * <p>
	 * This should be called by the given {@link FileEditorTab}.
	 *
	 * @param display    the display.
	 * @param horizontal whether should be displayed horizontally.
	 */
	void openInNewHolder(FileEditorTab display, boolean horizontal) {
		if (list != null) {
			if (!list.contains(display) || list.size() == 1) return;
			list.removeTabSimple(display);

			getItems().clear();
			first = new FileEditorHolder(workingPane, list);
			second = new FileEditorHolder(workingPane);

			second.list.addTabSimple(display);

			first.parent = this;
			second.parent = this;
			list = null;

			getItems().add(first);
			getItems().add(second);
			setOrientation(horizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL);
			SplitPane.setResizableWithParent(first, false);
			SplitPane.setResizableWithParent(second, false);
			refreshSupport();
			setDividerPosition(0, 0.5);
			Platform.runLater(this::layoutAllDisplays);
		} else {
			(first == null ? second : first).openInNewHolder(display, horizontal);
		}
	}

	/**
	 * Checks if the list of this holder is empty. If true, this holder is removed.
	 * This doesn't work if this holder has no list.
	 */
	void checkIfEmpty() {
		if (list != null && list.isEmpty() && parent != null) {
			parent.removeChild(this);
		}
	}


	/**
	 * Removes the given child. This should be used by the child to be removed.
	 *
	 * @param holder the child to be removed.
	 */
	private void removeChild(FileEditorHolder holder) {
		if (holder == first) {
			getItems().remove(first);
			first = second;
			second = null;
		} else if (holder == second) {
			getItems().remove(second);
			second = null;
		} else return;
		if (first == null) {
			if (parent == null) {
				list = new FileEditorTabList(this);
				list.setHolder(this);
				getItems().add(list);
				SplitPane.setResizableWithParent(list, false);
			} else {
				parent.removeChild(this);
			}
		}
	}

	private void refreshSupport() {
		if (list != null && draggingSupport != null) {
			draggingSupport.addSupport(list);
		}
		if (first != null) {
			first.draggingSupport = draggingSupport;
			first.refreshSupport();
		}
		if (second != null) {
			second.draggingSupport = draggingSupport;
			second.refreshSupport();
		}
	}

	private void layoutAllDisplays() {
		if (list != null) list.layoutAllDisplays();
		if (first != null) first.layoutAllDisplays();
		if (second != null) second.layoutAllDisplays();
	}
}
