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
import javafx.scene.control.Label;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

/**
 * This class allows {@link ExplorerSection}s to be represented inside the explorer.
 * Its functionality is similar to the class {@link ExplorerBasicElement}.
 */
public class ExplorerSectionLanguageRepresentation extends ExplorerSectionRepresentation {

    private String languageNode;

    /**
     * Creates the representation.
     *
     * @param section        the {@link ExplorerSection} to represent.
     * @param hierarchyLevel the hierarchy level, used by the spacing.
     * @param languageNode   the language node.
     */
    public ExplorerSectionLanguageRepresentation(ExplorerSection section, int hierarchyLevel, String languageNode) {
        super(section, hierarchyLevel);
        this.languageNode = languageNode;
        loadElements(languageNode);
    }

    /**
     * Returns the language node of this representation, or null if not present.
     *
     * @return the language node or null.
     */
    public String getLanguageNode() {
        return languageNode;
    }

    /**
     * Sets the language node of this representation. This node may be null.
     *
     * @param node the language node.
     */
    public void setLanguageNode(String node) {
        this.languageNode = node;
        if (node != null) {
            if (label instanceof LanguageLabel) {
                ((LanguageLabel) label).setNode(node);
            } else {
                getChildren().remove(label);
                label = new LanguageLabel(node);
                getChildren().add(label);
            }
        } else {
            if (label instanceof LanguageLabel) {
                getChildren().remove(label);
                label = new Label(section.getName());
                getChildren().add(label);
            }
        }
    }


    @Override
    protected void loadElements() {
        statusIcon = new QualityImageView(null, 0, 0);
        icon = new QualityImageView(null, 0, 0);


        statusIcon.iconProperty().addListener((obs, old, val) -> {
            statusIcon.setFitHeight(val == null ? 0 : FileType.IMAGE_SIZE);
            statusIcon.setFitWidth(val == null ? 0 : FileType.IMAGE_SIZE);
        });

        icon.iconProperty().addListener((obs, old, val) -> {
            icon.setFitHeight(val == null ? 0 : FileType.IMAGE_SIZE);
            icon.setFitWidth(val == null ? 0 : FileType.IMAGE_SIZE);
        });

        separator = new ExplorerSeparatorRegion(true, hierarchyLevel);

        getChildren().addAll(separator, statusIcon, icon);
        setSpacing(ExplorerBasicElement.SPACING);
        setAlignment(Pos.CENTER_LEFT);
    }

    protected void loadElements(String languageNode) {
        label = languageNode == null ? new Label(section.getName()) : new LanguageLabel(languageNode);
        getChildren().add(label);
    }
}
