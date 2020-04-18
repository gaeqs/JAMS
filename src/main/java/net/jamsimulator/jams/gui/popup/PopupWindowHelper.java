package net.jamsimulator.jams.gui.popup;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.utils.AnchorUtils;

class PopupWindowHelper {

	static void open(Stage stage, Node node, int width, int height, boolean transparent) {
		if (transparent) {
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.focusedProperty().addListener((obs, old, val) -> {
				if (!val) stage.close();
			});
		} else {
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setResizable(false);
		}

		stage.initOwner(JamsApplication.getStage());

		AnchorPane background = new AnchorPane();
		AnchorUtils.setAnchor(node, 0, 0, 0, 0);

		background.getChildren().add(node);
		ThemedScene scene = new ThemedScene(background);

		stage.setScene(scene);
		stage.setOnCloseRequest(event -> scene.unregisterJamsListeners());

		if (node instanceof Region) {
			node.applyCss();
			((Region) node).layout();
		}

		stage.show();
		stage.centerOnScreen();
	}

}
