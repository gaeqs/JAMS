package net.jamsimulator.jams.gui.bar.mode;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.bar.BarSnapshot;
import net.jamsimulator.jams.gui.bar.BarSnapshotHolder;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;

import java.util.Optional;

/**
 * Represents a {@link BarSnapshot snapshot}'s representation mode where
 * its content is placed in a persistent window.
 * <p>
 * A persistent window will always be on top of the main window.
 */
public class BarSnapshotViewModePersistentWindow implements BarSnapshotViewMode {

    public static final String NAME = "persistent_window";
    public static final BarSnapshotViewModePersistentWindow INSTANCE = new BarSnapshotViewModePersistentWindow();

    private BarSnapshotViewModePersistentWindow() {
    }

    @Override
    public Optional<BarSnapshotHolder> manageView(BarButton button) {
        var window = new Window();
        return window.show(button) ? Optional.of(window) : Optional.empty();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLanguageNode() {
        return "BAR_VIEW_MODE_PERSISTENT_WINDOW";
    }

    private static class Window extends Stage implements BarSnapshotHolder {

        private BarSnapshot snapshot;

        @Override
        public boolean show(BarButton button) {
            snapshot = button.getSnapshot();
            setTitle(Jams.getLanguageManager().getSelected().getOrDefault(snapshot.getLanguageNode()));
            JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO).ifPresent(getIcons()::add);

            var anchor = new AnchorPane(snapshot.getNode());
            AnchorUtils.setAnchor(snapshot.getNode(), 0, 0, 0, 0);
            var scene = new ThemedScene(anchor);

            initOwner(JamsApplication.getStage());
            setScene(scene);
            setOnCloseRequest(event -> hide(button));

            setWidth(800);
            setHeight(600);
            show();

            Jams.getLanguageManager().registerListeners(this, true);

            return true;
        }

        @Override
        public boolean hide(BarButton button) {
            hide();
            button.forceHide();
            return true;
        }

        @Listener
        private void onLanguageChange(SelectedLanguageChangeEvent.After event) {
            setTitle(event.getNewLanguage().getOrDefault(snapshot.getLanguageNode()));
        }

        @Listener
        private void onLanguageChange(DefaultLanguageChangeEvent.After event) {
            setTitle(Jams.getLanguageManager().getSelected().getOrDefault(snapshot.getLanguageNode()));
        }
    }
}
