package net.jamsimulator.jams.gui.editor;

import javafx.collections.ListChangeListener;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.fxmisc.richtext.model.Paragraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a bar that hints errors, warnings and information about the current file.
 * This bar is usually located at the right side of the editor.
 */
public class EditorHintBar extends Region {

    private final CodeFileEditor editor;
    private final Set<Hint> linesHints;

    /**
     * Creates the hint bar and binds all events to the editor.
     *
     * @param editor the editor.
     */
    public EditorHintBar(CodeFileEditor editor) {
        this.editor = editor;
        this.linesHints = new HashSet<>();

        editor.getParagraphs().addListener(
                (ListChangeListener<? super Paragraph<Collection<String>, String, Collection<String>>>) e -> {
                    double heightPerLine = getHeight() / e.getList().size();
                    double scaleFix = getScaleFix();

                    linesHints.forEach(hint -> hint.rectangle.setLayoutY(heightPerLine * hint.line * scaleFix));
                });

        heightProperty().addListener((obs, old, val) -> {
            double heightPerLine = val.doubleValue() / editor.getParagraphs().size();
            double scaleFix = getScaleFix();

            linesHints.forEach(hint -> hint.rectangle.setLayoutY(heightPerLine * hint.line * scaleFix));
        });

        editor.totalHeightEstimateProperty().addListener((obs, old, val) -> {
            double heightPerLine = getHeight() / editor.getParagraphs().size();
            double scaleFix = getScaleFix();

            linesHints.forEach(hint -> hint.rectangle.setLayoutY(heightPerLine * hint.line * scaleFix));
        });
    }

    /**
     * Adds a hint, replacing any existing hint.
     *
     * @param line the line where the hint is located.
     * @param type the type of the hint.
     */
    public void addHint(int line, HintType type) {
        addHint(line, type.style);
    }

    /**
     * Adds a hint, replacing any existing hint.
     *
     * @param line           the line where the hint is located.
     * @param rectangleStyle the style of the rectangle representing the hint.
     */
    public void addHint(int line, String rectangleStyle) {
        removeHint(line);

        var rectangle = new Rectangle(0, 2);
        rectangle.getStyleClass().add(rectangleStyle);
        rectangle.widthProperty().bind(widthProperty());

        double heightPerLine = getHeight() / editor.getParagraphs().size();
        rectangle.layoutYProperty().set(line * heightPerLine * getScaleFix());

        rectangle.setCursor(Cursor.HAND);
        rectangle.setOnMouseClicked(event -> editor.showParagraphAtTop(line));

        getChildren().add(rectangle);
        linesHints.add(new Hint(rectangle, line));
    }

    /**
     * Removes the hint located at the given line.
     *
     * @param line the line where the hint is located.
     */
    public void removeHint(int line) {
        getHint(line).ifPresent(value -> {
            getChildren().remove(value.rectangle);
            linesHints.remove(value);
        });
    }

    /**
     * Removes all hint from this bar.
     */
    public void clearHints() {
        linesHints.clear();
        getChildren().clear();
    }

    /**
     * Updates the internal data when the given line is being removed.
     *
     * @param line the line being removed.
     */
    public void applyLineRemoval(int line) {
        removeHint(line);

        double heightPerLine = getHeight() / editor.getParagraphs().size();
        double scaleFix = getScaleFix();
        linesHints.stream().filter(target -> target.line > line)
                .forEach(target -> target.move(-1, heightPerLine, scaleFix));
    }

    /**
     * Updates the internal data when the given line is being added.
     *
     * @param line the line being added.
     */
    public void applyLineAddition(int line) {
        double heightPerLine = getHeight() / editor.getParagraphs().size();
        double scaleFix = getScaleFix();
        linesHints.stream().filter(target -> target.line >= line)
                .forEach(target -> target.move(1, heightPerLine, scaleFix));
    }

    private double getScaleFix() {
        double scaleFix = 1;
        try {
            scaleFix = Math.min(editor.getTotalHeightEstimate() / editor.getViewportHeight(), 1);
        } catch (Exception ignore) {
        }
        return scaleFix;
    }

    private Optional<Hint> getHint(int line) {
        return linesHints.stream().filter(target -> target.line == line).findAny();
    }

    /**
     * Small helper enum containing general hints.
     */
    public enum HintType {

        ERROR("hint-bar-error"),
        WARNING("hint-bar-warning"),
        INFO("hint-bar-info");

        private final String style;

        HintType(String style) {
            this.style = style;
        }

        /**
         * Returns style of the rectangle representing this hint type.
         *
         * @return the style.
         */
        public String getStyle() {
            return style;
        }
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
    }

}
