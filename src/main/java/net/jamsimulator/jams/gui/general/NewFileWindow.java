package net.jamsimulator.jams.gui.general;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;

public class NewFileWindow extends VBox {

	public static int WIDTH = 300;
	public static int HEIGHT = 50;

	private NewFileWindow(Stage stage, File folder) {
		getStyleClass().add("v-box");
		Validate.notNull(folder, "Folder cannot be null!");
		Validate.isTrue(folder.isDirectory(), "Folder must be a directory!");
		setAlignment(Pos.BOTTOM_CENTER);
		getChildren().add(new Label("New file"));

		TextField field = new TextField();
		getChildren().add(field);

		field.setOnAction(event -> {
			if (field.getText().isEmpty()) {
				stage.close();
				return;
			}
			File file = new File(folder, field.getText());
			try {
				file.createNewFile();
				stage.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				stage.close();
				event.consume();
			}
		});

	}

	public static void open(File folder) {
		Stage stage = new Stage();
		PopupWindowHelper.open(stage, new NewFileWindow(stage, folder), WIDTH, HEIGHT);
	}
}
