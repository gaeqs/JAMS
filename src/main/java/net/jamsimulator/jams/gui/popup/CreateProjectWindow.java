package net.jamsimulator.jams.gui.popup;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.start.StartWindowSectionNewProject;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;

public class CreateProjectWindow extends VBox {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static void open() {
        Stage stage = new Stage();
        stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.MAIN_MENU_FILE_CREATE_PROJECT_TITLE));
        JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO).ifPresent(stage.getIcons()::add);
        var node = new StartWindowSectionNewProject(() -> stage);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(JamsApplication.getStage());

        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);

        ThemedScene scene = new ThemedScene(node);

        stage.setScene(scene);

        Platform.runLater(() -> {
            Stage main = JamsApplication.getStage();
            stage.setX(main.getX() + main.getWidth() / 2 - WIDTH / 2.0);
            stage.setY(main.getY() + main.getHeight() / 2 - HEIGHT / 2.0);
        });

        stage.showAndWait();
    }
}
