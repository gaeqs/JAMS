package net.jamsimulator.jams.gui.start;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.BorderlessMainScene;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

import java.io.IOException;
import java.util.ArrayList;

public class StartWindow extends AnchorPane {

    public static StartWindow INSTANCE = new StartWindow();
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;

    private final ObservableList<StartWindowSection> sections;
    private StartWindowSection selected;
    private Node selectedButton;

    private Stage stage;
    private StartWindowTopBar topBar;
    private SplitPane sectionsSplitPane;
    private VBox sectionsVBox;
    private AnchorPane sectionDisplay;

    private StartWindow() {
        sections = FXCollections.observableList(new ArrayList<>());
        sections.add(new StartWindowSectionProjects(this));
        sections.add(new StartWindowSectionNewProject(this::getStage));
        sections.add(new StartWindowSectionConfiguration());
        sections.add(new StartWindowSectionAbout());
    }

    public Stage getStage() {
        return stage;
    }

    public ObservableList<StartWindowSection> getSections() {
        return sections;
    }

    private void select(StartWindowSection section, Node button) {
        if (selected == section) return;
        if (selected != null) {
            selectedButton.getStyleClass().remove("start-window-section-list-entry-selected");
        }

        selected = section;
        selectedButton = button;

        sectionDisplay.getChildren().clear();
        if (selected != null) {
            selectedButton.getStyleClass().add("start-window-section-list-entry-selected");
            var node = selected.toNode();
            if (node != null) {
                AnchorUtils.setAnchor(node, 0, 0, 0, 0);
                sectionDisplay.getChildren().add(node);
            }
        }
    }

    private void init() {
        getStyleClass().addAll("anchor-pane", "start-window");

        topBar = new StartWindowTopBar(stage);
        sectionsSplitPane = new SplitPane();
        sectionsVBox = new VBox();
        sectionDisplay = new AnchorPane();

        sectionsSplitPane.getStyleClass().add("start-window-split-pane");
        sectionsVBox.getStyleClass().add("start-window-section-list");
        sectionDisplay.getStyleClass().add("start-window-section-display");

        loadButtons();
        loadSectionMenu();
    }

    private void loadButtons() {
        AnchorUtils.setAnchor(topBar, 0, -1, 0, 0);
        topBar.setPrefHeight(30);
        getChildren().add(topBar);
    }

    private void loadSectionMenu() {
        sections.addListener((ListChangeListener<? super StartWindowSection>) listener -> refreshSections());
        refreshSections();

        sectionsSplitPane.getItems().addAll(sectionsVBox, sectionDisplay);
        Platform.runLater(() -> sectionsSplitPane.setDividerPosition(0, 0.15));
        stage.addEventFilter(WindowEvent.WINDOW_SHOWING,
                event -> Platform.runLater(() -> sectionsSplitPane.setDividerPosition(0, 0.15)));

        AnchorUtils.setAnchor(sectionsSplitPane, 30, 0, 0, 0);
        getChildren().add(sectionsSplitPane);
    }

    private void refreshSections() {
        sectionsVBox.getChildren().clear();
        sections.forEach(section -> {
            var button = new HBox(new LanguageLabel(section.getLanguageNode()));
            button.getStyleClass().add("start-window-section-list-entry");
            button.setFillHeight(true);
            button.setOnMouseClicked(event -> select(section, button));
            sectionsVBox.getChildren().add(button);
        });

        if (!sectionsVBox.getChildren().isEmpty()) {
            select(sections.get(0), sectionsVBox.getChildren().get(0));
        }
    }


    public void open() {
        if (stage == null) {
            stage = new Stage();
            init();

            var scene = new BorderlessMainScene(stage, this, WIDTH, HEIGHT);
            stage.setScene(scene);

            scene.setMoveControl(topBar);
            scene.setResizable(true);
            scene.setSnapEnabled(false);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setWidth(WIDTH);
            stage.setHeight(HEIGHT);
            JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO).ifPresent(stage.getIcons()::add);
            stage.setOnHidden(event -> {
                // We are saving the configuration because it may be edited in the start window!
                try {
                    Jams.getMainConfiguration().save(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        stage.showAndWait();
    }
}
