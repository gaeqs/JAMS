package net.jamsimulator.jams.gui.mips.simulator.label;

import net.jamsimulator.jams.gui.JamsApplication;
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

    private Label label;

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

        representation.getIcon().setImage(JamsApplication.getIconManager()
                .getOrLoadSafe(Icons.SIMULATION_LABELS).orElse(null));

        var address = "0x" + StringUtils.addZeros(Integer.toHexString(label.getAddress()), 8);

        var addressElement = new LanguageExplorerBasicElement(this, "address", 2, Messages.LABELS_ADDRESS);
        var lineElement = new LanguageExplorerBasicElement(this, "line", 2, Messages.LABELS_LINE);

        addressElement.setReplacements(new String[]{"{ADDRESS}", address});
        lineElement.setReplacements(new String[]{"{LINE}", String.valueOf(label.getOriginLine())});

        addressElement.hideIcon(true);
        lineElement.hideIcon(true);

        addElement(addressElement);
        addElement(lineElement);
        addElement(new LabelTableReferences(explorer, this, label, 2));

    }

    public Label getLabel() {
        return label;
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return region.equals(RegionTags.EXPLORER_ELEMENT) || region.equals(RegionTags.MIPS_SIMULATION_LABELS_LABEL);
    }
}
