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

package net.jamsimulator.jams.gui.editor.top;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.util.AnchorUtils;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a search node for a text editor.
 */
public class CodeFileEditorReplace extends CodeFileEditorSearch {

    /**
     * The maximum amount of result this node can style.
     * <p>
     * This maximum amount is set due to performance reasons.
     */
    public static final int MAX_STYLED_RESULTS = 30;

    private final TextField replaceField;

    public CodeFileEditorReplace(CodeFileEditor editor) {
        super(editor);
        AnchorUtils.setAnchor(searchHBox, 0, 30, 0, -1);

        var replaceHBox = new HBox();
        AnchorUtils.setAnchor(replaceHBox, 30, 2, 0, -1);

        replaceHBox.setSpacing(5);
        replaceHBox.setAlignment(Pos.CENTER_LEFT);
        replaceHBox.setFillHeight(true);
        getChildren().add(replaceHBox);


        replaceField = new TextField();
        replaceField.getStyleClass().add("code-file-editor-search-text-field");
        replaceField.setOnAction(event -> onReplace());
        replaceHBox.getChildren().add(replaceField);

        var replaceButton = new Button("Replace");
        replaceButton.setOnAction(event -> onReplace());
        replaceHBox.getChildren().add(replaceButton);

        var replaceAllButton = new Button("Replace all");
        replaceAllButton.setOnAction(event -> replaceAll());
        replaceHBox.getChildren().add(replaceAllButton);
    }

    private void onReplace() {
        if (selected == null) return;
        var toReplace = selected;
        selectNextWithoutStyle();
        editor.replace(toReplace.start(), toReplace.end(),
                replaceField.getText(), Collections.emptySet());
        refreshText();
        moveToSelected();
    }

    private void replaceAll() {
        var result = editor.getText().replace(textField.getText(), replaceField.getText());
        editor.replace(0, editor.getLength(), result, Set.of());
    }
}
