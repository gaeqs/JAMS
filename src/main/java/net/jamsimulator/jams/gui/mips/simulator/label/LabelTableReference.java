package net.jamsimulator.jams.gui.mips.simulator.label;

import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.mips.label.LabelReference;
import net.jamsimulator.jams.utils.StringUtils;

public class LabelTableReference extends ExplorerBasicElement {

    private final LabelReference reference;

    public LabelTableReference(ExplorerSection parent, LabelReference reference, int hierarchyLevel) {
        super(parent, "0x" + StringUtils.addZeros(Integer.toHexString(reference.address()), 8) + " (" +
                reference.originFile() + ":" + reference.originLine() + ")", hierarchyLevel);
        this.reference = reference;
    }

    public LabelReference getReference() {
        return reference;
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return super.supportsActionRegion(region) || region.equals(RegionTags.MIPS_SIMULATION_LABELS_REFERENCE);
    }
}
