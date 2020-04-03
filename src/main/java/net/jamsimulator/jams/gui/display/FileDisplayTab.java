package net.jamsimulator.jams.gui.display;

import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.utils.AnchorUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.util.Objects;

public class FileDisplayTab extends Tab {

	private final FileDisplayList list;
	private final File file;
	private final FileDisplay display;

	public FileDisplayTab(FileDisplayList list, File file) {
		this.list = list;
		this.file = file;

		FileType type = Jams.getFileTypeManager().getByFile(file).orElse(Jams.getFileTypeManager().getUnknownType());
		this.display = type.createDisplayTab(this);

		setGraphic(new ImageView(type.getIcon()));
		setText(file.getName());

		AnchorPane pane = new AnchorPane();
		VirtualizedScrollPane<CodeArea> scroll = new VirtualizedScrollPane<>(display);
		AnchorUtils.setAnchor(scroll, 0, 0, 0, 0);
		display.prefWidthProperty().bind(pane.widthProperty());
		display.prefHeightProperty().bind(pane.heightProperty());

		pane.getChildren().addAll(scroll);

		setContent(pane);

		setOnClosed(target -> display.onClose());
	}

	public FileDisplayList getList() {
		return list;
	}

	public File getFile() {
		return file;
	}

	public FileDisplay getDisplay() {
		return display;
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
