package net.jamsimulator.jams.gui.popup;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConfirmationWindow extends VBox {

	public static int WIDTH = 400;
	public static int HEIGHT = 200;

	private ConfirmationWindow(Stage stage, String message, Runnable onYes, Runnable onCancel) {
		getStyleClass().add("v-box");
		Label label = new Label(message);
		label.setPadding(new Insets(10));
		getChildren().add(label);

		Button yes = new Button("Yes");
		yes.setOnAction(event -> {
			stage.close();
			event.consume();
			onYes.run();
		});

		Button cancel = new Button("Cancel");
		cancel.setOnAction(event -> {
			stage.close();
			event.consume();
			onCancel.run();
		});

		HBox box = new HBox();
		box.setSpacing(10);
		box.setPadding(new Insets(5));
		box.getStyleClass().add("h-box");


		Region vRegion = new Region();
		VBox.setVgrow(vRegion, Priority.ALWAYS);
		getChildren().add(vRegion);

		Region hRegion = new Region();
		box.getChildren().addAll(hRegion, yes, cancel);
		HBox.setHgrow(hRegion, Priority.ALWAYS);
		getChildren().add(box);

		setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				stage.close();
				event.consume();
				onCancel.run();
			}
		});

	}

	public static void open(String message, Runnable onYes, Runnable onCancel) {
		Stage stage = new Stage();
		stage.setTitle(message);
		PopupWindowHelper.open(stage, new ConfirmationWindow(stage, message, onYes, onCancel), -1, -1, false);
	}
}
