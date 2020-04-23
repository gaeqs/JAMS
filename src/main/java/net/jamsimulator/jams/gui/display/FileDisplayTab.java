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

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.utils.AnchorUtils;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.Virtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.util.Objects;

public class FileDisplayTab extends Tab {

	private final FileDisplayList list;
	private final File file;
	private final FileDisplay display;
	private boolean saveMark;

	public FileDisplayTab(FileDisplayList list, File file) {
		this.list = list;
		this.file = file;
		this.saveMark = false;

		FileType type = Jams.getFileTypeManager().getByFile(file).orElse(Jams.getFileTypeManager().getUnknownType());
		this.display = type.createDisplayTab(this);
		if (display == null) return;

		setGraphic(new NearestImageView(type.getIcon()));
		setText(file.getName());


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
			list.closeFileInternal(this);
			display.onClose();
		});
	}

	public FileDisplayList getList() {
		return list;
	}

	public WorkingPane getWorkingPane() {
		return list.getWorkingPane();
	}

	public File getFile() {
		return file;
	}

	public FileDisplay getDisplay() {
		return display;
	}

	public boolean isSaveMark() {
		return saveMark;
	}

	public void setSaveMark(boolean saveMark) {
		if (saveMark == this.saveMark) return;
		this.saveMark = saveMark;
		setText(saveMark ? file.getName() + " *" : file.getName());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FileDisplayTab that = (FileDisplayTab) o;
		return file.equals(that.file);
	}

	@Override
	public int hashCode() {
		return Objects.hash(file);
	}
}
