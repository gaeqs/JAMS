package net.jamsimulator.jams.gui.explorer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

/**
 * This class allows {@link ExplorerSection}s to be represented inside the explorer.
 * It's functionality is similar to the class {@link ExplorerBasicElement}.
 */
public class ExplorerSectionLanguageRepresentation extends ExplorerSectionRepresentation {

	/**
	 * Creates the representation.
	 *
	 * @param section        the {@link ExplorerSection} to represent.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 * @param languageNode   the language node.
	 */
	public ExplorerSectionLanguageRepresentation(ExplorerSection section, int hierarchyLevel, String languageNode) {
		super(section, hierarchyLevel);
		loadElements(languageNode);
	}

	public void setLanguageNode(String node) {
		if (node != null) {
			if (label instanceof LanguageLabel) {
				((LanguageLabel) label).setNode(node);
			} else {
				getChildren().remove(label);
				label = new LanguageLabel(node);
				getChildren().add(label);
			}
		} else {
			if (label instanceof LanguageLabel) {
				getChildren().remove(label);
				label = new Label(section.getName());
				getChildren().add(label);
			}
		}
	}


	@Override
	protected void loadElements() {
		statusIcon = new ImageView();
		icon = new ImageView();

		ExplorerSeparatorRegion separator = new ExplorerSeparatorRegion(hierarchyLevel);

		getChildren().addAll(separator, statusIcon, icon);
		setSpacing(ExplorerBasicElement.SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}

	protected void loadElements(String languageNode) {
		label = languageNode == null ? new Label(section.getName()) : new LanguageLabel(languageNode);
		getChildren().add(label);
	}

}
