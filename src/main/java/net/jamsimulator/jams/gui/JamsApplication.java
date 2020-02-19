package net.jamsimulator.jams.gui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.font.FontLoader;
import net.jamsimulator.jams.gui.main.MainAnchorPane;

public class JamsApplication extends Application {

	private static final int WIDTH = 1200, HEIGHT = 800;
	private static Stage stage;

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		FontLoader.load();
		primaryStage.setTitle("JAMS (Just Another MIPS Simulator)");
		AnchorPane pane = new MainAnchorPane();

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

	public static Stage getStage() {
		return stage;
	}

	public static void start(String[] args) {
		launch(args);
	}

}