/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import net.jamsimulator.jams.gui.image.icon.IconManager;
import net.jamsimulator.jams.gui.main.MainAnchorPane;
import net.jamsimulator.jams.gui.main.MainScene;
import net.jamsimulator.jams.gui.project.ProjectListTabPane;
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

		scene = new MainScene(mainAnchorPane);
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
	 * Returns the {@link ProjectListTabPane}.
	 *
	 * @return the {@link ProjectListTabPane}.
	 */
	public static ProjectListTabPane getProjectsTabPane() {
		return mainAnchorPane.getProjectListTabPane();
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