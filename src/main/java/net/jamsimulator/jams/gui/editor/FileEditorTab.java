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

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMenuBuilder;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.utils.AnchorUtils;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.Virtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FileEditorTab extends Tab implements ActionRegion {

	private FileEditorTabList list;
	private final File file;
	private final FileEditor display;
	private final Label name;
	private boolean saveMark;

	public FileEditorTab(FileEditorTabList list, File file) {
		this.list = list;
		this.file = file;
		this.saveMark = false;

		FileType type = Jams.getFileTypeManager().getByFile(file).orElse(Jams.getFileTypeManager().getUnknownType());
		this.display = type.createDisplayTab(this);

		ImageView view = new NearestImageView(type.getIcon(), FileType.IMAGE_SIZE, FileType.IMAGE_SIZE);
		name = new Label(file.getName(), view);
		setGraphic(name);

		if (display == null) return;

		Node element = (Node) display;
		AnchorPane pane = new AnchorPane();
		if (element instanceof Region) {
			((Region) element).prefWidthProperty().bind(pane.widthProperty());
			((Region) element).prefHeightProperty().bind(pane.heightProperty());
		}
		if (display instanceof Region && display instanceof Virtualized) {
			ScaledVirtualized scale = new ScaledVirtualized(element);
			VirtualizedScrollPane scroll = new VirtualizedScrollPane(scale);
			AnchorUtils.setAnchor(scroll, 0, 0, 0, 0);
			pane.getChildren().addAll(scroll);
			if (element instanceof VirtualScrollHandled) {
				((VirtualScrollHandled) element).setScrollPane(scroll);
				((VirtualScrollHandled) element).setZoom(scale);
			}
		} else {
			ScrollPane scroll = new ScrollPane(element);
			AnchorUtils.setAnchor(scroll, 0, 0, 0, 0);
			pane.getChildren().addAll(scroll);
		}

		setContent(pane);

		setOnClosed(target -> {
			this.list.closeFileInternal(this);
			display.onClose();
		});

		setContextMenu(createContextMenu());
	}

	public FileEditorTabList getList() {
		return list;
	}

	void setList(FileEditorTabList list) {
		this.list = list;
	}

	public WorkingPane getWorkingPane() {
		return list.getWorkingPane();
	}

	public File getFile() {
		return file;
	}

	public FileEditor getDisplay() {
		return display;
	}

	public boolean isSaveMark() {
		return saveMark;
	}

	public void setSaveMark(boolean saveMark) {
		if (saveMark == this.saveMark) return;
		this.saveMark = saveMark;
		name.setText(saveMark ? file.getName() + " *" : file.getName());
	}

	public void openInNewHolder(boolean horizontal) {
		list.getHolder().openInNewHolder(this, horizontal);
	}

	private Set<ContextAction> getSupportedContextActions() {
		Set<Action> actions = JamsApplication.getActionManager().getAll();
		Set<ContextAction> set = new HashSet<>();
		for (Action action : actions) {
			if (action instanceof ContextAction && supportsActionRegion(action.getRegionTag())) {
				set.add((ContextAction) action);
			}
		}
		return set;
	}

	private ContextMenu createContextMenu() {
		Set<ContextAction> set = getSupportedContextActions();
		if (set.isEmpty()) return null;
		return new ContextActionMenuBuilder(this).addAll(set).build();
	}

	@Override
	public boolean supportsActionRegion(String region) {
		return RegionTags.EDITOR_TAB.equals(region);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FileEditorTab that = (FileEditorTab) o;
		return file.equals(that.file);
	}

	@Override
	public int hashCode() {
		return Objects.hash(file);
	}
}
