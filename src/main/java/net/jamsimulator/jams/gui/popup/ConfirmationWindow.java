package net.jamsimulator.jams.gui.popup;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;

public class ConfirmationWindow extends VBox {

	private ConfirmationWindow(Stage stage, String message, Runnable onOk, Runnable onCancel) {
		getStyleClass().add("v-box");
		Label label = new Label(message);
		label.setPrefWidth(500);
		label.setWrapText(true);
		label.setPadding(new Insets(10));
		getChildren().add(label);

		LanguageButton ok = new LanguageButton(Messages.GENERAL_OK);
		LanguageButton cancel = new LanguageButton(Messages.GENERAL_CANCEL);

		ok.setOnAction(event -> {
			stage.close();
			ok.dispose();
			cancel.dispose();

			event.consume();
			onOk.run();
		});

		cancel.setOnAction(event -> {
			stage.close();
			ok.dispose();
			cancel.dispose();

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
		box.getChildren().addAll(hRegion, ok, cancel);
		HBox.setHgrow(hRegion, Priority.ALWAYS);
		getChildren().add(box);

		setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				stage.close();
				ok.dispose();
				cancel.dispose();
				event.consume();
				onCancel.run();
			}
		});

	}

	public static void open(String message, Runnable onYes, Runnable onCancel) {
		Stage stage = new Stage();
		stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.GENERAL_CONFIRMATION));
		PopupWindowHelper.open(stage, new ConfirmationWindow(stage, message, onYes, onCancel), -1, -1, false);
	}
}
