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

package net.jamsimulator.jams.gui.popup;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.start.StartWindowSectionNewProject;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.language.Messages;

public class CreateProjectWindow extends VBox {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static void open() {
        Stage stage = new Stage();
        stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.MAIN_MENU_FILE_CREATE_PROJECT_TITLE));
        Icons.LOGO.getImage().ifPresent(stage.getIcons()::add);
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
