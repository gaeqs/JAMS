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

package net.jamsimulator.jams.gui.main;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.context.ActionMenuItem;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMainMenuBuilder;
import net.jamsimulator.jams.gui.action.context.MainMenuRegion;
import net.jamsimulator.jams.gui.action.defaults.general.GeneralActionOpenProject;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnbindEvent;
import net.jamsimulator.jams.gui.bar.ToolsMenu;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenu;
import net.jamsimulator.jams.manager.event.ManagerDefaultElementChangeEvent;
import net.jamsimulator.jams.manager.event.ManagerSelectedElementChangeEvent;
import net.jamsimulator.jams.project.ProjectSnapshot;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * The main {@link MenuBar}.
 */
public class MainMenuBar extends MenuBar {

    public static final int MAX_RECENT_PROJECTS = 10;

    public MainMenuBar() {
        refresh();
        Jams.getLanguageManager().registerListeners(this, true);
        JamsApplication.getActionManager().registerListeners(this, true);
    }

    public void refresh() {
        getMenus().clear();
        Set<MainMenuRegion> set = new HashSet<>();
        set.add(MainMenuRegion.TOOLS);

        for (Action action : JamsApplication.getActionManager()) {
            if (!(action instanceof ContextAction context) || context.getMainMenuRegion().isEmpty()) continue;
            set.add(context.getMainMenuRegion().get());
        }

        set.stream().sorted(Comparator.comparingInt(MainMenuRegion::getPriority)).forEach(this::createMenu);
    }

    private void createMenu(MainMenuRegion region) {
        if (region == MainMenuRegion.TOOLS) {
            createToolsMenu();
            return;
        }
        Set<ContextAction> set = getSupportedContextActions(region);
        if (set.isEmpty()) return;
        Menu main = new ContextActionMainMenuBuilder(region.getLanguageNode()).addAll(set).build();

        if (region == MainMenuRegion.FILE) {
            modifyFileMenu(main);
        }

        main.setOnShowing(event -> {
            for (MenuItem item : main.getItems()) {
                if (item instanceof ActionMenuItem action) {
                    item.setDisable(!action.getAction().supportsMainMenuState(this));
                }
            }
        });

        getMenus().add(main);
    }

    private void createToolsMenu() {
        getMenus().add(new ToolsMenu());
    }

    private void modifyFileMenu(Menu menu) {
        int index = 0;

        for (MenuItem item : menu.getItems()) {
            index++;
            if (item instanceof ActionMenuItem ami && ami.getAction() instanceof GeneralActionOpenProject) {
                break;
            }
        }

        var recentMenu = new LanguageMenu(Messages.ACTION_GENERAL_OPEN_RECENT);
        recentMenu.getItems().add(new MenuItem(""));
        recentMenu.setOnShowing(event -> {
            recentMenu.getItems().clear();
            for (ProjectSnapshot project : Jams.getRecentProjects()) {
                var file = new File(project.path());
                if (JamsApplication.getProjectsTabPane().isProjectOpen(file)) continue;
                var item = new MenuItem(project.name());
                item.setOnAction(action ->
                        Jams.getProjectTypeManager().getByProjectfolder(file).ifPresent(type ->
                                JamsApplication.getProjectsTabPane().openProject(type.loadProject(file))));
                recentMenu.getItems().add(item);
                if (recentMenu.getItems().size() >= MAX_RECENT_PROJECTS) break;
            }

            if (recentMenu.getItems().isEmpty()) {
                recentMenu.getItems().add(new MenuItem(""));
            }
        });

        menu.getItems().add(index, recentMenu);
    }

    private Set<ContextAction> getSupportedContextActions(MainMenuRegion region) {
        Set<Action> actions = JamsApplication.getActionManager();
        Set<ContextAction> set = new HashSet<>();
        for (Action action : actions) {
            if (action instanceof ContextAction && region.equals(((ContextAction) action).getMainMenuRegion().orElse(null))) {
                set.add((ContextAction) action);
            }
        }
        return set;
    }

    @Listener
    private void onLanguageChange(ManagerSelectedElementChangeEvent.After<Language> event) {
        Platform.runLater(this::refresh);
    }

    @Listener
    private void onLanguageChange(ManagerDefaultElementChangeEvent.After<Language> event) {
        Platform.runLater(this::refresh);
    }

    @Listener
    private void onActionBind(ActionBindEvent.After event) {
        Platform.runLater(this::refresh);
    }

    @Listener
    private void onActionUnbind(ActionUnbindEvent.After event) {
        Platform.runLater(this::refresh);
    }
}
