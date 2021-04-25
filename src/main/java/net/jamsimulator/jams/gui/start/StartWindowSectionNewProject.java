package net.jamsimulator.jams.gui.start;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.project.ProjectTemplateBuilder;
import net.jamsimulator.jams.project.ProjectType;
import net.jamsimulator.jams.project.event.ProjectTypeRegisterEvent;
import net.jamsimulator.jams.project.event.ProjectTypeUnregisterEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class StartWindowSectionNewProject extends SplitPane implements StartWindowSection {

    private final Supplier<Stage> stageSupplier;

    private final HashMap<ProjectTemplateBuilder<?>, Node> addedCreators;
    private ProjectTemplateBuilder<?> selected;
    private Node selectedButton;

    private final VBox sectionsVBox;
    private final AnchorPane sectionDisplay;

    private final ListChangeListener<ProjectTemplateBuilder<?>> listener = event -> {
        event.getAddedSubList().forEach(this::addCreator);
        event.getRemoved().forEach(this::removeCreator);
    };

    public StartWindowSectionNewProject(Supplier<Stage> stageSupplier) {
        this.stageSupplier = stageSupplier;

        addedCreators = new HashMap<>();
        sectionsVBox = new VBox();
        sectionDisplay = new AnchorPane();

        sectionsVBox.getStyleClass().add("start-window-new-project-list");
        sectionDisplay.getStyleClass().add("start-window-new-project-display");

        loadSectionMenu();

        Jams.getProjectTypeManager().registerListeners(this, true);
    }

    private void select(ProjectTemplateBuilder<?> creator, Node button) {
        if (selected == creator) return;
        if (selected != null) {
            selectedButton.getStyleClass().remove("start-window-new-project-list-entry-selected");
        }

        selected = creator;
        selectedButton = button;

        sectionDisplay.getChildren().clear();
        if (selected != null) {
            selectedButton.getStyleClass().add("start-window-new-project-list-entry-selected");
            populate();
        }
    }

    private void loadSectionMenu() {
        refreshSections();

        getItems().addAll(sectionsVBox, sectionDisplay);
        Platform.runLater(() -> setDividerPosition(0, 0.3));
    }

    private void refreshSections() {
        sectionsVBox.getChildren().clear();
        Jams.getProjectTypeManager().forEach(this::addCreators);
    }

    private void addCreators(ProjectType<?> type) {
        type.getBuilderCreators().forEach(this::addCreator);
        type.getBuilderCreators().addListener(listener);
    }

    private void addCreator(ProjectTemplateBuilder<?> creator) {
        var button = new HBox();
        button.setSpacing(5);

        creator.getIcon().ifPresent(icon -> button.getChildren().add(new NearestImageView(icon, 20, 20)));
        button.getChildren().add(creator.getLanguageNode()
                .map(node -> (Label) new LanguageLabel(node))
                .orElseGet(() -> new Label(creator.getName())));

        button.getStyleClass().add("start-window-new-project-list-entry");
        button.setFillHeight(true);
        button.setOnMouseClicked(event -> select(creator, button));
        sectionsVBox.getChildren().add(button);

        if (selected == null) {
            select(creator, button);
        }

        addedCreators.put(creator, button);
    }

    private void removeCreator(ProjectTemplateBuilder<?> creator) {
        var value = addedCreators.remove(creator);
        if (value != null) sectionsVBox.getChildren().remove(value);
    }

    private void populate() {
        var builder = selected.createBuilder();
        var node = builder.getBuilderNode();
        AnchorUtils.setAnchor(node, 0, 35, 0, 0);
        sectionDisplay.getChildren().add(node);

        var buttons = new HBox();
        var createButton = new LanguageButton(Messages.GENERAL_CREATE);
        createButton.setOnAction(event -> {
            var project = builder.build();
            JamsApplication.getProjectsTabPane().openProject(project);
            stageSupplier.get().hide();
        });
        createButton.disableProperty().bind(builder.validProperty().not());
        buttons.getChildren().add(createButton);
        buttons.getStyleClass().add("start-window-new-project-buttons");

        AnchorUtils.setAnchor(buttons, -1, 0, 0, 0);
        sectionDisplay.getChildren().add(buttons);

    }

    @Listener
    private void onTypeRegistered(ProjectTypeRegisterEvent.After event) {
        event.getProjectType().getBuilderCreators().forEach(this::addCreator);
        event.getProjectType().getBuilderCreators().addListener(listener);
    }

    @Listener
    private void onTypeUnregistered(ProjectTypeUnregisterEvent.After event) {
        event.getProjectType().getBuilderCreators().forEach(this::removeCreator);
        event.getProjectType().getBuilderCreators().removeListener(listener);
    }

    @Override
    public String getLanguageNode() {
        return Messages.START_NEW_PROJECT;
    }

    @Override
    public Node toNode() {
        return this;
    }

    @Override
    public String getName() {
        return "new_project";
    }
}
