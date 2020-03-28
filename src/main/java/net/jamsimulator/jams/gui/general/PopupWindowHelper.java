package net.jamsimulator.jams.gui.general;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.utils.AnchorUtils;

class PopupWindowHelper {

	static void open(Stage stage, Node node, int width, int height) {
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.initOwner(JamsApplication.getStage());
		stage.setWidth(width);
		stage.setHeight(height);


		Stage main = JamsApplication.getStage();

		stage.setX(main.getX() + main.getWidth() / 2 - (width >> 1));
		stage.setY(main.getY() + main.getHeight() / 2 - (height >> 1));

		AnchorPane background = new AnchorPane();
		AnchorUtils.setAnchor(node, 0, 0, 0, 0);

		background.getChildren().add(node);
		ThemedScene scene = new ThemedScene(background);

		stage.setScene(scene);
		stage.show();

		stage.setOnCloseRequest(event -> scene.unregisterJamsListeners());

		stage.focusedProperty().addListener((obs, old, val) -> {
			if (!val) stage.close();
		});
	}

}
