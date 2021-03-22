package net.jamsimulator.jams.gui.bar;

import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.context.MainMenuRegion;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.wrapper.LanguageCheckMenuItem;
import net.jamsimulator.jams.language.wrapper.LanguageMenu;

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
            var item = new LanguageCheckMenuItem(snapshot.getLanguageNode());
            item.setSelected(snapshot.isEnabled());

            if (snapshot.getIcon() != null) {
                item.setGraphic(new NearestImageView(snapshot.getIcon(), ICON_SIZE, ICON_SIZE));
            }

            getItems().add(item);

            item.selectedProperty().addListener((obs, old, val) -> Jams.getMainConfiguration()
                    .set(String.format(BarSnapshot.CONFIGURATION_NODE_ENABLED, snapshot.getName()), val));
        });

        // An empty item should be added for the menu to work.
        if (getItems().isEmpty()) {
            getItems().add(new MenuItem(""));
        }
    }
}
