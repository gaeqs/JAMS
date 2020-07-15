package net.jamsimulator.jams.gui.mips.sidebar;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.defaults.texteditor.TextEditorActionCompile;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.configuration.ConfigurationsWindow;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.project.mips.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationAddEvent;
import net.jamsimulator.jams.project.mips.event.MipsSimulationConfigurationRemoveEvent;
import net.jamsimulator.jams.project.mips.event.SelectedMipsSimulationConfigurationChangeEvent;

import java.util.Set;

public class SimulationSidebar extends VBox {

	public static final int BUTTONS_SIZE = 20;

	private final MipsProject project;

	private ComboBox<String> configBox;
	private Button configButton;

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

		project.getData().registerListeners(this, true);
	}

	//region configuration HBox

	protected void generateConfigurationHBox() {
		configBox = new ComboBox<>();

		Set<MIPSSimulationConfiguration> configurations = project.getData().getConfigurations();
		configurations.forEach(config -> configBox.getItems().add(config.getName()));
		if (project.getData().getSelectedConfiguration().isPresent()) {
			configBox.getSelectionModel().select(project.getData().getSelectedConfiguration().get().getName());
		}

		generateRemoveButton();

		configBox.setOnAction(target -> Platform.runLater(() ->
				project.getData().setSelectedConfiguration(configBox.getSelectionModel().getSelectedItem())));

		HBox configHBox = new HBox(configBox, configButton);
		configHBox.setAlignment(Pos.CENTER);

		configBox.prefWidthProperty().bind(widthProperty().subtract(configButton.widthProperty()));
		getChildren().add(configHBox);
	}

	private void generateRemoveButton() {
		Image image = JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_SETTINGS,
				Icons.PROJECT_SETTINGS_PATH, 1024, 1024).orElse(null);

		configButton = new Button("", new NearestImageView(image, 16, 16));
		configButton.getStyleClass().add("bold-button");
		configButton.setEllipsisString("-");
		configButton.setOnAction(event -> {
			ConfigurationsWindow.open(project.getData());
			//if (configBox.getSelectionModel().getSelectedItem() == null) return;
			//project.getData().removeConfiguration(configBox.getSelectionModel().getSelectedItem());
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
		if (event.getNewConfig() == null) {
			return;
		}
		configBox.getSelectionModel().select(event.getNewConfig().getName());
	}

}
