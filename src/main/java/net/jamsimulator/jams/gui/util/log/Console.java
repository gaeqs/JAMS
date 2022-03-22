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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.gui.util.log.event.ConsoleInputEvent;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Console extends HBox implements Log, EventBroadcast {

    protected final SimpleStringProperty lastLineProperty;
    protected final SimpleObjectProperty<LocalDateTime> lastLineTimeProperty;

    protected ToggleButton followButton;

    protected final LinkedList<String> inputs;
    protected VBox buttons;
    protected CodeArea display;
    protected TextField input;
    protected VBox inputsDisplay;
    protected boolean willRefresh = false;

    protected SimpleEventBroadcast broadcast;

    protected LinkedList<Pair> buffer;


    public Console() {
        super();
        getStyleClass().add("log");

        inputs = new LinkedList<>();
        broadcast = new SimpleEventBroadcast();
        buffer = new LinkedList<>();

        loadButtons();
        loadDisplay();

        loadInputListeners();

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

    public Optional<String> popInput() {
        try {
            synchronized (inputs) {
                Optional<String> optional = Optional.of(inputs.removeFirst());
                refreshLater();
                return optional;
            }
        } catch (NoSuchElementException ex) {
            return Optional.empty();
        }
    }

    public Optional<Character> popChar() {
        try {
            synchronized (inputs) {
                boolean found = false;
                char c = 0;
                String str;

                while (!found) {
                    str = inputs.getFirst();
                    if (str.isEmpty()) {
                        inputs.removeFirst();
                    } else {
                        c = str.charAt(0);
                        found = true;
                        if (str.length() == 1) {
                            inputs.removeFirst();
                        } else {
                            inputs.set(0, str.substring(1));
                        }
                        refreshLater();
                    }
                }
                return Optional.of(c);
            }
        } catch (NoSuchElementException ex) {
            return Optional.empty();
        }
    }

    @Override
    public void print(Object object) {
        printAndStyle(object, null);
    }

    @Override
    public void println(Object object) {
        printAndStyleLn(object, null);
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
        Platform.runLater(() -> display.clear());
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

    public void flush() {
        Platform.runLater(() -> {
            Pair pair;
            while (!buffer.isEmpty()) {
                pair = buffer.pop();
                int from = display.getLength();
                display.appendText(pair.text);
                if (pair.style != null) {
                    display.setStyle(from, display.getLength(), Collections.singleton(pair.style));
                }
            }
            if (followButton.isSelected()) display.scrollYBy(Double.MAX_VALUE);
        });
    }

    private void printAndStyle(Object object, String style) {
        buffer.add(new Pair(object == null ? "null" : object.toString(), style));
    }

    private void printAndStyleLn(Object object, String style) {
        buffer.add(new Pair(object == null ? "null\n" : object.toString() + '\n', style));
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

    protected void refreshLater() {
        if (willRefresh) return;
        willRefresh = true;
        Platform.runLater(() -> {
            willRefresh = false;
            inputsDisplay.getChildren().clear();

            int index = 0;
            for (String in : inputs) {
                inputsDisplay.getChildren().add(new ConsoleInput(in, index++, this));
            }
        });
    }

    private void loadButtons() {
        buttons = new VBox();
        buttons.setAlignment(Pos.TOP_CENTER);

        var clear = new Button("C");
        clear.setOnAction(event -> clear());
        clear.getStyleClass().add("button-bold");

        var clearInputs = new Button("Ci");
        clearInputs.setOnAction(event -> inputsDisplay.getChildren().clear());
        clearInputs.getStyleClass().add("button-bold");

        followButton = new ToggleButton("▼");
        followButton.setSelected(true);
        followButton.setOnAction(event -> followText(!isFollowingText()));
        followButton.getStyleClass().add("button-bold");

        followButton.setOnAction(event -> {
            if (followButton.isSelected()) display.scrollYBy(Double.MAX_VALUE);
        });

        buttons.getChildren().addAll(clear, clearInputs, followButton);
        getChildren().add(buttons);
    }

    private void loadDisplay() {
        VBox vbox = new VBox();
        AnchorPane anchor = new AnchorPane();

        display = new CodeArea();
        VirtualizedScrollPane<ScaledVirtualized<CodeArea>> scroll = new VirtualizedScrollPane<>(new ScaledVirtualized<>(display));
        display.setEditable(false);
        applyZoomListener(scroll);
        display.getStyleClass().add("display");

        inputsDisplay = new VBox();
        inputsDisplay.getStyleClass().add("inputs");

        ScrollPane inputsScroll = new PixelScrollPane(inputsDisplay);
        inputsScroll.setPadding(new Insets(0));
        inputsScroll.setFitToWidth(true);
        inputsScroll.setFitToHeight(true);
        inputsScroll.setPrefWidth(200);
        inputsDisplay.minHeightProperty().bind(inputsScroll.heightProperty());

        anchor.getChildren().add(inputsScroll);
        anchor.getChildren().add(scroll);

        AnchorUtils.setAnchor(scroll, 0, 0, 0, 200);
        AnchorUtils.setAnchor(inputsScroll, 0, 0, -1, 0);

        vbox.getChildren().add(anchor);

        input = new TextField();
        vbox.getChildren().add(input);
        input.setMaxHeight(30);
        input.setPromptText("> ...");
        input.getStyleClass().add("prompt");

        anchor.prefWidthProperty().bind(widthProperty().subtract(buttons.widthProperty()));
        anchor.prefHeightProperty().bind(heightProperty().subtract(input.heightProperty()));

        getChildren().add(vbox);
    }

    private void loadInputListeners() {
        input.setOnAction(event -> {
            String data = input.getText();
            if (data.isEmpty()) return;

            ConsoleInputEvent.Before before =
                    callEvent(new ConsoleInputEvent.Before(this, data));
            if (before.isCancelled()) return;

            inputs.add(before.getInput());
            refreshLater();
            input.setText("");

            callEvent(new ConsoleInputEvent.After(this, data));
        });
    }


    @Override
    public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
        return broadcast.registerListener(instance, method, useWeakReferences);
    }

    @Override
    public int registerListeners(Object instance, boolean useWeakReferences) {
        return broadcast.registerListeners(instance, useWeakReferences);
    }

    @Override
    public boolean unregisterListener(Object instance, Method method) {
        return broadcast.unregisterListener(instance, method);
    }

    @Override
    public int unregisterListeners(Object instance) {
        return broadcast.unregisterListeners(instance);
    }

    @Override
    public <T extends Event> T callEvent(T event) {
        return broadcast.callEvent(event, this);
    }

    @Override
    public void transferListenersTo(EventBroadcast broadcast) {
        this.broadcast.transferListenersTo(broadcast);
    }

    private static class Pair {
        String text, style;

        public Pair(String text, String style) {
            this.text = text;
            this.style = style;
        }
    }
}
