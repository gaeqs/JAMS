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

import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.bar.BarSnapshot;
import net.jamsimulator.jams.gui.bar.BarSnapshotHolder;
import net.jamsimulator.jams.manager.ManagerResource;

import java.util.Optional;

/**
 * Represents the way a {@link BarSnapshot snapshot} should be presented to the user.
 * <p>
 * Instances of this class manages the view of the snapshot when required. They also return the viewer holding
 * the node as a {@link BarSnapshotHolder}. This holder will be called when the snapshot should hide.
 */
public interface BarSnapshotViewMode extends ManagerResource {

    /**
     * Manages the visualization of the {@link BarSnapshot snapshot} inside the given {@link BarButton}.
     *
     * @param button the button.
     * @return the {@link BarSnapshotHolder} holding the node.
     */
    Optional<BarSnapshotHolder> manageView(BarButton button);

    /**
     * The language node of this view mode.
     * The message of this node is used in the view mode selection menu.
     *
     * @return the language node.
     */
    String getLanguageNode();
}
