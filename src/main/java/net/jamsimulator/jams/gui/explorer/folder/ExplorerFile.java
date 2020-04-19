package net.jamsimulator.jams.gui.explorer.folder;

import javafx.scene.input.*;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ExplorerFile extends ExplorerBasicElement {

	private final File file;

	/**
	 * Creates an explorer file.
	 *
	 * @param parent         the {@link ExplorerSection} containing this file.
	 * @param file           the represented {@link File}.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ExplorerFile(ExplorerFolder parent, File file, int hierarchyLevel) {
		super(parent, file.getName(), hierarchyLevel);
		getStyleClass().add("explorer-file");
		this.file = file;
		icon.setImage(Jams.getFileTypeManager().getByFile(file).orElse(Jams.getFileTypeManager().getUnknownType()).getIcon());
	}

	@Override
	protected void onMouseClicked(MouseEvent mouseEvent) {
		super.onMouseClicked(mouseEvent);
		if (mouseEvent.getClickCount() % 2 == 0) {
			Explorer explorer = getExplorer();
			if (explorer instanceof FolderExplorer) {
				((FolderExplorer) explorer).getFileOpenAction().accept(this);
				mouseEvent.consume();
			}
		}
	}

	@Override
	protected void onKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			Explorer explorer = getExplorer();
			if (explorer instanceof FolderExplorer) {
				((FolderExplorer) explorer).getFileOpenAction().accept(this);
				event.consume();
				return;
			}
		}
		super.onKeyPressed(event);
	}

	@Override
	protected void loadListeners() {
		super.loadListeners();

		addEventHandler(DragEvent.DRAG_OVER, event -> {
			if (!(parent instanceof ExplorerFolder) || !event.getDragboard().hasFiles()) return;
			File to = ((ExplorerFolder) parent).getFolder();
			if (event.getDragboard().getFiles().stream().anyMatch(target -> target.equals(to)
					|| FileUtils.isChild(to, target)))
				return;

			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

			if(!getStyleClass().contains("explorer-file-allow-drop")) {
				getStyleClass().add("explorer-file-allow-drop");
			}
			if(parent instanceof ExplorerFolder) ((ExplorerFolder) parent).removeDragHint();
			event.consume();
		});

		addEventHandler(DragEvent.DRAG_EXITED, event -> {
			getStyleClass().remove("explorer-file-allow-drop");
			System.out.println(getStyleClass()
			);
			applyCss();
		});

		addEventHandler(DragEvent.DRAG_DROPPED, event -> {
			List<File> files = event.getDragboard().getFiles();
			for (File file : files) {
				if (!FileUtils.copyFile(((ExplorerFolder) parent).getFolder(), file)) {
					System.err.println("Error while copying file " + file + ".");
				}
			}
			event.setDropCompleted(true);
			event.consume();
		});

		addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
			getExplorer().setSelectedElement(this);
			Dragboard db = startDragAndDrop(TransferMode.COPY);
			ClipboardContent content = new ClipboardContent();
			content.putFiles(Collections.singletonList(file));
			db.setContent(content);
			event.consume();
		});
	}

	/**
	 * Returns the {@link File} represented by this explorer file.
	 *
	 * @return the {@link File}.
	 */
	public File getFile() {
		return file;
	}

	@Override
	public ExplorerFolder getParentSection() {
		return (ExplorerFolder) super.getParentSection();
	}

	@Override
	public Explorer getExplorer() {
		return super.getExplorer();
	}
}
