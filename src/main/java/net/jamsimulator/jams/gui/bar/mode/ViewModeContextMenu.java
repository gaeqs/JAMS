package net.jamsimulator.jams.gui.bar.mode;

import javafx.scene.control.ContextMenu;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.bar.BarPaneSnapshot;
import net.jamsimulator.jams.gui.bar.mode.event.BarSnapshotViewModeRegisterEvent;
import net.jamsimulator.jams.gui.bar.mode.event.BarSnapshotViewModeUnregisterEvent;
import net.jamsimulator.jams.language.wrapper.LanguageCheckMenuItem;
import net.jamsimulator.jams.manager.Labeled;

import java.util.Comparator;

public class ViewModeContextMenu extends ContextMenu {

    private final BarPaneSnapshot snapshot;

    public ViewModeContextMenu(BarPaneSnapshot snapshot) {
        this.snapshot = snapshot;
        JamsApplication.getBarSnapshotViewModeManager().stream()
                .sorted((Comparator.comparing(Labeled::getName)))
                .forEach(mode -> getItems().add(new Item(mode)));

        JamsApplication.getBarSnapshotViewModeManager().registerListeners(this, true);

        setOnShowing(event -> getItems().forEach(item -> {
            if(item instanceof Item) ((Item) item)
                    .setSelected(this.snapshot.getViewMode().equals(((Item) item).viewMode));
        }));
    }

    @Listener
    private void onViewModeRegister(BarSnapshotViewModeRegisterEvent.After event) {
        getItems().clear();
        JamsApplication.getBarSnapshotViewModeManager().stream()
                .sorted((Comparator.comparing(Labeled::getName)))
                .forEach(mode -> getItems().add(new Item(mode)));
    }

    @Listener
    private void onViewModeUnregister(BarSnapshotViewModeUnregisterEvent.After event) {
        getItems().removeIf(item -> item instanceof Item && ((Item) item).viewMode.equals(event.getViewMode()));
    }

    private class Item extends LanguageCheckMenuItem {

        final BarSnapshotViewMode viewMode;

        public Item(BarSnapshotViewMode viewMode) {
            super(viewMode.getLanguageNode());
            this.viewMode = viewMode;

            setOnAction(event -> Jams.getMainConfiguration().set(
                    String.format(BarPaneSnapshot.CONFIGURATION_NODE_VIEW_MODE, snapshot.getName()),
                    viewMode.getName()));
        }
    }

}
