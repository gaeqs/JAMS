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

package net.jamsimulator.jams.gui.util;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.file.FileTypeManager;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;
import java.util.Objects;

public class PathAndNameEditor extends StyledNodeList {

    private final SimpleBooleanProperty valid;
    private final TextField nameField, pathField;

    public PathAndNameEditor() {
        getStyleClass().add("path-and-name-editor");
        var defPath = new File(Jams.getMainFolder().getParentFile(), "JAMSProjects");
        this.valid = new SimpleBooleanProperty(false);

        nameField = new TextField();
        nameField.getStyleClass().add("invalid-text-field");
        addEntry(Messages.MAIN_MENU_FILE_CREATE_PROJECT_NAME, nameField);

        pathField = new TextField(defPath.getAbsolutePath() + File.separator);
        var selectParent = new Button("", new QualityImageView(Manager.get(FileTypeManager.class).getFolderType().getIcon(), 16, 16));
        addEntry(Messages.MAIN_MENU_FILE_CREATE_PROJECT_PATH, pathField, selectParent);

        selectParent.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File folder = chooser.showDialog(JamsApplication.getStage());
            if (folder == null) return;
            pathField.setText(folder.getAbsolutePath() + File.separator + nameField.getText());
        });

        nameField.textProperty().addListener((obs, old, val) -> refreshName(val));
        pathField.textProperty().addListener((obs, old, val) -> refreshPath(val));
    }

    public SimpleBooleanProperty validProperty() {
        return valid;
    }

    public String getName() {
        return nameField.getText();
    }

    public String getPath() {
        return pathField.getText();
    }

    private void refreshName(String value) {
        int index = pathField.getText().lastIndexOf(File.separator);
        if (index == -1) {
            pathField.setText(value);
        } else {
            pathField.setText(pathField.getText().substring(0, index) + File.separator + value);
        }

        if (value.isEmpty()) {
            if (!nameField.getStyleClass().contains("invalid-text-field")) {
                nameField.getStyleClass().add("invalid-text-field");
            }
            valid.set(false);
        } else {
            nameField.getStyleClass().remove("invalid-text-field");
            valid.set(!pathField.getStyleClass().contains("invalid-text-field"));
        }
    }

    private void refreshPath(String val) {
        File file = new File(val);
        if (!FileUtils.isValidPath(val)
                || !file.isDirectory() && file.exists()
                || file.isDirectory() && Objects.requireNonNull(file.listFiles()).length > 0) {
            if (!pathField.getStyleClass().contains("invalid-text-field")) {
                pathField.getStyleClass().add("invalid-text-field");
            }
            valid.set(false);
        } else {
            pathField.getStyleClass().remove("invalid-text-field");
            valid.set(!nameField.getText().isEmpty());
        }
    }
}
