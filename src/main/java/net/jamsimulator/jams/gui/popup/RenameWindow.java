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
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.util.InvalidableTextField;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;

public class RenameWindow extends VBox {

    public static int WIDTH = 300;
    public static int HEIGHT = 50;

    private RenameWindow(Stage stage, File file) {
        getStyleClass().add("v-box");
        Validate.notNull(file, "File cannot be null!");
        setAlignment(Pos.BOTTOM_CENTER);
        getChildren().add(new LanguageLabel(Messages.ACTION_FOLDER_EXPLORER_ELEMENT_RENAME));

        var field = new InvalidableTextField();
        getChildren().add(field);

        field.setOnAction(event -> {
            if (field.getText().isEmpty()) {
                stage.close();
                return;
            }

            var newFile = new File(file.getParentFile(), field.getText());
            if (file.renameTo(newFile)) {
                stage.close();
            }
        });

        field.textProperty().addListener((obs, old, val) -> {
            var newFile = new File(file.getParentFile(), val);
            field.setInvalid(!newFile.equals(file) && newFile.exists());
        });

        field.focusedProperty().addListener((obs, old, val) -> Platform.runLater(() -> {
            int index = file.getName().lastIndexOf('.');
            if (file.isDirectory() || index == -1) {
                field.selectAll();
            } else {
                field.selectRange(0, index);
            }
        }));

        field.setText(file.getName());

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
                event.consume();
            }
        });

    }

    public static void open(File file) {
        Stage stage = new Stage();
        PopupWindowHelper.open(stage, new RenameWindow(stage, file), WIDTH, HEIGHT, true);
    }
}
