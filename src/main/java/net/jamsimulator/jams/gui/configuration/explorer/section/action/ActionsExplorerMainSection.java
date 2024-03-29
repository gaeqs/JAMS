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

package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSectionRepresentation;
import net.jamsimulator.jams.gui.explorer.LanguageExplorerSection;
import net.jamsimulator.jams.language.Messages;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the main section of an {@link ActionsExplorer}.
 */
public class ActionsExplorerMainSection extends LanguageExplorerSection {

    protected Map<String, ActionExplorerRegion> regions;

    /**
     * Creates the explorer section.
     *
     * @param explorer the {@link Explorer} of this section.
     */
    public ActionsExplorerMainSection(ActionsExplorer explorer) {
        super(explorer, null, "Actions", 0,
                Comparator.comparing(ExplorerElement::getName), Messages.CONFIG_ACTION);
        generateRegions();
    }

    /**
     * Adds an {@link Action} to the explorer.
     * <p>
     * If the {@link Action} was already inside the explorer a duplicated element will be created. Be careful!
     *
     * @param action the {@link Action}.
     */
    public void addAction(Action action) {
        ActionExplorerRegion region;
        if (regions.containsKey(action.getRegionTag())) {
            region = regions.get(action.getRegionTag());
        } else {
            region = new ActionExplorerRegion((ActionsExplorer) explorer, this, action.getRegionTag());
            regions.put(action.getRegionTag(), region);
            addElement(region);
        }
        region.addElement(new ActionsExplorerAction(region, action, getExplorer().isSmallRepresentation()));
    }

    /**
     * Removes an {@link Action} from the explorer.
     *
     * @param action the {@link Action}.
     */
    public void removeAction(Action action) {
        if (!regions.containsKey(action.getRegionTag())) return;
        ActionExplorerRegion region = regions.get(action.getRegionTag());

        region.removeElementIf(target -> target instanceof ActionsExplorerAction
                && ((ActionsExplorerAction) target).getAction().equals(action));

        if (region.isEmpty()) {
            regions.remove(action.getRegionTag());
            removeElement(region);
        }
    }

    protected void generateRegions() {
        regions = new HashMap<>();
        JamsApplication.getActionManager().forEach(this::addAction);
    }

    public void setSmallRepresentation(boolean smallRepresentation) {
        for (ActionExplorerRegion region : regions.values()) {
            region.setSmallRepresentation(smallRepresentation);
        }
    }

    @Override
    public ActionsExplorer getExplorer() {
        return (ActionsExplorer) super.getExplorer();
    }

    @Override
    public String getVisibleName() {
        return representation.getLabel().getText();
    }

    /**
     * Returns the width property of the biggest element in big representation this section.
     * This may return the {@link ExplorerSectionRepresentation} of this section.
     *
     * @return the width property.
     */
    public double getBiggestElementInBigRepresentation() {
        double property = getRepresentation().getRepresentationWidth();
        double current;
        for (ExplorerElement element : elements) {
            if (element instanceof ActionExplorerRegion) {
                current = ((ActionExplorerRegion) element).getBiggestElementInBigRepresentation();
                if (property < current) {
                    property = current;
                }
            }
        }
        return property;
    }
}
