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

package net.jamsimulator.jams.gui.util.log;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.time.LocalDateTime;
import java.util.Collections;

public class SimpleLog extends HBox implements Log {

    protected final CodeArea display;
    protected final SimpleStringProperty lastLineProperty;
    protected final SimpleObjectProperty<LocalDateTime> lastLineTimeProperty;

    protected ToggleButton followButton;

    public SimpleLog() {
        super();

        var buttons = new VBox();
        var clear = new Button("C");
        clear.setOnAction(event -> clear());
        clear.getStyleClass().add("button-bold");
        buttons.getChildren().add(clear);

        followButton = new ToggleButton("▼");
        followButton.setSelected(true);
        followButton.getStyleClass().add("button-bold");
        buttons.getChildren().add(followButton);

        getChildren().add(buttons);

        display = new CodeArea();
        var scroll = new VirtualizedScrollPane<>(new ScaledVirtualized<>(display));
        display.setEditable(false);
        applyZoomListener(scroll);
        display.getStyleClass().add("display");
        getChildren().add(scroll);

        followButton.setOnAction(event -> {
            if (followButton.isSelected()) display.scrollYBy(Double.MAX_VALUE);
        });

        display.prefWidthProperty().bind(widthProperty().subtract(buttons.widthProperty()));

        lastLineProperty = new SimpleStringProperty("");
        lastLineTimeProperty = new SimpleObjectProperty<>(LocalDateTime.now());
        display.textProperty().addListener((obs, old, val) -> {
            var paragraphs = display.getParagraphs().size();
            if (paragraphs == 0) lastLineProperty.set("");
            else if (paragraphs == 1) lastLineProperty.set(val);
            else {
                var paragraph = display.getParagraph(paragraphs - 1);
                if (paragraph.getText().isEmpty()) paragraph = display.getParagraph(paragraphs - 2);
                lastLineProperty.set(paragraph.getText());
            }
            lastLineTimeProperty.set(LocalDateTime.now());
        });
    }

    @Override
    public void print(Object object) {
        Platform.runLater(() -> display.appendText(object == null ? "null" : object.toString()));
    }

    @Override
    public void println(Object object) {
        Platform.runLater(() -> display.appendText((object == null ? "null" : object.toString()) + '\n'));
    }

    @Override
    public void printError(Object object) {
        printAndStyle(object, "log_error");
    }

    @Override
    public void printErrorLn(Object object) {
        printAndStyleLn(object, "log_error");
    }

    @Override
    public void printInfo(Object object) {
        printAndStyle(object, "log_info");
    }

    @Override
    public void printInfoLn(Object object) {
        printAndStyleLn(object, "log_info");
    }

    @Override
    public void printWarning(Object object) {
        printAndStyle(object, "log_warning");
    }

    @Override
    public void printWarningLn(Object object) {
        printAndStyleLn(object, "log_warning");
    }

    @Override
    public void printDone(Object object) {
        printAndStyle(object, "log_done");
    }

    @Override
    public void printDoneLn(Object object) {
        printAndStyleLn(object, "log_done");
    }

    @Override
    public void println() {
        println("");
    }

    @Override
    public void clear() {
        Platform.runLater(display::clear);
    }

    @Override
    public boolean isFollowingText() {
        return followButton.isSelected();
    }

    @Override
    public void followText(boolean followText) {
        Platform.runLater(() -> {
            followButton.setSelected(followText);
            if (followText) display.scrollYBy(Double.MAX_VALUE);
        });
    }

    @Override
    public StringProperty lastLineProperty() {
        return lastLineProperty;
    }

    @Override
    public ObjectProperty<LocalDateTime> lastLineTimeProperty() {
        return lastLineTimeProperty;
    }

    private void printAndStyle(Object object, String style) {
        Platform.runLater(() -> {
            int from = display.getLength();
            display.appendText(object == null ? "null" : object.toString());
            display.setStyle(from, display.getLength(), Collections.singleton(style));
            if (followButton.isSelected()) display.scrollYBy(Double.MAX_VALUE);
        });
    }

    private void printAndStyleLn(Object object, String style) {
        Platform.runLater(() -> {
            int from = display.getLength();
            display.appendText((object == null ? "null" : object.toString()) + '\n');
            display.setStyle(from, display.getLength(), Collections.singleton(style));
            if (followButton.isSelected()) display.scrollYBy(Double.MAX_VALUE);
        });
    }

    protected void applyZoomListener(VirtualizedScrollPane<ScaledVirtualized<CodeArea>> scroll) {
        display.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                double current = scroll.getContent().getZoom().getX();
                if (event.getDeltaY() < 0) {
                    if (current > 0.4) {
                        scroll.getContent().getZoom().setX(current - 0.2);
                        scroll.getContent().getZoom().setY(current - 0.2);
                    }
                } else if (event.getDeltaY() > 0) {
                    scroll.getContent().getZoom().setX(current + 0.2);
                    scroll.getContent().getZoom().setY(current + 0.2);
                }
                event.consume();
            }
        });

        //RESET
        display.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.isControlDown() && event.getButton() == MouseButton.MIDDLE) {
                scroll.getContent().getZoom().setX(1);
                scroll.getContent().getZoom().setY(1);
                scroll.getContent().getZoom().setZ(1);
            }
        });
    }
}
