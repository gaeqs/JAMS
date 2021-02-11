package net.jamsimulator.jams.gui.mips.configuration.cache;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.gui.util.AnchorUtils;


public class MIPSConfigurationCacheControls extends AnchorPane {

	private final MIPSConfigurationDisplayCacheTab cacheTab;
	private final HBox buttonsHbox;

	public MIPSConfigurationCacheControls(MIPSConfigurationDisplayCacheTab cacheTab) {
		this.cacheTab = cacheTab;

		buttonsHbox = new HBox();
		AnchorUtils.setAnchor(buttonsHbox, 0, 0, 0, -1);
		buttonsHbox.setAlignment(Pos.CENTER_LEFT);
		getChildren().add(buttonsHbox);

		populate();
	}


	private void populate() {
		generateAddButton();
		generateRemoveButton();
	}

	private void generateAddButton() {
		var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.CONTROL_ADD
		).orElse(null);

		var button = new Button(null, new NearestImageView(icon, 16, 16));
		button.setTooltip(new LanguageTooltip(Messages.GENERAL_ADD));
		button.getStyleClass().add("bold-button");

		button.setOnAction(event -> {
			var builder = Jams.getCacheBuilderManager().stream().findAny()
					.map(CacheBuilder::makeNewInstance).orElse(null);

			cacheTab.getConfiguration().getCacheBuilders().add(builder);
			cacheTab.getContents().add(builder);
		});

		buttonsHbox.getChildren().add(button);
	}


	private void generateRemoveButton() {
		var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.CONTROL_REMOVE
		).orElse(null);

		var button = new Button(null, new NearestImageView(icon, 16, 16));
		button.setTooltip(new LanguageTooltip(Messages.GENERAL_REMOVE));
		button.getStyleClass().add("bold-button");

		button.setOnAction(event -> {
			var contents = cacheTab.getContents();
			var selected = contents.getSelectedElements();

			if (selected.isEmpty()) return;
			for (ExplorerElement element : selected) {
				if (!(element instanceof MIPSConfigurationCacheContents.Representation)) continue;

				var previous = element.getPrevious();
				contents.remove((MIPSConfigurationCacheContents.Representation) element);
				cacheTab.getConfiguration().getCacheBuilders()
						.remove(((MIPSConfigurationCacheContents.Representation) element).getIndex());

				if (previous.isPresent()) {
					contents.selectElementAlone(previous.get());
				} else {
					contents.getMainSection().getElementByIndex(0).ifPresent(contents::selectElementAlone);

					if (contents.getMainSection().isEmpty()) {
						cacheTab.display(null);
					}
				}
			}
		});

		buttonsHbox.getChildren().add(button);
	}

}
