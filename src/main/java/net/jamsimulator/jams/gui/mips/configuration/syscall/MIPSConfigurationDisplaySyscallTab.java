package net.jamsimulator.jams.gui.mips.configuration.syscall;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.utils.AnchorUtils;

public class MIPSConfigurationDisplaySyscallTab extends AnchorPane {

	private final MIPSSimulationConfiguration configuration;

	private final MIPSConfigurationSyscallControls controls;
	private final MIPSConfigurationSyscallContents contents;
	private final SplitPane splitPane;
	private final ScrollPane displayGroup;

	private MIPSConfigurationSyscallDisplay display;

	public MIPSConfigurationDisplaySyscallTab(MIPSSimulationConfiguration configuration) {
		this.configuration = configuration;

		controls = new MIPSConfigurationSyscallControls(this);
		splitPane = new SplitPane();
		displayGroup = new ScrollPane();
		displayGroup.setFitToHeight(true);
		displayGroup.setFitToWidth(true);

		var scroll = new PixelScrollPane();
		contents = new MIPSConfigurationSyscallContents(null, this);
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

	public MIPSConfigurationSyscallControls getControls() {
		return controls;
	}

	public MIPSConfigurationSyscallContents getContents() {
		return contents;
	}

	public MIPSConfigurationSyscallDisplay getDisplay() {
		return display;
	}

	public void display(SyscallExecutionBuilder<?> builder) {
		display = builder == null ? null : new MIPSConfigurationSyscallDisplay(builder);
		displayGroup.setContent(display);
	}
}
