package net.jamsimulator.jams.gui.explorer.folder;

import javafx.scene.input.MouseEvent;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;

import java.io.File;

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
			}
		}
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
