package net.jamsimulator.jams.gui.start;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.configuration.ConfigurationWindow;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.BorderlessMainScene;
import net.jamsimulator.jams.gui.popup.CreateProjectWindow;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.io.File;

public class StartWindow extends AnchorPane {

	public static Stage stage;

	private final StartWindowTopBar topBar;
	private final VBox contents;
	private Label subtitle;

	private StartWindow() {
		getStyleClass().add("anchor-pane");

		contents = new VBox();
		topBar = new StartWindowTopBar(stage);

		AnchorUtils.setAnchor(contents, 30, 0, 0, 0);
		getChildren().add(contents);

		contents.setAlignment(Pos.CENTER);
		contents.setSpacing(8);

		loadButtons();
		loadImage();
		loadTitle();

		contents.getChildren().add(new Region());

		loadOptions();

		Jams.getLanguageManager().registerListeners(this, true);
	}

	private void loadButtons() {
		AnchorUtils.setAnchor(topBar, 0, -1, 0, 0);
		topBar.setPrefHeight(30);
		getChildren().add(topBar);
	}

	private void loadImage() {
		Image image = JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO
		).orElse(null);
		contents.getChildren().add(new NearestImageView(image, 150, 150));
	}

	private void loadTitle() {
		Label title = new LanguageLabel(Messages.START_TITLE);
		title.getStyleClass().add("start-title");
		contents.getChildren().add(title);

		String subtitleText = Jams.getLanguageManager().getSelected().getOrDefault(Messages.START_SUBTITLE)
				.replace("{VERSION}", Jams.getVersion());
		subtitle = new Label(subtitleText);
		subtitle.getStyleClass().add("start-subtitle");
		contents.getChildren().add(subtitle);
	}

	private void loadOptions() {
		Label createProject = new LanguageLabel(Messages.MAIN_MENU_FILE_CREATE_PROJECT);
		createProject.setCursor(Cursor.HAND);
		contents.getChildren().add(createProject);

		Label openProject = new LanguageLabel(Messages.MAIN_MENU_FILE_OPEN_PROJECT);
		openProject.setCursor(Cursor.HAND);
		contents.getChildren().add(openProject);

		Label settings = new LanguageLabel(Messages.MAIN_MENU_FILE_SETTINGS);
		settings.setCursor(Cursor.HAND);
		contents.getChildren().add(settings);

		createProject.setOnMouseClicked(event -> {
			CreateProjectWindow.open();
			if (!JamsApplication.getProjectsTabPane().getProjects().isEmpty()) {
				stage.hide();
			}
		});

		openProject.setOnMouseClicked(event -> {
			DirectoryChooser chooser = new DirectoryChooser();
			File folder = chooser.showDialog(JamsApplication.getStage());
			if (folder == null || JamsApplication.getProjectsTabPane().isProjectOpen(folder)) return;
			JamsApplication.getProjectsTabPane().openProject(new MIPSProject(folder));
			stage.hide();
		});

		settings.setOnMouseClicked(event -> ConfigurationWindow.getInstance().open());
	}

	@Listener
	private void onLanguageChange(SelectedLanguageChangeEvent.After event) {
		String subtitleText = Jams.getLanguageManager().getSelected().getOrDefault(Messages.START_SUBTITLE)
				.replace("{VERSION}", Jams.getVersion());
		subtitle.setText(subtitleText);
	}


	@Listener
	private void onLanguageChange(DefaultLanguageChangeEvent.After event) {
		String subtitleText = Jams.getLanguageManager().getSelected().getOrDefault(Messages.START_SUBTITLE)
				.replace("{VERSION}", Jams.getVersion());
		subtitle.setText(subtitleText);
	}

	public static void open() {
		if (stage == null) {
			stage = new Stage();

			StartWindow window = new StartWindow();
			BorderlessMainScene scene = new BorderlessMainScene(stage, window, 400, 400);
			stage.setScene(scene);

			scene.setMoveControl(window.topBar);
			scene.setResizable(false);
			scene.setSnapEnabled(false);

			stage.initStyle(StageStyle.TRANSPARENT);
			stage.setHeight(500);
			stage.setWidth(500);
		}
		stage.showAndWait();
	}
}
