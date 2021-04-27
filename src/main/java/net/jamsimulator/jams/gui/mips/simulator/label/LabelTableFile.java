package net.jamsimulator.jams.gui.mips.simulator.label;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.image.icon.Icons;

import java.util.Comparator;

public class LabelTableFile extends ExplorerSection {

    /**
     * Creates the explorer section.
     *
     * @param explorer   the {@link Explorer} of this section.
     * @param parent     the {@link ExplorerSection} containing this section. This may be null.
     * @param file       the represented file.
     *                   * @param hierarchyLevel the .hierarchy level, used by the spacing.
     */
    public LabelTableFile(Explorer explorer, ExplorerSection parent, String file, int hierarchyLevel) {
        super(explorer, parent, file, hierarchyLevel, Comparator.comparing(ExplorerElement::getVisibleName));

        representation.getIcon().setImage(JamsApplication.getIconManager()
                .getOrLoadSafe(Icons.FILE_ASSEMBLY).orElse(null));
    }
}
