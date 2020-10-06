package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.utils.AnchorUtils;

public class MIPSConfigurationsList extends AnchorPane {

	private final MIPSConfigurationWindow window;

	private final MIPSConfigurationListControls controls;
	private final MIPSConfigurationsListContents contents;

	public MIPSConfigurationsList(MIPSConfigurationWindow window) {
		this.window = window;

		SplitPane.setResizableWithParent(this, false);

		controls = new MIPSConfigurationListControls(this);

		var scroll = new PixelScrollPane();
		contents = new MIPSConfigurationsListContents(scroll, window);
		scroll.setContent(contents);
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
