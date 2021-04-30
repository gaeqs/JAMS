package net.jamsimulator.jams.gui.mips.simulator.label;

import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.LanguageExplorerSection;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.label.Label;

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

        representation.hideIcon(true);

        label.getReferences().forEach(reference -> {
            addElement(new LabelTableReference(this, reference, 2));
        });
    }
}
