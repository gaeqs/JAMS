package net.jamsimulator.jams.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.font.FontLoader;
import net.jamsimulator.jams.gui.icon.IconManager;
import net.jamsimulator.jams.gui.main.MainAnchorPane;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.manager.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class JamsApplication extends Application {


	private static final int WIDTH = 1200, HEIGHT = 800;
	private static final int MIN_WIDTH = 20, MIN_HEIGHT = 20;
	private static Stage stage;
	private static Scene scene;

	private static List<EventHandler<WindowEvent>> closeListeners;

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		FontLoader.load();
		primaryStage.setTitle("JAMS (Just Another MIPS Simulator)");

		closeListeners = new ArrayList<>();
		stage.setOnCloseRequest(event -> closeListeners.forEach(target -> target.handle(event)));

		AnchorPane pane = new MainAnchorPane();

		scene = new ThemedScene(pane);

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
	 * Returns the {@link ThemeManager}.
	 *
	 * @return the {@link ThemeManager}.
	 */
	public static ThemeManager getThemeManager() {
		return ThemeManager.INSTANCE;
	}

	/**
	 * Adds a listener that will be invoked when the main stage is closed.
	 *
	 * @param listener the listener.
	 */
	public static void addStageCloseListener(EventHandler<WindowEvent> listener) {
		closeListeners.add(listener);
	}


	/**
	 * Removed a listener that would be invoked when the main stage is closed.
	 *
	 * @param listener the listener.
	 */
	public static void removeStageCloseListener(EventHandler<WindowEvent> listener) {
		closeListeners.remove(listener);
	}

	public static void main(String[] args) {
		launch(args);
	}
}