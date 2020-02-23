package net.jamsimulator.jams.gui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.font.FontLoader;
import net.jamsimulator.jams.gui.icon.FileIconManager;
import net.jamsimulator.jams.gui.icon.IconManager;
import net.jamsimulator.jams.gui.main.MainAnchorPane;

public class JamsApplication extends Application {

	private static final int WIDTH = 1200, HEIGHT = 800;
	private static final int MIN_WIDTH = 20, MIN_HEIGHT = 20;
	private static Stage stage;
	private static Scene scene;

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		FontLoader.load();
		primaryStage.setTitle("JAMS (Just Another MIPS Simulator)");
		AnchorPane pane = new MainAnchorPane();

		pane.getStylesheets().add("gui/style/dark_style.css");

		scene = new Scene(pane);

		primaryStage.setScene(scene);
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);

		primaryStage.setMinWidth(MIN_WIDTH);
		primaryStage.setMinHeight(MIN_HEIGHT);

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

	public static Scene getScene() {
		return scene;
	}

	public static void start(String[] args) {
		launch(args);
	}

	/**
	 * Returns the {@link IconManager}.
	 *
	 * @return the {@link IconManager}.
	 */
	public static IconManager getIconManager() {
		return IconManager.INSTANCE;
	}

	/**
	 * Returns the {@link FileIconManager}.
	 *
	 * @return the {@link FileIconManager}.
	 */
	public static FileIconManager getFileIconManager() {
		return FileIconManager.INSTANCE;
	}
}