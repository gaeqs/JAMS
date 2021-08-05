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
 */
public class BarSnapshotViewModeWindow implements BarSnapshotViewMode {

    public static final String NAME = "window";
    public static final BarSnapshotViewModeWindow INSTANCE = new BarSnapshotViewModeWindow();

    private BarSnapshotViewModeWindow() {
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
        return "BAR_VIEW_MODE_WINDOW";
    }

    private static class Window extends Stage implements BarSnapshotHolder {

        private BarSnapshot snapshot;

        @Override
        public boolean show(BarButton button) {
            snapshot = button.getSnapshot();
            setTitle(Jams.getLanguageManager().getSelected().getOrDefault(snapshot.getLanguageNode().orElse(null)));
            Icons.LOGO.getImage().ifPresent(getIcons()::add);

            var anchor = new AnchorPane(snapshot.getNode());
            AnchorUtils.setAnchor(snapshot.getNode(), 0, 0, 0, 0);
            var scene = new ThemedScene(anchor);

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
            setTitle(event.getNewLanguage().getOrDefault(snapshot.getLanguageNode().orElse(null)));
        }

        @Listener
        private void onLanguageChange(DefaultLanguageChangeEvent.After event) {
            setTitle(Jams.getLanguageManager().getSelected().getOrDefault(snapshot.getLanguageNode().orElse(null)));
        }
    }
}
