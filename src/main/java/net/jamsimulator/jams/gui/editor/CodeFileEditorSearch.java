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

package net.jamsimulator.jams.gui.editor;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.TwoDimensional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a search node for a text editor.
 */
public class CodeFileEditorSearch extends AnchorPane implements FileEditorTabTopNode {

    /**
     * The maximum amount of result this node can style.
     *
     * This maximum amount is set due to performance reasons.
     */
    public static final int MAX_STYLED_RESULTS = 30;

    private final CodeFileEditor editor;
    private final TextField textField;
    private final LanguageLabel resultsLabel;

    private final List<Result> results;
    private final Set<Result> styled;
    private Result selected;

    public CodeFileEditorSearch(CodeFileEditor editor) {
        this.editor = editor;
        getStyleClass().add("code-file-editor-search");

        var hBox = new HBox();
        AnchorUtils.setAnchor(hBox, 0, 0, 0, -1);

        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setFillHeight(true);
        getChildren().add(hBox);


        textField = new TextField();
        textField.getStyleClass().add("code-file-editor-search-text-field");
        textField.textProperty().addListener((obs, old, val) -> onTextChange());
        textField.setOnAction(event -> selectNext());
        hBox.getChildren().add(textField);

        var previousButton = new Button("\u2191");
        var nextButton = new Button("\u2193");
        previousButton.getStyleClass().add("code-file-editor-search-button");
        nextButton.getStyleClass().add("code-file-editor-search-button");
        previousButton.setOnAction(event -> selectPrevious());
        nextButton.setOnAction(event -> selectNext());
        hBox.getChildren().addAll(previousButton, nextButton);

        resultsLabel = new LanguageLabel(Messages.BAR_SEARCH_RESULTS, "{RESULTS}", "0");
        resultsLabel.getStyleClass().add("code-file-editor-search-results");
        hBox.getChildren().add(resultsLabel);

        var closeButton = new Button("x");
        closeButton.getStyleClass().add("code-file-editor-search-button");
        closeButton.setOnAction(event -> hide());
        AnchorUtils.setAnchor(closeButton, 2, 2, -1, 0);
        getChildren().add(closeButton);

        results = new LinkedList<>();
        styled = new HashSet<>();
        selected = null;
    }

    public void open() {
        editor.getTab().setTopNode(this);
        textField.requestFocus();
    }

    public void hide() {
        if (editor.getTab().getTopNode().orElse(null) == this) {
            editor.getTab().setTopNode(null);
        }
    }

    @Override
    public void onShow() {
        selectCloser();
        styleNearbyResults();
    }

    @Override
    public void onHide() {
        clearStyles();
        if (textField.isFocused()) {
            editor.requestFocus();
        }
    }

    public void refreshText() {
        clearStyles();
        results.clear();

        var text = textField.getText();

        if (!text.isEmpty()) {
            var pattern = Pattern.compile(Pattern.quote(text));

            var matcher = pattern.matcher(editor.getText());
            while (matcher.find()) {
                results.add(new Result(matcher));
            }
        }

        resultsLabel.setReplacements(new String[]{"{RESULTS}", String.valueOf(results.size())});
        selectCloser();
        styleNearbyResults();
    }


    private void onTextChange() {
        refreshText();
        moveToSelected();
    }

    private void selectCloser() {
        if (results.isEmpty()) selected = null;
        var caret = editor.getCaretPosition();
        selected = results.stream().min(Comparator.comparingInt(o -> o.distanceTo(caret))).orElse(null);
    }

    private void moveToSelected() {
        if (selected != null) {
            editor.showParagraphInViewport(editor.offsetToPosition(selected.start,
                    TwoDimensional.Bias.Forward).getMajor());
            editor.moveTo(selected.end);
        }
    }

    private void selectNext() {
        if (selected == null || results.size() < 2) return;
        var index = results.indexOf(selected) + 1;
        if (index == results.size()) index = 0;
        selected = results.get(index);
        moveToSelected();
        clearStyles();
        styleNearbyResults();
    }

    private void selectPrevious() {
        if (selected == null || results.size() < 2) return;
        var index = results.indexOf(selected) - 1;
        if (index < 0) index = results.size() - 1;
        selected = results.get(index);
        moveToSelected();
        clearStyles();
        styleNearbyResults();
    }

    private void clearStyles() {
        styled.forEach(result -> {
            var spans = editor.getStyleSpans(result.start, result.end);

            if (spans.length() > 0) {
                var styleSpans = new StyleSpansBuilder<Collection<String>>();
                for (var span : spans) {
                    var styles = new LinkedList<>(span.getStyle());
                    styles.removeIf(str -> str.equals("search-result") || str.equals("selected-search-result"));
                    styleSpans.add(styles, span.getLength());
                }
                editor.setStyleSpans(result.start, styleSpans.create());
            }
        });
        styled.clear();
    }

    private void styleNearbyResults() {
        if (selected == null) return;
        Collection<Result> toStyle;

        if (results.size() <= MAX_STYLED_RESULTS) toStyle = results;
        else {
            var index = results.indexOf(selected);
            var from = index - MAX_STYLED_RESULTS / 2;
            var to = index + MAX_STYLED_RESULTS / 2;
            if (from < 0) {
                to -= from;
                from = 0;
            } else if (to > results.size()) {
                var amount = to - results.size();
                to = results.size();
                from -= amount;
            }
            toStyle = results.subList(from, to);
        }

        styled.addAll(toStyle);

        toStyle.forEach(result -> {
            var spans = editor.getStyleSpans(result.start, result.end);

            if (spans.length() > 0) {
                var styleSpans = new StyleSpansBuilder<Collection<String>>();
                for (var span : spans) {
                    var styles = new LinkedList<>(span.getStyle());
                    styles.add(result == selected ? "selected-search-result" : "search-result");
                    styleSpans.add(styles, span.getLength());
                }
                editor.setStyleSpans(result.start, styleSpans.create());
            }
        });
    }


    private static record Result(int start, int end) {

        public Result(Matcher matcher) {
            this(matcher.start(), matcher.end());
        }

        public int length() {
            return end - start;
        }

        public int distanceTo(int position) {
            if (position < start) return start - position;
            if (position > end) return position - end;
            return 0;
        }

    }
}
