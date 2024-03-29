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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;

/**
 * This class allows {@link ExplorerSection}s to be represented inside the explorer.
 * Its functionality is similar to the class {@link ExplorerBasicElement}.
 */
public class ExplorerSectionRepresentation extends HBox {

    protected ExplorerSection section;

    protected QualityImageView statusIcon;
    protected QualityImageView icon;
    protected Label label;
    protected ExplorerSeparatorRegion separator;

    protected ExplorerSeparatorRegion emptyRegion;

    //HIERARCHY
    protected int hierarchyLevel;

    protected boolean selected;

    /**
     * Creates the representation.
     *
     * @param section        the {@link ExplorerSection} to represent.
     * @param hierarchyLevel the hierarchy level, used by the spacing.
     */
    public ExplorerSectionRepresentation(ExplorerSection section, int hierarchyLevel) {
        getStyleClass().addAll("explorer-element", "explorer-representation");
        this.section = section;
        this.hierarchyLevel = hierarchyLevel;

        selected = false;

        emptyRegion = new ExplorerSeparatorRegion(FileType.IMAGE_SIZE);

        loadElements();
        loadListeners();
        refreshStatusIcon();

        prefWidthProperty().bind(section.explorer.widthProperty());
    }

    public double getRepresentationWidth() {
        double statusWidth = statusIcon.getIcon() == null ? 0 : statusIcon.getFitWidth();
        double iconWidth = icon.getIcon() == null ? 0 : icon.getFitWidth();
        double separatorWidth = separator == null ? 0 : separator.getWidth();

        return separatorWidth + statusWidth + iconWidth + label.getWidth() + ExplorerBasicElement.SPACING * 3;
    }


    public Label getLabel() {
        return label;
    }

    public QualityImageView getIcon() {
        return icon;
    }

    /**
     * Refresh the status icon of the folder.
     */
    public void refreshStatusIcon() {
        IconData icon;
        if (section.isEmpty()) {
            icon = null;
        } else if (section.isExpanded()) {
            icon = Icons.EXPLORER_FOLDER_EXPANDED;
        } else {
            icon = Icons.EXPLORER_FOLDER_COLLAPSED;
        }
        statusIcon.setIcon(icon);
        if (icon == null) {
            getChildren().remove(statusIcon);
            if (!getChildren().contains(emptyRegion))
                getChildren().add(1, emptyRegion);
        } else if (!getChildren().contains(statusIcon)) {
            getChildren().remove(emptyRegion);
            getChildren().add(1, statusIcon);
        }
    }


    /**
     * Returns the represented {@link ExplorerSection}.
     *
     * @return the represented {@link ExplorerSection}.
     */
    public ExplorerSection getSection() {
        return section;
    }

    /**
     * Returns the hierarchy level.
     *
     * @return the hierarchy level.
     */
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public boolean isSelected() {
        return selected;
    }

    public void hideIcon(boolean hide) {
        getChildren().clear();
        if (hide) {
            getChildren().addAll(separator, statusIcon, new Group(label));
        } else {
            getChildren().addAll(separator, statusIcon, icon, new Group(label));
        }
    }

    public void select() {
        if (selected) return;
        getStyleClass().add("selected-explorer-element");
        selected = true;
    }

    public void deselect() {
        if (!selected) return;
        getStyleClass().remove("selected-explorer-element");
        selected = false;
    }


    protected void loadElements() {
        statusIcon = new QualityImageView(null, 0, 0);
        icon = new QualityImageView(null, 0, 0);
        label = new Label(section.getName());

        statusIcon.iconProperty().addListener((obs, old, val) -> {
            statusIcon.setFitHeight(val == null ? 0 : FileType.IMAGE_SIZE);
            statusIcon.setFitWidth(val == null ? 0 : FileType.IMAGE_SIZE);
        });

        icon.iconProperty().addListener((obs, old, val) -> {
            icon.setFitHeight(val == null ? 0 : FileType.IMAGE_SIZE);
            icon.setFitWidth(val == null ? 0 : FileType.IMAGE_SIZE);
        });


        separator = new ExplorerSeparatorRegion(true, hierarchyLevel);

        getChildren().addAll(separator, statusIcon, icon, new Group(label));
        setSpacing(ExplorerBasicElement.SPACING);
        setAlignment(Pos.CENTER_LEFT);
    }

    protected void loadListeners() {
        statusIcon.setOnMouseClicked(event -> {
            section.getExplorer().selectElementAlone(section);
            section.expandOrContract();
            event.consume();
        });
    }

}
