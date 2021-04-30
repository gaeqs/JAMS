package net.jamsimulator.jams.gui.start;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.popup.CreateProjectWindow;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.project.ProjectSnapshot;
import net.jamsimulator.jams.project.event.RecentProjectAddEvent;
import net.jamsimulator.jams.project.mips.MIPSProject;

import java.io.File;

public class StartWindowSectionProjects extends AnchorPane implements StartWindowSection {

    private final StartWindow window;
    private VBox projects;

    public StartWindowSectionProjects(StartWindow window) {
        this.window = window;
        loadButtons();
        loadRecentProjects();

        Jams.getRecentProjects().registerListeners(this, true);
    }

    @Override
    public String getLanguageNode() {
        return Messages.START_PROJECTS;
    }

    @Override
    public Node toNode() {
        return this;
    }

    @Override
    public String getName() {
        return "projects";
    }

    private void loadButtons() {
        var openButton = new LanguageButton(Messages.ACTION_GENERAL_OPEN_PROJECT);

        openButton.getStyleClass().add("light-button");

        openButton.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File folder = chooser.showDialog(JamsApplication.getStage());
            if (folder == null || JamsApplication.getProjectsTabPane().isProjectOpen(folder)) return;
            JamsApplication.getProjectsTabPane().openProject(new MIPSProject(folder));
            window.getStage().hide();
        });

        var hbox = new HBox(openButton);
        hbox.getStyleClass().add("start-window-project-buttons-box");

        AnchorUtils.setAnchor(hbox, 0, -1, 0, 0);
        getChildren().add(hbox);
    }

    private void loadRecentProjects() {
        projects = new VBox();
        var scrollpane = new PixelScrollPane(projects);
        scrollpane.setFitToWidth(true);
        scrollpane.setFitToHeight(true);

        AnchorUtils.setAnchor(scrollpane, 50, 0, 0, 0);
        getChildren().add(scrollpane);
        refeshProjects();
    }

    private void refeshProjects() {
        projects.getChildren().clear();
        Jams.getRecentProjects().forEach(snapshot -> {
            var file = new File(snapshot.path());
            if (!file.isDirectory()) return;
            projects.getChildren().add(new RecentProject(snapshot));
        });
    }

    @Listener
    private void onRecentProjectAdd(RecentProjectAddEvent.After event) {
        // We refresh instead of adding it because it can be an already present
        // project that is being moved to the top of the list.
        refeshProjects();
    }


    private class RecentProject extends HBox {

        public RecentProject(ProjectSnapshot snapshot) {
            getStyleClass().add("start-window-project-entry");

            var title = new Label(snapshot.name());
            var path = new Label(snapshot.path());

            title.getStyleClass().add("title");
            path.getStyleClass().add("path");

            var type = Jams.getProjectTypeManager().getByProjectfolder(new File(snapshot.path()));
            if (type.isPresent() && type.get().getIcon().isPresent()) {
                var image = new NearestImageView(type.get().getIcon().get(), 40, 40);
                getChildren().addAll(image, new VBox(title, path));
            } else {
                getChildren().addAll(title, new VBox(title, path));
            }

            setOnMouseClicked(event -> {
                var file = new File(snapshot.path());
                if (!file.isDirectory()) {
                    projects.getChildren().remove(this);
                    return;
                }
                JamsApplication.getProjectsTabPane().openProject(new MIPSProject(file));
                window.getStage().hide();
            });
        }

    }
}
