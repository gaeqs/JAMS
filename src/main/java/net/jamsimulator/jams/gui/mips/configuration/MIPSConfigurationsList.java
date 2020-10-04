package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.utils.AnchorUtils;

public class MIPSConfigurationsList extends AnchorPane {

	private final MIPSConfigurationWindow window;

	private final MIPSConfigurationListControls controls;
	private final MIPSConfigurationsListContents contents;

	public MIPSConfigurationsList(MIPSConfigurationWindow window) {
		this.window = window;
		controls = new MIPSConfigurationListControls(this);
		contents = new MIPSConfigurationsListContents(null, window);

		var scroll = new ScrollPane(contents);
		scroll.setFitToHeight(true);
		scroll.setFitToWidth(true);

		AnchorUtils.setAnchor(controls, 0, -1, 0, 0);
		AnchorUtils.setAnchor(scroll, 30, 0, 0, 0);

		getChildren().addAll(controls, scroll);
	}

	public MIPSConfigurationWindow getWindow() {
		return window;
	}

	public MIPSConfigurationListControls getControls() {
		return controls;
	}

	public MIPSConfigurationsListContents getContents() {
		return contents;
	}
}
