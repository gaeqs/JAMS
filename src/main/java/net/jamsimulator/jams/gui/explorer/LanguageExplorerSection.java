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

import java.util.Comparator;

/**
 * Represents an explorer section that displays a language node.
 */
public class LanguageExplorerSection extends ExplorerSection {


    /**
     * Creates the explorer section.
     *
     * @param explorer       the {@link Explorer} of this section.
     * @param parent         the {@link ExplorerSection} containing this section. This may be null.
     * @param name           the name of the section.
     * @param hierarchyLevel the hierarchy level, used by the spacing.
     * @param comparator     the comparator used to sort the elements.
     * @param languageNode   the language node.
     */
    public LanguageExplorerSection(Explorer explorer, ExplorerSection parent, String name, int hierarchyLevel,
                                   Comparator<ExplorerElement> comparator, String languageNode) {
        super(explorer, parent, name, hierarchyLevel, comparator);
        ((ExplorerSectionLanguageRepresentation) representation).setLanguageNode(languageNode);
    }

    /**
     * Returns the language node of this section, or null if not present.
     *
     * @return the language node or null.
     */
    public String getLanguageNode() {
        return ((ExplorerSectionLanguageRepresentation) representation).getLanguageNode();
    }

    @Override
    public String getVisibleName() {
        return representation.label.getText();
    }

    @Override
    protected ExplorerSectionRepresentation loadRepresentation() {
        return new ExplorerSectionLanguageRepresentation(this, hierarchyLevel, null);
    }
}
