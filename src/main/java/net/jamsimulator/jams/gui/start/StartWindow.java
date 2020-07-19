package net.jamsimulator.jams.gui.start;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.MainScene;
import net.jamsimulator.jams.utils.AnchorUtils;

public class StartWindow extends AnchorPane {

	private final VBox contents;

	private StartWindow() {
		getStyleClass().add("anchor-pane");

		contents = new VBox();
		AnchorUtils.setAnchor(contents, 0, 0, 0, 0);
		getChildren().add(contents);

		contents.setAlignment(Pos.CENTER);
		contents.setSpacing(8);

		loadImage();
		loadTitle();
	}


	private void loadImage() {
		Image image = JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO, Icons.LOGO_PATH,
				1024, 1024).orElse(null);
		contents.getChildren().add(new NearestImageView(image, 150, 150));
	}

	private void loadTitle() {
		Label label = new Label("JAMS");
		label.getStyleClass().add("start-title");
		contents.getChildren().add(label);
	}

	public static void open() {
		Stage stage = new Stage();
		stage.setScene(new MainScene(new StartWindow(), 400, 400));
		//stage.initStyle(StageStyle.TRANSPARENT);
		stage.show();
	}
}
