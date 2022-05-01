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

package net.jamsimulator.jams.gui.configuration;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.MainConfiguration;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowExplorer;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowSection;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;

import java.util.List;

public class ConfigurationWindow extends SplitPane {

    public static final String STYLE_CLASS = "configuration";
    public static final String DISPLAY_STYLE_CLASS = "display";
    public static final String DISPLAY_CONTENTS_STYLE_CLASS = "contents";

    private final MainConfiguration configuration;

    private final SectionTreeDisplay sectionTreeDisplay;
    private final AnchorPane sectionDisplay;
    private final ScrollPane basicSectionContentsScroll;
    private final VBox basicSectionContents;

    public ConfigurationWindow(MainConfiguration configuration) {
        this.configuration = configuration;

        getStyleClass().add(STYLE_CLASS);

        var explorerScrollPane = new PixelScrollPane();
        var explorer = new ConfigurationWindowExplorer(this, explorerScrollPane);

        explorerScrollPane.setFitToHeight(true);
        explorerScrollPane.setFitToWidth(true);
        explorerScrollPane.setContent(explorer);

        sectionTreeDisplay = new SectionTreeDisplay();

        sectionDisplay = new AnchorPane();
        sectionDisplay.getStyleClass().add(DISPLAY_STYLE_CLASS);

        basicSectionContentsScroll = new PixelScrollPane();
        basicSectionContentsScroll.setFitToWidth(true);
        basicSectionContentsScroll.setFitToHeight(true);

        basicSectionContents = new VBox();
        basicSectionContents.getStyleClass().add(DISPLAY_CONTENTS_STYLE_CLASS);
        basicSectionContentsScroll.setContent(basicSectionContents);

        AnchorUtils.setAnchor(sectionTreeDisplay, 0, -1, 0, 0);
        sectionDisplay.getChildren().add(sectionTreeDisplay);

        getItems().add(explorerScrollPane);
        getItems().add(sectionDisplay);
        SplitPane.setResizableWithParent(explorerScrollPane, false);
        Platform.runLater(() -> setDividerPosition(0, 0.2));
    }

    public MainConfiguration getConfiguration() {
        return configuration;
    }

    public void display(ConfigurationWindowSection section) {
        while (sectionDisplay.getChildren().size() > 1) {
            sectionDisplay.getChildren().remove(1);
        }

        if (section.isSpecial()) {
            Node node = section.getSpecialNode();
            AnchorUtils.setAnchor(node, 35, 0, 0, 0);
            sectionDisplay.getChildren().add(node);
        } else {
            displayNormalSection(section);
        }

        sectionTreeDisplay.setSection(section);
    }

    private void displayNormalSection(ConfigurationWindowSection section) {
        basicSectionContents.getChildren().clear();

        List<ConfigurationWindowNode> nodes = section.getNodes();
        String currentRegion = null;

        for (ConfigurationWindowNode node : nodes) {
            if (currentRegion == null || !currentRegion.equals(node.getMetadata().getRegion())) {
                currentRegion = node.getMetadata().getRegion();
                if (currentRegion != null) {
                    basicSectionContents.getChildren().add(new RegionDisplay(section.getLanguageNode(), currentRegion));
                }
            }
            basicSectionContents.getChildren().add(node);
        }

        AnchorUtils.setAnchor(basicSectionContentsScroll, 35, 0, 0, 0);
        sectionDisplay.getChildren().add(basicSectionContentsScroll);
    }
}
