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

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.font.FontLoader;
import net.jamsimulator.jams.gui.image.icon.IconManager;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.BorderlessMainScene;
import net.jamsimulator.jams.gui.main.MainAnchorPane;
import net.jamsimulator.jams.gui.main.MainScene;
import net.jamsimulator.jams.gui.project.ProjectListTabPane;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.start.StartWindow;
import net.jamsimulator.jams.manager.ActionManager;
import net.jamsimulator.jams.manager.ThemeManager;
import net.jamsimulator.jams.utils.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JamsApplication extends Application {

	private static final int WIDTH = 1200, HEIGHT = 800;
	private static final int MIN_WIDTH = 20, MIN_HEIGHT = 20;

	private static Stage stage;
	private static Scene scene;
	private static MainAnchorPane mainAnchorPane;

	private static List<EventHandler<WindowEvent>> closeListeners;

	private static ContextMenu lastContextMenu;

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;

		FontLoader.load();
		primaryStage.setTitle("JAMS (Just Another MIPS Simulator)");

		closeListeners = new ArrayList<>();

		Optional<Boolean> useBorderless = Jams.getMainConfiguration().get("appearance.hide_top_bar");
		boolean transparent = useBorderless.orElse(false);
		mainAnchorPane = new MainAnchorPane(stage, transparent);

		if (transparent) {
			stage.initStyle(StageStyle.TRANSPARENT);
			scene = new BorderlessMainScene(stage, mainAnchorPane);
			((BorderlessScene) scene).setMoveControl(mainAnchorPane.getTopBar().getMenuBar());
		} else {
			scene = new MainScene(mainAnchorPane);
		}

		stage.setScene(scene);
		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT);

		stage.setMinWidth(MIN_WIDTH);
		stage.setMinHeight(MIN_HEIGHT);

		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

		double x = bounds.getMinX() + (bounds.getWidth() - WIDTH) / 2;
		double y = bounds.getMinY() + (bounds.getHeight() - HEIGHT) / 2;

		stage.setX(x);
		stage.setY(y);


		getIconManager().getOrLoadSafe(Icons.LOGO, Icons.LOGO_PATH, 250, 250).ifPresent(primaryStage.getIcons()::add);
		if(getProjectsTabPane().getProjects().isEmpty()) {
			StartWindow.open();
			if(!getProjectsTabPane().getProjects().isEmpty()) {
				stage.show();
			}
		} else {
			stage.show();
		}

		stage.setOnCloseRequest(event -> {
			closeListeners.forEach(target -> target.handle(event));
			onClose();
		});
		Jams.getMainConfiguration().registerListeners(this, true);
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

	/**
	 * Opens the given {@link ContextMenu}, hiding the last open context menu.
	 *
	 * @param menu   the {@link ContextMenu} to open.
	 * @param parent the parent's {@link Node}.
	 * @param x      the x pos.
	 * @param y      the y pos.
	 */
	public static void openContextMenu(ContextMenu menu, Node parent, double x, double y) {
		Validate.notNull(menu, "Menu cannot be null!");
		Validate.notNull(parent, "Parent cannot be null!");
		hideContextMenu();
		lastContextMenu = menu;
		lastContextMenu.show(parent, x, y);
	}

	/**
	 * Hides the current context menu, if present.
	 */
	public static void hideContextMenu() {
		if (lastContextMenu != null) {
			lastContextMenu.hide();
			lastContextMenu = null;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}


	private static void onClose() {
		getProjectsTabPane().saveOpenProjects();
		for (ProjectTab project : getProjectsTabPane().getProjects()) {
			project.getProject().onClose();
		}

		//Avoids exit lag.
		new Thread(() -> System.exit(0)).start();
	}
}