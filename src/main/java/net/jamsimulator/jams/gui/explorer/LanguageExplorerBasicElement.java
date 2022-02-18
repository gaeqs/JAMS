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

import javafx.geometry.Pos;
import javafx.scene.Group;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

/**
 * Represents a file inside an {@link Explorer}.
 */
public class LanguageExplorerBasicElement extends ExplorerBasicElement {

    /**
     * Creates an explorer basic element.
     *
     * @param parent         the {@link ExplorerSection} containing this element.
     * @param name           the name of the element.
     * @param hierarchyLevel the hierarchy level, used by the spacing.
     */
    public LanguageExplorerBasicElement(ExplorerSection parent, String name, int hierarchyLevel, String languageNode) {
        super(parent, name, hierarchyLevel);
        ((LanguageLabel) label).setNode(languageNode);
    }

    /**
     * Returns the language node of this element, or null if not present.
     *
     * @return the language node or null.
     */
    public String getLanguageNode() {
        return ((LanguageLabel) label).getNode();
    }

    @Override
    public String getVisibleName() {
        return label.getText();
    }

    /**
     * Sets the replacements for this element's language node.
     *
     * @param replacements the replacements.
     */
    public void setReplacements(String[] replacements) {
        ((LanguageLabel) label).setReplacements(replacements);
    }

    @Override
    protected void loadElements() {
        icon = new QualityImageView(null, FileType.IMAGE_SIZE, FileType.IMAGE_SIZE);
        label = new LanguageLabel(null);

        separator = new ExplorerSeparatorRegion(false, hierarchyLevel);

        //The label's group avoids it from being resized.
        getChildren().addAll(separator, icon, new Group(label));
        setSpacing(SPACING);
        setAlignment(Pos.CENTER_LEFT);
    }

}