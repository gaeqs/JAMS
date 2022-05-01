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

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.project.GlobalIndexHolder;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.Optional;

public class NewAssemblyFileWindow extends VBox {

    public static int WIDTH = 500;
    public static int HEIGHT = 50;

    private NewAssemblyFileWindow(Stage stage, File folder, GlobalIndexHolder holder) {
        getStyleClass().add("v-box");
        Validate.notNull(folder, "Folder cannot be null!");
        Validate.isTrue(folder.isDirectory(), "Folder must be a directory!");
        setAlignment(Pos.CENTER);
        getChildren().add(new LanguageLabel(Messages.ACTION_FOLDER_EXPLORER_ELEMENT_NEW_ASSEMBLY_FILE));


        TextField field = new TextField();
        getChildren().add(field);

        CheckBox check = new CheckBox("Add file to assembler");
        if (holder != null) {
            getChildren().add(new Group(check));
            check.setOnAction(action -> field.requestFocus());
            Optional<Boolean> optional = Jams.getMainConfiguration().data()
                    .get("explorer.mips.add_created_asm_files_to_assemble");
            check.setSelected(optional.orElse(false));
        }

        field.setOnAction(event -> {
            if (field.getText().isEmpty()) {
                stage.close();
                return;
            }
            File file = new File(folder, field.getText() + ".asm");
            try {
                if (file.createNewFile()) {
                    stage.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (holder != null && check.isSelected()) {
                holder.getGlobalIndex().addFile(file);
            }
        });

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
                event.consume();
            }
        });
    }

    public static void open(File folder, GlobalIndexHolder holder) {
        Stage stage = new Stage();
        PopupWindowHelper.open(stage, new NewAssemblyFileWindow(stage, folder, holder), WIDTH, HEIGHT, true);
    }
}
