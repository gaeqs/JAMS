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
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.project.ProjectTemplateBuilder;
import net.jamsimulator.jams.project.ProjectType;
import net.jamsimulator.jams.project.exception.MIPSTemplateBuildException;

import java.util.HashMap;
import java.util.function.Supplier;

public class StartWindowSectionNewProject extends SplitPane implements StartWindowSection {

    private final Supplier<Stage> stageSupplier;

    private final HashMap<ProjectTemplateBuilder<?>, Node> addedCreators;
    private final VBox sectionsVBox;
    private final AnchorPane sectionDisplay;
    private ProjectTemplateBuilder<?> selected;
    private Node selectedButton;
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

        Manager.of(ProjectType.class).registerListeners(this, true);
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
        Manager.of(ProjectType.class).forEach(this::addCreators);
    }

    private void addCreators(ProjectType<?> type) {
        type.getTemplateBuilders().forEach(this::addCreator);
        type.getTemplateBuilders().addListener(listener);
    }

    private void addCreator(ProjectTemplateBuilder<?> creator) {
        var button = new HBox();
        button.setSpacing(5);

        creator.getIcon().ifPresent(icon -> button.getChildren().add(new QualityImageView(icon, 20, 20)));
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
            Project project;
            try {
                project = builder.build();
            } catch (MIPSTemplateBuildException e) {
                e.printStackTrace();
                return;
            }
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
    private void onTypeRegistered(ManagerElementRegisterEvent.After<ProjectType<?>> event) {
        event.getElement().getTemplateBuilders().forEach(this::addCreator);
        event.getElement().getTemplateBuilders().addListener(listener);
    }

    @Listener
    private void onTypeUnregistered(ManagerElementUnregisterEvent.After<ProjectType<?>> event) {
        event.getElement().getTemplateBuilders().forEach(this::removeCreator);
        event.getElement().getTemplateBuilders().removeListener(listener);
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
