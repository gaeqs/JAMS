/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.gui.explorer;

import javafx.scene.layout.Region;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;

/**
 * Small class used to add padding to an {@link Explorer}.
 */
public class ExplorerSeparatorRegion extends Region {

    public static final String HIERARCHY_SEPARATOR_SECTION_NODE = "explorer.section_separator_width";
    public static final String HIERARCHY_SEPARATOR_ELEMENT_NODE = "explorer.element_separator_width";

    private final boolean section;

    private int hierarchyLevel;
    private boolean hasHierarchyLevel;

    public ExplorerSeparatorRegion(boolean section, int hierarchyLevel) {
        this.section = section;
        this.hierarchyLevel = hierarchyLevel;
        this.hasHierarchyLevel = true;
        setHierarchyLevel(hierarchyLevel);
        Jams.getMainConfiguration().registerListeners(this, true);
    }

    public ExplorerSeparatorRegion(double width) {
        section = false;
        hierarchyLevel = -1;
        hasHierarchyLevel = false;
        setPrefWidth(width);
    }

    public void setHierarchyLevel(int hierarchyLevel) {
        if (!hasHierarchyLevel) {
            Jams.getMainConfiguration().registerListeners(this, true);
        }

        hasHierarchyLevel = true;
        this.hierarchyLevel = hierarchyLevel;

        updateLevel();
    }

    private void updateLevel() {
        var config = Jams.getMainConfiguration();

        double width;

        double folderWidth = config.getNumber(HIERARCHY_SEPARATOR_SECTION_NODE).orElse(0.0).doubleValue();
        if (section) {
            width = folderWidth * hierarchyLevel;
        } else {
            double fileWidth = config.getNumber(HIERARCHY_SEPARATOR_ELEMENT_NODE).orElse(0.0).doubleValue();
            width = folderWidth * (hierarchyLevel - 1) + fileWidth;
        }

        setPrefWidth(width);
        setMinWidth(width);
    }

    @Listener
    private void onConfigurationNodeChange(ConfigurationNodeChangeEvent.After event) {
        if (event.getNode().equals(HIERARCHY_SEPARATOR_ELEMENT_NODE)
                || event.getNode().equals(HIERARCHY_SEPARATOR_SECTION_NODE)) {
            updateLevel();
        }
    }

}
