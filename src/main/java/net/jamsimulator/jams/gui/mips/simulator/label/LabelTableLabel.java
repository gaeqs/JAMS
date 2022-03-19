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

package net.jamsimulator.jams.gui.mips.simulator.label;

import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.LanguageExplorerBasicElement;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Comparator;

public class LabelTableLabel extends ExplorerSection {

    private final Label label;

    /**
     * Creates the explorer section.
     *
     * @param explorer the {@link Explorer} of this section.
     * @param parent   the {@link ExplorerSection} containing this section. This may be null.
     * @param label    the represented label.
     *                 * @param hierarchyLevel the .hierarchy level, used by the spacing.
     */
    public LabelTableLabel(Explorer explorer, ExplorerSection parent, Label label,
                           int hierarchyLevel) {
        super(explorer, parent, label.getKey(), hierarchyLevel, Comparator.comparing(ExplorerElement::getVisibleName));

        this.label = label;

        representation.getIcon().setIcon(Icons.SIMULATION_LABELS);

        var address = "0x" + StringUtils.addZeros(Integer.toHexString(label.getAddress()), 8);

        var addressElement = new LanguageExplorerBasicElement(this, "address", hierarchyLevel, Messages.LABELS_ADDRESS);
        var lineElement = new LanguageExplorerBasicElement(this, "line", hierarchyLevel, Messages.LABELS_LINE);

        addressElement.setReplacements(new String[]{"{ADDRESS}", address});
        lineElement.setReplacements(new String[]{"{LINE}", String.valueOf(label.getOriginLine())});

        addressElement.hideIcon(true);
        lineElement.hideIcon(true);

        addElement(addressElement);
        addElement(lineElement);
        addElement(new LabelTableReferences(explorer, this, label, hierarchyLevel + 1));

    }

    public Label getLabel() {
        return label;
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return region.equals(RegionTags.EXPLORER_ELEMENT) || region.equals(RegionTags.MIPS_SIMULATION_LABELS_LABEL);
    }
}
