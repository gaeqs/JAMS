package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.ExplorerSeparatorRegion;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

public class ActionsExplorerAction extends ExplorerBasicElement {

	public static final String LANGUAGE_NODE_PREFIX = "ACTION_";

	protected Action action;

	/**
	 * Creates an action explorer element.
	 *
	 * @param parent the {@link ExplorerSection} containing this element.
	 * @param action the represented {@link Action}.
	 */
	public ActionsExplorerAction(ActionExplorerRegion parent, Action action) {
		super(parent, action.getName(), 2);
		this.action = action;

		((LanguageLabel) label).setNode(LANGUAGE_NODE_PREFIX + action.getName());

		Region separator = new Region();
		HBox.setHgrow(separator, Priority.ALWAYS);
		getChildren().add(separator);

		for (KeyCombination combination : JamsApplication.getActionManager().getBindCombinations(action.getName())) {
			getChildren().add(new ActionExplorerActionCombination(combination));
		}
	}

	public Action getAction() {
		return action;
	}

	@Override
	protected void loadElements() {
		icon = new NearestImageView();
		label = new LanguageLabel(null);

		separator = new ExplorerSeparatorRegion(false, hierarchyLevel);

		getChildren().addAll(separator, icon, label);
		setSpacing(SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}
}
