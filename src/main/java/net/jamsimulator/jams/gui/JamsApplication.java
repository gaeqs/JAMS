/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.general.JAMSApplicationPostInitEvent;
import net.jamsimulator.jams.gui.font.FontLoader;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.BorderlessMainScene;
import net.jamsimulator.jams.gui.main.MainAnchorPane;
import net.jamsimulator.jams.gui.main.MainScene;
import net.jamsimulator.jams.gui.project.ProjectListTabPane;
import net.jamsimulator.jams.gui.start.StartWindow;
import net.jamsimulator.jams.gui.action.ActionManager;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModeManager;
import net.jamsimulator.jams.gui.theme.ThemeManager;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

public class JamsApplication extends Application {

    private static final int WIDTH = 1200, HEIGHT = 800;
    private static final int MIN_WIDTH = 20, MIN_HEIGHT = 20;

    private static boolean loaded = false;

    private static Stage stage;
    private static Scene scene;
    private static MainAnchorPane mainAnchorPane;

    private static ContextMenu lastContextMenu;

    /**
     * Returns whether the application has finished loading.
     *
     * @return whether the application has finished loading.
     */
    public static boolean isLoaded() {
        return loaded;
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
     * Returns the {@link BarSnapshotViewModeManager}.
     *
     * @return the {@link BarSnapshotViewModeManager}.
     */
    public static BarSnapshotViewModeManager getBarSnapshotViewModeManager() {
        return BarSnapshotViewModeManager.INSTANCE;
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

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        FontLoader.load();
        Jams.REGISTRY.loadJAMSApplicationManagers();

        primaryStage.setTitle("JAMS (Just Another MIPS Simulator)");

        Optional<Boolean> useBorderless = Jams.getMainConfiguration().data().get("appearance.hide_top_bar");
        boolean transparent = useBorderless.orElse(false);
        mainAnchorPane = new MainAnchorPane(stage, transparent);

        if (transparent) {
            stage.initStyle(StageStyle.TRANSPARENT);
            scene = new BorderlessMainScene(stage, mainAnchorPane);
            ((BorderlessScene) scene).setMoveControl(mainAnchorPane.getTopBar().getMenuBar());
        } else {
            scene = new MainScene(mainAnchorPane, -1, -1, true);
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

        Icons.LOGO.getImage().ifPresent(primaryStage.getIcons()::add);
        Jams.getMainConfiguration().data().registerListeners(this, true);

        Jams.getGeneralEventBroadcast().callEvent(new JAMSApplicationPostInitEvent());

        // Load projects
        getProjectsTabPane().openSavedProjects();

        loaded = true;

        if (getProjectsTabPane().getProjects().isEmpty()) {
            StartWindow.INSTANCE.open();
            if (!getProjectsTabPane().getProjects().isEmpty()) {
                stage.show();
            }
        } else {
            stage.show();
        }
    }
}