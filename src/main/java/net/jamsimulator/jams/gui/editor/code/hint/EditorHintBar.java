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

package net.jamsimulator.jams.gui.editor.code.hint;

import javafx.animation.AnimationTimer;
import javafx.collections.ListChangeListener;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.InspectionLevel;
import net.jamsimulator.jams.utils.Validate;
import org.fxmisc.richtext.model.Paragraph;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a bar that hints errors, warnings and information about the current file.
 * This bar is usually located on the right side of the editor.
 */
public class EditorHintBar extends Region {

    private final CodeFileEditor editor;
    private final Queue<QueuedHint> queue = new ConcurrentLinkedQueue<>();
    private final Set<Hint> linesHints = new HashSet<>();
    private final QueueConsumer consumer = new QueueConsumer();

    public EditorHintBar(CodeFileEditor editor) {
        this.editor = editor;
        consumer.start();
        initListeners();
    }


    private void initListeners() {
        Runnable run = () -> {
            double heightPerLine = getHeight() / editor.getParagraphs().size();
            double scaleFix = getScaleFix();
            linesHints.forEach(hint -> hint.rectangle.setLayoutY(heightPerLine * hint.line * scaleFix));
        };
        editor.getParagraphs().addListener(
                (ListChangeListener<? super Paragraph<Collection<String>, String, Collection<String>>>) e -> run.run());
        heightProperty().addListener((obs, old, val) -> run.run());
        editor.totalHeightEstimateProperty().addListener((obs, old, val) -> run.run());
    }

    public void addHint(int line, InspectionLevel level) {
        Validate.notNull(level, "Level cannot be null!");
        queue.add(new QueuedHint(line, QueueAction.EDIT, level));
    }

    public void removeHint(int line) {
        addHint(line, InspectionLevel.NONE);
    }

    public void addLine(int line) {
        queue.add(new QueuedHint(line, QueueAction.ADD, InspectionLevel.NONE));
    }

    public void removeLine(int line) {
        queue.add(new QueuedHint(line, QueueAction.REMOVE, InspectionLevel.NONE));
    }

    public void clear() {
        queue.add(new QueuedHint(-1, QueueAction.CLEAR, InspectionLevel.NONE));
    }

    public void dispose() {
        consumer.stop();
    }

    private void queueRemove(int line) {
        var hint = linesHints.stream().filter(it -> it.line == line).findAny();
        if (hint.isEmpty()) return;
        linesHints.remove(hint.get());
        getChildren().remove(hint.get().rectangle);
    }

    private void queueAdd(int line, String style) {
        var optional = linesHints.stream().filter(it -> it.line == line).findAny();
        if (optional.isPresent()) {
            optional.get().rectangle.getStyleClass().setAll(style);
        } else {
            var rectangle = new Rectangle(0, 2);
            var hint = new Hint(rectangle, line);

            rectangle.getStyleClass().add(style);
            rectangle.widthProperty().bind(widthProperty());

            double heightPerLine = getHeight() / editor.getParagraphs().size();
            rectangle.layoutYProperty().set(line * heightPerLine * getScaleFix());

            rectangle.setCursor(Cursor.HAND);
            rectangle.setOnMouseClicked(event -> editor.showParagraphAtTop(hint.line));

            getChildren().add(rectangle);
            linesHints.add(hint);
        }
    }

    private void queueAddLine(int line) {
        double heightPerLine = getHeight() / editor.getParagraphs().size();
        double scaleFix = getScaleFix();
        linesHints.stream().filter(it -> it.line >= line).forEach(it -> it.move(1, heightPerLine, scaleFix));
    }

    private void queueRemoveLine(int line) {
        double heightPerLine = getHeight() / editor.getParagraphs().size();
        double scaleFix = getScaleFix();
        queueRemove(line);
        linesHints.stream().filter(it -> it.line > line).forEach(it -> it.move(-1, heightPerLine, scaleFix));
    }

    private void queueClear() {
        queue.clear();
        linesHints.forEach(it -> getChildren().remove(it.rectangle));
        linesHints.clear();
    }

    private double getScaleFix() {
        double scaleFix = 1;
        try {
            scaleFix = Math.min(editor.getTotalHeightEstimate() / editor.getViewportHeight(), 1);
        } catch (Exception ignore) {
        }
        return scaleFix;
    }

    private class QueueConsumer extends AnimationTimer {

        @Override
        public void handle(long l) {
            QueuedHint hint;
            while ((hint = queue.poll()) != null) {
                switch (hint.action()) {
                    case EDIT -> {
                        var style = hint.level().getHintStyle().orElse(null);
                        if (style == null) queueRemove(hint.line());
                        else queueAdd(hint.line(), style);
                    }
                    case ADD -> queueAddLine(hint.line());
                    case REMOVE -> queueRemoveLine(hint.line());
                    case CLEAR -> queueClear();
                }
            }
        }
    }

    private enum QueueAction {
        EDIT, ADD, REMOVE, CLEAR
    }

    private static record QueuedHint(int line, QueueAction action, InspectionLevel level) {
    }

    private static class Hint {

        final Rectangle rectangle;
        int line;

        Hint(Rectangle rectangle, int line) {
            this.rectangle = rectangle;
            this.line = line;
        }

        void move(int amount, double heightPerLine, double scaleFix) {
            line += amount;
            rectangle.setLayoutY(heightPerLine * line * scaleFix);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Hint hint = (Hint) o;
            return line == hint.line;
        }

        @Override
        public int hashCode() {
            return Objects.hash(line);
        }
    }


}
