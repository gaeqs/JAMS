package net.jamsimulator.jams.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.jamsimulator.jams.gui.font.FontLoader;
import net.jamsimulator.jams.gui.icon.IconManager;
import net.jamsimulator.jams.gui.main.MainAnchorPane;
import net.jamsimulator.jams.gui.project.ProjectsTabPane;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.manager.ActionManager;
import net.jamsimulator.jams.manager.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class JamsApplication extends Application {

	private static final int WIDTH = 1200, HEIGHT = 800;
	private static final int MIN_WIDTH = 20, MIN_HEIGHT = 20;

	private static Stage stage;
	private static Scene scene;
	private static MainAnchorPane mainAnchorPane;

	private static List<EventHandler<WindowEvent>> closeListeners;

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		FontLoader.load();
		primaryStage.setTitle("JAMS (Just Another MIPS Simulator)");

		closeListeners = new ArrayList<>();
		stage.setOnCloseRequest(event -> closeListeners.forEach(target -> target.handle(event)));

		mainAnchorPane = new MainAnchorPane();

		scene = new ThemedScene(mainAnchorPane);
		getActionManager().addAcceleratorsToScene(scene, false);

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

	/**
	 * Returns tha main {@link Stage} of the JAMS's GUI.
	 *
	 * @return the main {@link Stage}.
	 */
	public static Stage getStage() {
		return stage;
	}

	/**
	 * Returns the JAMS's main {@link Scene}.
	 *
	 * @return the main {@link Scene}.
	 */
	public static Scene getScene() {
		return scene;
	}

	/**
	 * Returns the {@link javafx.scene.layout.AnchorPane} inside the main {@link Scene}.
	 *
	 * @return the {@link javafx.scene.layout.AnchorPane}.
	 */
	public static MainAnchorPane getMainAnchorPane() {
		return mainAnchorPane;
	}

	/**
	 * Returns the {@link MenuBar} located at the top of the main {@link Scene}.
	 *
	 * @return the {@link MenuBar}.
	 */
	public static MenuBar getTopMenuBar() {
		return mainAnchorPane.getTopMenuBar();
	}

	/**
	 * Returns the {@link ProjectsTabPane}.
	 *
	 * @return the {@link ProjectsTabPane}.
	 */
	public static ProjectsTabPane getProjectsTabPane() {
		return mainAnchorPane.getProjectsTabPane();
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
	 * Returns the {@link ActionManager}.
	 *
	 * @return the {@link ActionManager}.
	 */
	public static ActionManager getActionManager() {
		return ActionManager.INSTANCE;
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