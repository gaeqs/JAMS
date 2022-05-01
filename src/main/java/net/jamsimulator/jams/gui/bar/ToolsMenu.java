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

package net.jamsimulator.jams.gui.bar;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.context.MainMenuRegion;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.wrapper.LanguageCheckMenuItem;
import net.jamsimulator.jams.language.wrapper.LanguageMenu;

/**
 * Represents a {@link javafx.scene.control.Menu} representing
 * all {@link BarSnapshot snapshots} of the current {@link WorkingPane}.
 */
public class ToolsMenu extends LanguageMenu {

    public static final int ICON_SIZE = 16;

    public ToolsMenu() {
        super(MainMenuRegion.TOOLS.getLanguageNode());

        // An empty item should be added for the menu to work.
        getItems().add(new MenuItem(""));

        setOnShowing(event -> refresh());
    }

    private void refresh() {
        getItems().clear();

        var optionalProject = JamsApplication.getProjectsTabPane().getFocusedProject();
        if (optionalProject.isEmpty()) return;
        var projectTab = optionalProject.get();
        var tab = projectTab.getProjectTabPane().getSelectionModel().getSelectedItem();

        if (tab == null || !(tab.getContent() instanceof WorkingPane)) return;

        ((WorkingPane) tab.getContent()).getBarMap().getRegisteredSnapshots().forEach(snapshot -> {

            var item = snapshot.getLanguageNode()
                    .map(v -> (CheckMenuItem) new LanguageCheckMenuItem(v))
                    .orElseGet(() -> new CheckMenuItem(snapshot.getName()));
            item.setSelected(snapshot.isEnabled());

            snapshot.getIcon().ifPresent(target -> item.setGraphic(new QualityImageView(target, ICON_SIZE, ICON_SIZE)));

            getItems().add(item);

            item.selectedProperty().addListener((obs, old, val) -> Jams.getMainConfiguration()
                    .data().set(String.format(BarSnapshot.CONFIGURATION_NODE_ENABLED, snapshot.getName()), val));
        });

        // An empty item should be added for the menu to work.
        if (getItems().isEmpty()) {
            getItems().add(new MenuItem(""));
        }
    }
}
