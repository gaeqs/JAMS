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

package net.jamsimulator.jams.gui.configuration;

import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.MainConfiguration;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.configuration.format.ConfigurationFormat;
import net.jamsimulator.jams.configuration.format.ConfigurationFormatJSON;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.event.LanguageRefreshEvent;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.utils.Validate;

import java.io.IOException;

public class ConfigurationWindowScene extends ThemedScene {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;

    private final Stage stage;

    public ConfigurationWindowScene(Parent root, Stage stage) {
        super(root);
        Manager.of(Language.class).registerListeners(this, true);
        Validate.notNull(stage, "Stage cannot be null!");
        this.stage = stage;
    }

    public ConfigurationWindowScene(Parent root, double width, double height, Stage stage) {
        super(root, width, height);
        Validate.notNull(stage, "Stage cannot be null!");
        this.stage = stage;
        Manager.of(Language.class).registerListeners(this, true);
    }

    public ConfigurationWindowScene(Parent root, double width, double height, boolean depthBuffer, Stage stage) {
        super(root, width, height, depthBuffer);
        Validate.notNull(stage, "Stage cannot be null!");
        this.stage = stage;
        Manager.of(Language.class).registerListeners(this, true);
    }

    @Listener
    public void onRefresh(LanguageRefreshEvent event) {
        stage.setTitle(event.getSelectedLanguage().getOrDefault(Messages.CONFIG));
    }

    @Listener
    private void onActionBind(ActionBindEvent.After event) {
        JamsApplication.getActionManager().addAcceleratorsToScene(this, true);
    }

    @Listener
    private void onActionUnbind(ActionBindEvent.After event) {
        JamsApplication.getActionManager().addAcceleratorsToScene(this, true);
    }

    public static void open() {
        open(Jams.getMainConfiguration());
    }

    public static void open(MainConfiguration configuration) {
        var stage = new Stage();
        var scene = new ConfigurationWindowScene(new ConfigurationWindow(configuration), stage);
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
                configuration.data().save(
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

        stage.show();
    }
}
