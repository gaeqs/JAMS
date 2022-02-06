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
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.configuration.format.ConfigurationFormat;
import net.jamsimulator.jams.configuration.format.ConfigurationFormatJSON;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowExplorer;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowSection;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerDefaultElementChangeEvent;
import net.jamsimulator.jams.manager.event.ManagerSelectedElementChangeEvent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class ConfigurationWindow extends SplitPane {

    public static final String STYLE_CLASS = "configuration";
    public static final String DISPLAY_STYLE_CLASS = "display";
    public static final String DISPLAY_CONTENTS_STYLE_CLASS = "contents";

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;

    private static ConfigurationWindow INSTANCE;
    private final RootConfiguration configuration;
    private final Configuration meta;
    private final ConfigurationWindowExplorer explorer;
    private final ScrollPane explorerScrollPane;
    private final SectionTreeDisplay sectionTreeDisplay;
    private final AnchorPane sectionDisplay;
    private final ScrollPane basicSectionContentsScroll;
    private final VBox basicSectionContents;
    private Stage stage;
    private Scene scene;

    public ConfigurationWindow(RootConfiguration configuration, Configuration meta) {
        this.stage = null;
        this.configuration = configuration;
        this.meta = meta;

        getStyleClass().add(STYLE_CLASS);

        explorerScrollPane = new PixelScrollPane();
        explorerScrollPane.setFitToHeight(true);
        explorerScrollPane.setFitToWidth(true);
        explorer = new ConfigurationWindowExplorer(this, explorerScrollPane);
        explorer.hideMainSectionRepresentation();
        explorerScrollPane.setContent(explorer);

        explorerScrollPane.getContent().addEventHandler(ScrollEvent.SCROLL, scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * 0.003;
            explorerScrollPane.setVvalue(explorerScrollPane.getVvalue() - deltaY);
        });

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

        init();
    }

    public static ConfigurationWindow getInstance() {
        if (INSTANCE == null) {
            try {
                var format = Manager.of(ConfigurationFormat.class).getOrNull(ConfigurationFormatJSON.NAME);
                Configuration types = new RootConfiguration(new InputStreamReader(
                        Objects.requireNonNull(Jams.class.getResourceAsStream(
                                "/configuration/main_config_meta.jconfig"))), format);
                INSTANCE = new ConfigurationWindow(Jams.getMainConfiguration(), types);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return INSTANCE;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Configuration getMeta() {
        return meta;
    }

    public Stage getStage() {
        return stage;
    }

    private void init() {
        getItems().add(explorerScrollPane);
        getItems().add(sectionDisplay);
        SplitPane.setResizableWithParent(explorerScrollPane, false);
        Platform.runLater(() -> setDividerPosition(0, 0.2));
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
            if (currentRegion == null || !currentRegion.equals(node.getRegion())) {
                currentRegion = node.getRegion();
                if (currentRegion != null) {
                    basicSectionContents.getChildren().add(new RegionDisplay(section.getLanguageNode(), currentRegion));
                }
            }
            basicSectionContents.getChildren().add(node);
        }

        AnchorUtils.setAnchor(basicSectionContentsScroll, 35, 0, 0, 0);
        sectionDisplay.getChildren().add(basicSectionContentsScroll);
    }

    public void open() {
        if (stage == null) {
            stage = new Stage();
            scene = new ThemedScene(this);
            stage.initOwner(JamsApplication.getStage());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            stage.setWidth(WIDTH);
            stage.setHeight(HEIGHT);
            stage.setMinWidth(WIDTH >> 1);
            stage.setMinHeight(HEIGHT >> 1);

            Stage main = JamsApplication.getStage();

            stage.setX(main.getX() + main.getWidth() / 2 - (WIDTH >> 1));
            stage.setY(main.getY() + main.getHeight() / 2 - (HEIGHT >> 1));

            stage.setTitle(Manager.ofS(Language.class).getSelected().getOrDefault(Messages.CONFIG));
            Icons.LOGO.getImage().ifPresent(stage.getIcons()::add);


            stage.setOnCloseRequest(event -> {
                try {
                    configuration.save(
                            Manager.of(ConfigurationFormat.class).getOrNull(ConfigurationFormatJSON.NAME),
                            true
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            });

            JamsApplication.getActionManager().addAcceleratorsToScene(scene, true);
            Manager.of(Language.class).registerListeners(this, true);
        }

        stage.show();
    }

    @Listener
    private void onSelectedLanguageChange(ManagerSelectedElementChangeEvent.After<Language> event) {
        stage.setTitle(event.getNewElement().getOrDefault(Messages.CONFIG));
    }

    @Listener
    private void onDefaultLanguageChange(ManagerDefaultElementChangeEvent.After<Language> event) {
        stage.setTitle(Manager.ofS(Language.class).getSelected().getOrDefault(Messages.CONFIG));
    }

    @Listener
    private void onActionBind(ActionBindEvent.After event) {
        if (scene != null) {
            JamsApplication.getActionManager().addAcceleratorsToScene(scene, true);
        }

    }

    @Listener
    private void onActionUnbind(ActionBindEvent.After event) {
        if (scene != null) {
            JamsApplication.getActionManager().addAcceleratorsToScene(scene, true);
        }

    }
}
