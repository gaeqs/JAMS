package net.jamsimulator.jams.gui.explorer.folder;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.ExplorerSectionRepresentation;

public class ExplorerFolderRepresentation extends ExplorerSectionRepresentation {
	/**
	 * Creates the representation.
	 *
	 * @param section         the {@link ExplorerSection} to represent.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ExplorerFolderRepresentation(ExplorerSection section, int hierarchyLevel) {
		super(section, hierarchyLevel);
	}


	@Override
	protected ImageView loadIcon() {
		return new ImageView(JamsApplication.getFileIconManager().getFolderIcon());
	}
}
