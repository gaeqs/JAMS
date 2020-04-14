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
			VirtualizedScrollPane scroll = new VirtualizedScrollPane(element);
			AnchorUtils.setAnchor(scroll, 0, 0, 0, 0);
			pane.getChildren().addAll(scroll);
			if (element instanceof VirtualScrollHandled) {
				((VirtualScrollHandled) element).setScrollPane(scroll);
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
