package net.jamsimulator.jams.gui.mips.simulator.label;

import net.jamsimulator.jams.gui.explorer.*;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Comparator;

public class LabelTableReferences extends LanguageExplorerSection {

    /**
     * Creates the explorer section.
     *
     * @param explorer the {@link Explorer} of this section.
     * @param parent   the {@link ExplorerSection} containing this section. This may be null.
     * @param label    the represented label.
     *                 * @param hierarchyLevel the .hierarchy level, used by the spacing.
     */
    public LabelTableReferences(Explorer explorer, ExplorerSection parent, Label label, int hierarchyLevel) {
        super(explorer, parent, "references", hierarchyLevel, Comparator.comparing(ExplorerElement::getVisibleName),
                Messages.LABELS_REFERENCES);

        //representation.getIcon().setImage(JamsApplication.getIconManager()
        //        .getOrLoadSafe(Icons.SIMULATION_LABELS).orElse(null));

        label.getReferences().forEach(reference -> {
            var address = StringUtils.addZeros(Integer.toHexString(reference.address()), 8);
            addElement(new ExplorerBasicElement(this, "0x" + address + " (" +
                    reference.originFile() + ":" + reference.originLine() + ")", 2));
        });
    }
}
