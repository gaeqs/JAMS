package net.jamsimulator.jams.gui.popup;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;

public class NewFolderWindow extends VBox {

	public static int WIDTH = 300;
	public static int HEIGHT = 50;

	private NewFolderWindow(Stage stage, File folder) {
		getStyleClass().add("v-box");
		Validate.notNull(folder, "Folder cannot be null!");
		Validate.isTrue(folder.isDirectory(), "Folder must be a directory!");
		setAlignment(Pos.BOTTOM_CENTER);
		getChildren().add(new LanguageLabel(Messages.EXPLORER_ITEM_ACTION_WINDOW_NEW_FOLDER));

		TextField field = new TextField();
		getChildren().add(field);

		field.setOnAction(event -> {
			if (field.getText().isEmpty()) {
				stage.close();
				return;
			}
			File file = new File(folder, field.getText());
			try {
				if (!file.mkdirs()) throw new IOException("Error creating folder.");
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
		PopupWindowHelper.open(stage, new NewFolderWindow(stage, folder), WIDTH, HEIGHT, true);
	}
}
