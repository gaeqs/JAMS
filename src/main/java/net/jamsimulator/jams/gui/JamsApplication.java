package net.jamsimulator.jams.gui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.main.JamsMainAnchorPane;

public class JamsApplication extends Application {

	private static final int WIDTH = 1200, HEIGHT = 800;

	@Override
	public void start(Stage primaryStage) {
		AnchorPane pane = new JamsMainAnchorPane();

		pane.getStylesheets().add("gui/style/dark_style.css");

		Scene scene = new Scene(pane);

		primaryStage.setScene(scene);
		primaryStage.setMinWidth(WIDTH);
		primaryStage.setMinHeight(HEIGHT);

		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

		double x = bounds.getMinX() + (bounds.getWidth() - WIDTH) / 2;
		double y = bounds.getMinY() + (bounds.getHeight() - HEIGHT) / 2;

		primaryStage.setX(x);
		primaryStage.setY(y);

		primaryStage.show();
	}

	public static void start(String[] args) {
		launch(args);
	}

}