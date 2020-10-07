package net.jamsimulator.jams.gui.mips.configuration.syscall;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.util.value.ValueEditors;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.defaults.SyscallExecutionRunExceptionHandler;
import net.jamsimulator.jams.utils.AnchorUtils;


public class MIPSConfigurationSyscallControls extends AnchorPane {

	private final MIPSConfigurationDisplaySyscallTab syscallTab;
	private final HBox buttonsHbox, bundleHbox;

	public MIPSConfigurationSyscallControls(MIPSConfigurationDisplaySyscallTab syscallTab) {
		this.syscallTab = syscallTab;

		buttonsHbox = new HBox();
		bundleHbox = new HBox();
		AnchorUtils.setAnchor(buttonsHbox, 0, 0, 0, -1);
		AnchorUtils.setAnchor(bundleHbox, 0, 0, -1, 0);
		buttonsHbox.setAlignment(Pos.CENTER_LEFT);
		bundleHbox.setAlignment(Pos.CENTER_RIGHT);
		bundleHbox.setSpacing(5);
		getChildren().addAll(bundleHbox, buttonsHbox);

		populate();
	}


	private void populate() {
		generateAddButton();
		generateRemoveButton();
		generateSortButton();
		generateBundleBox();
	}

	private void generateAddButton() {
		var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.CONTROL_ADD,
				Icons.CONTROL_ADD_PATH, 1024, 1024).orElse(null);

		var button = new Button(null, new NearestImageView(icon, 16, 16));
		button.setTooltip(new LanguageTooltip(Messages.GENERAL_ADD));
		button.getStyleClass().add("bold-button");

		button.setOnAction(event -> {
			int id = syscallTab.getContents().getBiggestId() + 1;
			var builder = Jams.getSyscallExecutionBuilderManager()
					.get(SyscallExecutionRunExceptionHandler.NAME).map(SyscallExecutionBuilder::makeNewInstance).orElse(null);

			syscallTab.getConfiguration().getSyscallExecutionBuilders().put(id, builder);
			syscallTab.getContents().add(id, builder);
		});

		buttonsHbox.getChildren().add(button);
	}


	private void generateRemoveButton() {
		var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.CONTROL_REMOVE,
				Icons.CONTROL_REMOVE_PATH, 1024, 1024).orElse(null);

		var button = new Button(null, new NearestImageView(icon, 16, 16));
		button.setTooltip(new LanguageTooltip(Messages.GENERAL_REMOVE));
		button.getStyleClass().add("bold-button");

		button.setOnAction(event -> {
			var contents = syscallTab.getContents();
			var selected = contents.getSelectedElements();

			if (selected.isEmpty()) return;
			for (ExplorerElement element : selected) {
				if (!(element instanceof MIPSConfigurationSyscallContents.Representation)) continue;

				var previous = element.getPrevious();
				contents.remove((MIPSConfigurationSyscallContents.Representation) element);
				syscallTab.getConfiguration().getSyscallExecutionBuilders()
						.remove(((MIPSConfigurationSyscallContents.Representation) element).getSyscallId());

				if (previous.isPresent()) {
					contents.selectElementAlone(previous.get());
				} else {
					contents.getMainSection().getElementByIndex(0).ifPresent(contents::selectElementAlone);

					if (contents.getMainSection().isEmpty()) {
						syscallTab.display(null);
					}
				}
			}
		});

		buttonsHbox.getChildren().add(button);
	}

	private void generateSortButton() {
		var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.CONTROL_SORT,
				Icons.CONTROL_SORT_PATH, 1024, 1024).orElse(null);

		var button = new Button(null, new NearestImageView(icon, 16, 16));
		button.setTooltip(new LanguageTooltip(Messages.GENERAL_SORT));
		button.getStyleClass().add("bold-button");

		button.setOnAction(event -> {
			syscallTab.getContents().sort();
		});

		buttonsHbox.getChildren().add(button);
	}

	private void generateBundleBox() {

		var editor = ValueEditors.getByTypeUnsafe(SyscallExecutionBuilderBundle.class).build();

		var button = new LanguageButton(Messages.SIMULATION_CONFIGURATION_SYSTEM_CALLS_TAB_LOAD_BUNDLE);
		button.setOnAction(event -> {
			var bundle = editor.getCurrentValue();

			syscallTab.getConfiguration().getSyscallExecutionBuilders().clear();
			syscallTab.getConfiguration().getSyscallExecutionBuilders().putAll(bundle.buildBundle());
			syscallTab.getContents().reload();
		});

		bundleHbox.getChildren().addAll(editor.getAsNode(), button);
	}

}
