package net.jamsimulator.jams.gui.explorer.folder;

import javafx.scene.image.ImageView;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;

import java.io.File;

public class ExplorerFile extends ExplorerBasicElement {

	private File file;

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
		icon.setImage(JamsApplication.getFileIconManager().getImageByFile(file));
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
}
