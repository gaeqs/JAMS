package net.jamsimulator.jams.gui.popup;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.AnchorUtils;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;

public class CreateProjectWindow extends VBox {

	public static final int WIDTH = 600;

	public CreateProjectWindow(Stage stage) {
		setSpacing(5);

		HBox nameHBox = new HBox();
		nameHBox.setSpacing(7);
		Label nameLabel = new LanguageLabel(Messages.MAIN_MENU_FILE_CREATE_PROJECT_NAME);
		nameHBox.getChildren().add(new Group(nameLabel));
		TextField nameField = new TextField();
		nameField.getStyleClass().add("invalid-text-field");
		nameHBox.getChildren().add(nameField);
		getChildren().add(nameHBox);

		HBox pathHBox = new HBox();
		pathHBox.setSpacing(7);
		Label pathLabel = new LanguageLabel(Messages.MAIN_MENU_FILE_CREATE_PROJECT_PATH);
		TextField pathField = new TextField();
		Button selectParent = new Button("", new NearestImageView(Jams.getFileTypeManager().getFolderType().getIcon(), 16, 16));
		pathHBox.getChildren().addAll(new Group(pathLabel), pathField, selectParent);
		getChildren().add(pathHBox);

		File defPath = new File(Jams.getMainFolder().getParentFile(), "JAMSProjects");
		pathField.setText(defPath.getAbsolutePath() + File.separator);

		HBox buttonsHBox = new HBox();
		buttonsHBox.setAlignment(Pos.BOTTOM_RIGHT);
		buttonsHBox.setSpacing(5);
		Button finish = new LanguageButton(Messages.GENERAL_FINISH);
		Button cancel = new LanguageButton(Messages.GENERAL_CANCEL);
		buttonsHBox.getChildren().addAll(finish, cancel);
		getChildren().add(buttonsHBox);

		selectParent.setOnAction(event -> {
			DirectoryChooser chooser = new DirectoryChooser();
			File folder = chooser.showDialog(JamsApplication.getStage());
			if (folder == null) return;
			pathField.setText(folder.getAbsolutePath() + File.separator + nameField.getText());
		});

		finish.setDisable(true);
		finish.setOnAction(event -> {
			File folder = new File(pathField.getText());
			if (!folder.mkdirs()) return;
			MIPSProject project = new MIPSProject(nameField.getText(), folder);
			JamsApplication.getProjectsTabPane().openProject(project);
			stage.close();
		});

		cancel.setOnAction(event -> stage.close());

		nameField.prefWidthProperty().bind(nameHBox.widthProperty().subtract(nameLabel.widthProperty()));
		nameField.textProperty().addListener((obs, old, val) -> {
			int index = pathField.getText().lastIndexOf(File.separator);
			if (index == -1) {
				pathField.setText(val);
			} else {
				pathField.setText(pathField.getText().substring(0, index) + File.separator + val);
			}

			if (val.isEmpty()) {
				if (!nameField.getStyleClass().contains("invalid-text-field")) {
					nameField.getStyleClass().add("invalid-text-field");
				}
				finish.setDisable(true);
			} else {
				nameField.getStyleClass().remove("invalid-text-field");
				finish.setDisable(pathField.getStyleClass().contains("invalid-text-field"));
			}
		});

		pathField.prefWidthProperty().bind(pathHBox.widthProperty().subtract(pathLabel.widthProperty()).subtract(selectParent.widthProperty()));
		pathField.textProperty().addListener((obs, old, val) -> {
			File file = new File(val);
			if (!FileUtils.isValidPath(val) || !file.isDirectory() && file.exists()) {
				if (!pathField.getStyleClass().contains("invalid-text-field")) {
					pathField.getStyleClass().add("invalid-text-field");
				}
				finish.setDisable(true);
			} else {
				pathField.getStyleClass().remove("invalid-text-field");
				finish.setDisable(nameField.getText().isEmpty());
			}
		});
	}


	public static void open() {
		Stage stage = new Stage();
		stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.MAIN_MENU_FILE_CREATE_PROJECT_TITLE));
		JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO, Icons.LOGO_PATH, 250, 250).ifPresent(stage.getIcons()::add);
		stage.setResizable(false);
		CreateProjectWindow node = new CreateProjectWindow(stage);

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(JamsApplication.getStage());

		AnchorPane background = new AnchorPane();
		background.getStyleClass().add("window-popup-background");
		AnchorUtils.setAnchor(node, 0, 0, 0, 0);

		background.setPrefWidth(WIDTH);

		background.getChildren().add(node);
		ThemedScene scene = new ThemedScene(background);

		stage.setScene(scene);
		background.setPadding(new Insets(5));
		background.applyCss();
		background.layout();

		Platform.runLater(() -> {
			Stage main = JamsApplication.getStage();
			stage.setX(main.getX() + main.getWidth() / 2 - background.getWidth() / 2);
			stage.setY(main.getY() + main.getHeight() / 2 - background.getHeight() / 2);
		});

		stage.showAndWait();
	}
}
