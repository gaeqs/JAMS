package net.jamsimulator.jams.gui.mips.sidebar;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.defaults.texteditor.TextEditorActionCompile;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.util.ArchitectureComboBox;
import net.jamsimulator.jams.gui.util.MemoryBuilderComboBox;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.project.mips.MipsSimulationConfiguration;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationAddEvent;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationRemoveEvent;
import net.jamsimulator.jams.project.mips.event.SelectedMipsSimulationConfigurationChangeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimulationSidebar extends VBox {

	public static final int BUTTONS_SIZE = 20;

	private final MipsProject project;

	private ComboBox<String> configBox;
	private Button addConfigButton, removeConfigButton;
	private TextField nameField;
	private MemoryBuilderComboBox memoryBox;
	private ArchitectureComboBox architectureBox;
	private SimulationSyscallsConfiguration syscallsConfiguration;

	public SimulationSidebar(MipsProject project) {
		setAlignment(Pos.TOP_LEFT);
		setSpacing(5);
		this.project = project;
		init();
	}

	public MipsProject getProject() {
		return project;
	}

	private void init() {
		getChildren().add(new Separator(Orientation.HORIZONTAL));

		HBox configurationsTitle = new HBox(new Group(), new LanguageLabel(Messages.SIMULATION_CONFIGURATION_CONFIGURATIONS));
		configurationsTitle.setSpacing(5);
		getChildren().add(configurationsTitle);

		generateConfigurationHBox();
		generateButtonsHBox();
		getChildren().add(new Separator(Orientation.HORIZONTAL));
		generateConfigurationInfo();
		getChildren().add(new Separator(Orientation.HORIZONTAL));
		generateSyscallsConfiguration();

		project.getData().registerListeners(this, true);
	}

	//region configuration HBox

	protected void generateConfigurationHBox() {
		configBox = new ComboBox<>();

		Set<MipsSimulationConfiguration> configurations = project.getData().getConfigurations();
		configurations.forEach(config -> configBox.getItems().add(config.getName()));
		if (project.getData().getSelectedConfiguration().isPresent()) {
			configBox.getSelectionModel().select(project.getData().getSelectedConfiguration().get().getName());
		}

		generateAddButton();
		generateRemoveButton();

		configBox.setOnAction(target -> Platform.runLater(() ->
				project.getData().setSelectedConfiguration(configBox.getSelectionModel().getSelectedItem())));

		HBox configHBox = new HBox(configBox, addConfigButton, removeConfigButton);
		configHBox.setAlignment(Pos.CENTER);

		configBox.prefWidthProperty().bind(widthProperty()
				.subtract(addConfigButton.widthProperty()).subtract(removeConfigButton.widthProperty()));
		getChildren().add(configHBox);
	}

	private void generateAddButton() {
		addConfigButton = new Button("+");
		addConfigButton.getStyleClass().add("bold-button");
		addConfigButton.setEllipsisString("+");


		Map<Integer, SyscallExecutionBuilder<?>> syscalls = new HashMap<>();
		//TODO DEFAULT

		addConfigButton.setOnAction(event -> {
			Set<MipsSimulationConfiguration> configs = project.getData().getConfigurations();
			String name = "New Configuration";
			if (configs.stream().anyMatch(target -> target.getName().equals(name))) {
				String newName = null;
				int i = 2;
				boolean repeat;
				do {
					String current = name + " (" + i + ")";
					repeat = configs.stream().anyMatch(target -> target.getName().equals(current));
					if (!repeat) newName = current;
					i++;
				} while (repeat);
				project.getData().addConfiguration(new MipsSimulationConfiguration(newName,
						Jams.getArchitectureManager().getDefault(), Jams.getMemoryBuilderManager().getDefault(),
						syscalls));
			} else {
				project.getData().addConfiguration(new MipsSimulationConfiguration(name,
						Jams.getArchitectureManager().getDefault(), Jams.getMemoryBuilderManager().getDefault(),
						syscalls));
			}

		});
	}

	private void generateRemoveButton() {
		removeConfigButton = new Button("-");
		removeConfigButton.getStyleClass().add("bold-button");
		removeConfigButton.setEllipsisString("-");
		removeConfigButton.setOnAction(event -> {
			if (configBox.getSelectionModel().getSelectedItem() == null) return;
			project.getData().removeConfiguration(configBox.getSelectionModel().getSelectedItem());
		});
	}

	//endregion

	private void generateButtonsHBox() {
		HBox box = new HBox();
		box.setAlignment(Pos.CENTER);

		Image assembleIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_ASSEMBLE,
				Icons.PROJECT_ASSEMBLE_PATH, 1024, 1024).orElse(null);
		Button assemble = new Button("", new NearestImageView(assembleIcon, BUTTONS_SIZE, BUTTONS_SIZE));
		box.getChildren().add(assemble);

		assemble.setOnAction(event -> {
			JamsApplication.getActionManager().get(TextEditorActionCompile.NAME).ifPresent(action -> {
				TextEditorActionCompile.compileAndShow(project);
			});
		});

		getChildren().add(box);
	}


	//region configuration info

	private void generateConfigurationInfo() {
		getChildren().add(new LanguageLabel(Messages.SIMULATION_CONFIGURATION_INFO));
		generateNameHBox();
		generateArchitectureHBox();
		generateMemoryHBox();
	}

	private void generateNameHBox() {
		MipsSimulationConfiguration selected = project.getData().getSelectedConfiguration().orElse(null);
		HBox nameHBox = new HBox();
		nameHBox.setSpacing(5);
		nameHBox.setAlignment(Pos.CENTER_LEFT);
		nameHBox.getChildren().add(new Region());

		Label label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_NAME);
		nameHBox.getChildren().add(new Group(label));


		nameField = new TextField(selected == null ? "" : selected.getName());
		nameField.setVisible(selected != null);

		EventHandler<ActionEvent> handler = event -> {
			if (!project.getData().getSelectedConfiguration().isPresent()) return;
			if (project.getData().getConfigurations().stream()
					.anyMatch(target -> target.getName().equals(nameField.getText()))) {
				nameField.setText(project.getData().getSelectedConfiguration().get().getName());
				return;
			}
			project.getData().getSelectedConfiguration().get().setName(nameField.getText());
			refreshConfigBox();
		};

		nameField.setOnAction(handler);
		nameField.focusedProperty().addListener((obs, old, val) -> {
			if (!val) {
				handler.handle(null);
			}
		});

		nameField.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty()).subtract(30));

		nameHBox.getChildren().add(nameField);
		getChildren().add(nameHBox);
	}

	private void generateArchitectureHBox() {
		MipsSimulationConfiguration selected = project.getData().getSelectedConfiguration().orElse(null);
		HBox architectureHBox = new HBox();
		architectureHBox.setSpacing(5);
		architectureHBox.setAlignment(Pos.CENTER_LEFT);
		architectureHBox.getChildren().add(new Region());
		Label label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_ARCHITECTURE);
		architectureHBox.getChildren().add(new Group(label));

		architectureBox = new ArchitectureComboBox(selected == null ? Jams.getArchitectureManager().getDefault() : selected.getArchitecture());
		architectureBox.setVisible(selected != null);
		architectureBox.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty()).subtract(30));

		architectureBox.setOnAction(event -> {
			if (!project.getData().getSelectedConfiguration().isPresent()) return;
			project.getData().getSelectedConfiguration().get()
					.setArchitecture(architectureBox.getSelectionModel().getSelectedItem());
		});

		architectureHBox.getChildren().add(architectureBox);
		getChildren().add(architectureHBox);
	}

	private void generateMemoryHBox() {
		MipsSimulationConfiguration selected = project.getData().getSelectedConfiguration().orElse(null);
		HBox memoryHBox = new HBox();
		memoryHBox.setSpacing(5);
		memoryHBox.setAlignment(Pos.CENTER_LEFT);
		memoryHBox.getChildren().add(new Region());
		Label label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_MEMORY);
		memoryHBox.getChildren().add(new Group(label));

		memoryBox = new MemoryBuilderComboBox(selected == null ? Jams.getMemoryBuilderManager().getDefault() : selected.getMemoryBuilder());
		memoryBox.setVisible(selected != null);
		memoryBox.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty()).subtract(30));

		memoryBox.setOnAction(event -> {
			if (!project.getData().getSelectedConfiguration().isPresent()) return;
			project.getData().getSelectedConfiguration().get()
					.setMemoryBuilder(memoryBox.getSelectionModel().getSelectedItem());
		});


		memoryHBox.getChildren().add(memoryBox);
		getChildren().add(memoryHBox);
	}


	//endregion

	//region syscalls

	private void generateSyscallsConfiguration() {
		getChildren().add(new LanguageLabel(Messages.SIMULATION_SYSTEM_CALLS));
		syscallsConfiguration = new SimulationSyscallsConfiguration(project.getData().getSelectedConfiguration().orElse(null));
		getChildren().add(syscallsConfiguration);
	}


	//endregion

	private void refreshConfigBox() {
		configBox.getItems().clear();
		Set<MipsSimulationConfiguration> configurations = project.getData().getConfigurations();
		configurations.forEach(config -> configBox.getItems().add(config.getName()));
		if (project.getData().getSelectedConfiguration().isPresent()) {
			configBox.getSelectionModel().select(project.getData().getSelectedConfiguration().get().getName());
		}
	}

	@Listener
	private void onConfigurationAdd(MipsSimulationConfigurationAddEvent.After event) {
		configBox.getItems().add(event.getMipsSimulationConfiguration().getName());
	}

	@Listener
	private void onConfigurationRemove(MipsSimulationConfigurationRemoveEvent.After event) {
		configBox.getItems().remove(event.getMipsSimulationConfiguration().getName());
	}

	@Listener
	private void onConfigurationChange(SelectedMipsSimulationConfigurationChangeEvent.After event) {
		syscallsConfiguration.setConfiguration(event.getNewConfig());
		if (event.getNewConfig() == null) {
			nameField.setVisible(false);
			architectureBox.setVisible(false);
			memoryBox.setVisible(false);
			return;
		}

		nameField.setVisible(true);
		architectureBox.setVisible(true);
		memoryBox.setVisible(true);

		configBox.getSelectionModel().select(event.getNewConfig().getName());
		nameField.setText(event.getNewConfig().getName());
		architectureBox.getSelectionModel().select(event.getNewConfig().getArchitecture());
		memoryBox.getSelectionModel().select(event.getNewConfig().getMemoryBuilder());
		syscallsConfiguration.setConfiguration(event.getNewConfig());
	}

}
