package net.jamsimulator.jams.gui.mips.configuration.cache;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.utils.AnchorUtils;

public class MIPSConfigurationDisplayCacheTab extends AnchorPane {

	private final MIPSSimulationConfiguration configuration;

	private final MIPSConfigurationCacheControls controls;
	private final MIPSConfigurationCacheContents contents;
	private final SplitPane splitPane;
	private final ScrollPane displayGroup;

	private MIPSConfigurationCacheDisplay display;

	public MIPSConfigurationDisplayCacheTab(MIPSSimulationConfiguration configuration) {
		this.configuration = configuration;

		controls = new MIPSConfigurationCacheControls(this);
		splitPane = new SplitPane();
		displayGroup = new ScrollPane();
		displayGroup.setFitToHeight(true);
		displayGroup.setFitToWidth(true);

		var scroll = new PixelScrollPane();
		contents = new MIPSConfigurationCacheContents(null, this);
		scroll.setContent(contents);
		scroll.setFitToHeight(true);
		scroll.setFitToWidth(true);

		AnchorUtils.setAnchor(controls, 0, -1, 0, 0);
		AnchorUtils.setAnchor(splitPane, 30, 0, 0, 0);

		splitPane.getItems().addAll(scroll, displayGroup);
		getChildren().addAll(controls, splitPane);

		contents.selectFirst();

		Platform.runLater(() -> splitPane.setDividerPosition(0, 0.55));
	}

	public MIPSSimulationConfiguration getConfiguration() {
		return configuration;
	}

	public MIPSConfigurationCacheControls getControls() {
		return controls;
	}

	public MIPSConfigurationCacheContents getContents() {
		return contents;
	}

	public MIPSConfigurationCacheDisplay getDisplay() {
		return display;
	}

	public void display(CacheBuilder<?> builder) {
		display = builder == null ? null : new MIPSConfigurationCacheDisplay(builder);
		displayGroup.setContent(display);
	}
}
