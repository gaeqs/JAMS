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

package net.jamsimulator.jams.gui.about;

import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.manager.Manager;

public class AboutWindow extends AnchorPane {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 400;
    private static final int IMAGE_SIZE = 150;

    public AboutWindow() {
        var contents = new VBox();
        AnchorUtils.setAnchor(contents, 0, 0, 0, 0);
        getChildren().add(contents);

        contents.setAlignment(Pos.CENTER);
        contents.setSpacing(5);

        contents.getChildren().add(new QualityImageView(Icons.LOGO, IMAGE_SIZE, IMAGE_SIZE));

        var label = new LanguageLabel(Messages.ABOUT, "{VERSION}", Jams.getVersion(), "{YEAR}", "2022");
        label.getStyleClass().add("about-text");
        contents.getChildren().add(label);

    }


    public static void open() {
        var content = new AboutWindow();
        var stage = new Stage();
        var scene = new ThemedScene(content);
        stage.initOwner(JamsApplication.getStage());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setMinWidth(WIDTH >> 1);
        stage.setMinHeight(0);

        Stage main = JamsApplication.getStage();

        stage.setX(main.getX() + main.getWidth() / 2 - (WIDTH >> 1));
        stage.setY(main.getY() + main.getHeight() / 2 - (HEIGHT >> 1));

        stage.setTitle(Manager.ofS(Language.class).getSelected().getOrDefault(Messages.MAIN_MENU_HELP_ABOUT));
        Icons.LOGO.getImage().ifPresent(stage.getIcons()::add);


        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

        JamsApplication.getActionManager().addAcceleratorsToScene(scene, true);
        Manager.of(Language.class).registerListeners(content, true);

        stage.show();
    }

    @Override
    public String getTypeSelector() {
        return "AnchorPane";
    }
}
