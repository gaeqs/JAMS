package net.jamsimulator.jams.gui.about;

import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.gui.util.AnchorUtils;

public class AboutWindow extends AnchorPane {

	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;
	private static final int IMAGE_SIZE = 150;

	public AboutWindow() {
		getStyleClass().add("anchor-pane");
		var contents = new VBox();
		AnchorUtils.setAnchor(contents, 0, 0,0,0);
		getChildren().add(contents);

		contents.setAlignment(Pos.CENTER);
		contents.setSpacing(5);

		var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO).orElse(null);
		contents.getChildren().add(new NearestImageView(icon, IMAGE_SIZE, IMAGE_SIZE));

		var label = new LanguageLabel(Messages.ABOUT, "{VERSION}", Jams.getVersion());
		label.getStyleClass().add("about-text");
		contents.getChildren().add(label);

	}


	public static void open() {
		var content = new AboutWindow();
		var stage = new Stage();
		var scene = new ThemedScene(content);
		stage.initOwner(JamsApplication.getStage());
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.setResizable(false);

		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT);
		stage.setMinWidth(WIDTH >> 1);
		stage.setMinHeight(0);

		Stage main = JamsApplication.getStage();

		stage.setX(main.getX() + main.getWidth() / 2 - (WIDTH >> 1));
		stage.setY(main.getY() + main.getHeight() / 2 - (HEIGHT >> 1));

		stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.MAIN_MENU_HELP_ABOUT));
		JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO)
				.ifPresent(stage.getIcons()::add);


		scene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});

		JamsApplication.getActionManager().addAcceleratorsToScene(scene, true);
		Jams.getLanguageManager().registerListeners(content, true);

		stage.show();
	}
}
