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


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMenuBuilder;
import net.jamsimulator.jams.gui.editor.popup.AutocompletionPopup;
import net.jamsimulator.jams.gui.editor.popup.DocumentationPopup;
import net.jamsimulator.jams.gui.theme.event.ThemeRefreshEvent;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.GUIReflectionUtils;
import net.jamsimulator.jams.gui.util.KeyCombinationBuilder;
import net.jamsimulator.jams.gui.util.ZoomUtils;
import net.jamsimulator.jams.utils.FileUtils;
import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.reactfx.Subscription;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a code editor.
 */
public class CodeFileEditor extends CodeArea implements FileEditor {

    protected final FileEditorTab tab;
    protected String old, original;
    protected VirtualizedScrollPane<ScaledVirtualized<CodeFileEditor>> scrollPane;
    protected ScaledVirtualized<CodeFileEditor> zoom;
    protected EditorHintBar hintBar;

    protected CodeFileEditorSearch search;
    protected CodeFileEditorReplace replace;

    protected AutocompletionPopup autocompletionPopup;
    protected DocumentationPopup documentationPopup;
    private ChangeListener<? super Number> autocompletionMoveListener;

    private final Subscription textRefreshSubscription;
    private boolean textRefreshEnabled;

    public CodeFileEditor(FileEditorTab tab) {
        super(read(tab));
        this.tab = tab;
        this.original = getText();
        this.hintBar = new EditorHintBar(this);

        search = new CodeFileEditorSearch(this);
        replace = new CodeFileEditorReplace(this);

        zoom = new ScaledVirtualized<>(this);
        scrollPane = new VirtualizedScrollPane<>(zoom);
        ZoomUtils.applyZoomListener(this, zoom);

        CustomLineNumberFactory factory = CustomLineNumberFactory.get(this);

        //JamsApplication.getThemeManager().apply(this);
        JamsApplication.getThemeManager().registerListeners(this, true);

        setParagraphGraphicFactory(factory);
        applyOldTextListener();
        applyAutoIndent();
        applyIndentRemover();
        applySaveMarkListener();
        initializeAutocompletionPopupListeners();
        initializeActionsListeners();

        setOnContextMenuRequested(request -> {
            createContextMenu(request.getScreenX(), request.getScreenY());
            request.consume();
        });

        focusedProperty().addListener((obs, old, val) -> {
            if (val) {
                var t = getTab();
                if (t != null) {
                    var holder = t.getList().getHolder();
                    if (holder == null) return;
                    holder.setLastFocusedEditor(this);
                }
            }
        });

        textRefreshEnabled = true;
        textRefreshSubscription = multiPlainChanges().subscribe(event -> {
            if (textRefreshEnabled) event.forEach(this::onTextRefresh);
            var top = tab.getTopNode().orElse(null);
            if (top == search) search.refreshText();
            if (top == replace) replace.refreshText();
        });
    }

    private static String read(FileEditorTab tab) {
        if (tab == null) return "";
        try {
            return FileUtils.readAll(tab.getFile());
        } catch (IOException ex) {
            StringWriter writer = new StringWriter();
            ex.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }
    }

    /**
     * Returns the {@link FileEditorTab} holding this editor.
     *
     * @return the {@link FileEditorTab}
     */
    public FileEditorTab getTab() {
        return tab;
    }

    /**
     * Returns the {@link EditorHintBar hint bar} of this editor.
     * The hint bar is used to show errors, warnings and information on the right side of the editor.
     *
     * @return the {@link EditorHintBar hint bar}.
     */
    public EditorHintBar getHintBar() {
        return hintBar;
    }

    /**
     * Returns the {@link CodeFileEditorSearch search bar} linked to this editor.
     *
     * @return the {@link CodeFileEditorSearch search bar}.
     */
    public CodeFileEditorSearch getSearch() {
        return search;
    }

    /**
     * Returns the {@link CodeFileEditorReplace replace bar} linked to this editor.
     *
     * @return the {@link CodeFileEditorReplace replace bar}.
     */
    public CodeFileEditorReplace getReplace() {
        return replace;
    }

    /**
     * Returns the {@link AutocompletionPopup} used in this editor.
     *
     * @return the {@link AutocompletionPopup}.
     */
    public AutocompletionPopup getAutocompletionPopup() {
        return autocompletionPopup;
    }

    //region actions

    /**
     * Returns the {@link Popup} showing the current documentation.
     *
     * @return the popup.
     */
    public DocumentationPopup getDocumentationPopup() {
        return documentationPopup;
    }

    /**
     * Returns a new {@link List} with all lines of this file inside.
     *
     * @return the {@link List}.
     */
    public List<CodeFileLine> getLines() {
        List<String> raw = StringUtils.multiSplit(getText(), "\n", "\r");
        List<CodeFileLine> lines = new ArrayList<>(raw.size());
        if (raw.isEmpty()) return lines;

        int current = 0;
        int amount = 0;
        for (String line : raw) {
            lines.add(new CodeFileLine(current, line, amount));
            amount += line.length() + 1;
            current++;
        }

        return lines;
    }

    /**
     * Returns the line at the given index.
     * <p>
     * If the index is negative the first line will be returned.
     * If the index exceeds the amount of lines, the last line will be returned.
     * If the file is empty, an empty {@link CodeFileLine} will be returned.
     *
     * @param index the index.
     * @return the {@link CodeFileLine}.
     */
    public CodeFileLine getLine(int index) {
        if (index < 0) index = 0;
        List<String> lines = StringUtils.multiSplit(getText(), "\n", "\r");

        if (lines.isEmpty()) return new CodeFileLine(0, "", 0);

        int current = 0;
        int amount = 0;
        for (String line : lines) {
            if (current >= index) break;
            amount += line.length() + 1;

            current++;
        }

        if (index >= lines.size()) index = lines.size() - 1;

        return new CodeFileLine(index, lines.get(index), amount);
    }

    /**
     * Returns the line at the given absolute position.
     * <p>
     * If the position is negative the first line will be returned.
     * If the position exceeds the amount of lines, the last line will be returned.
     * If the file is empty, an empty {@link CodeFileLine} will be returned.
     *
     * @param position the position.
     * @return the {@link CodeFileLine}.
     */
    public CodeFileLine getLineFromAbsolutePosition(int position) {
        if (position < 0) position = 0;
        List<String> lines = StringUtils.multiSplit(getText(), "\n", "\r");
        if (lines.isEmpty()) return new CodeFileLine(0, "", 0);

        int index = 0;
        int amount = 0;
        for (String line : lines) {
            position -= line.length() + 1;
            if (position < 0) break;
            amount += line.length() + 1;
            index++;
        }

        if (index >= lines.size()) index = lines.size() - 1;

        return new CodeFileLine(index, lines.get(index), amount);
    }

    /**
     * Returns whether the text refresh event is enabled.
     *
     * @return whether the text refresh event is enabled.
     * @see #enableRefreshEvent(boolean)
     */
    public boolean isRefreshEventEnabled() {
        return textRefreshEnabled;
    }

    /**
     * Enables or disables {@link #onTextRefresh(PlainTextChange)} calls.
     *
     * @param enabled wheteher calls are enabled.
     */
    public void enableRefreshEvent(boolean enabled) {
        textRefreshEnabled = enabled;
    }

    /**
     * Duplicates the current line.
     * If a selection is made, the selection will be duplicated instead.
     */
    public void duplicateCurrentLine() {
        IndexRange selection = getSelection();

        if (selection.getStart() == selection.getEnd()) {
            var start = getCaretPosition() - getCaretColumn();
            var end = start + getParagraphLength(getCurrentParagraph());
            selection = new IndexRange(start, end);

            String text = getText(selection);
            replaceText(selection.getEnd(), selection.getEnd(), "\n" + text);
            moveTo(selection.getEnd() + text.length() + 1);
        } else {
            String text = getText(selection);
            replaceText(selection.getEnd(), selection.getEnd(), text);
            moveTo(selection.getEnd() + text.length());
            getCaretSelectionBind().selectRange(selection.getEnd(), selection.getEnd() + text.length());
        }
    }

    /**
     * Reformats the file.
     * This method should be overridden by these children's classes.
     */
    public void reformat() {
    }

    /**
     * Replaces all the text of this editor with the given code.
     * This method should be overriden by child implementations to grant good performance.
     *
     * @param text the new text.
     * @return whether the operation was successfully executed.
     * Returns false when the text to replace is the same to the text in the editor.
     */
    public boolean replaceAllText(String text) {
        if (text.equals(getText())) return false;
        replace(0, getLength(), text, Collections.emptySet());
        tab.setSaveMark(true);
        tab.layoutDisplay();
        return true;
    }

    //endregion

    //region refresh

    protected void onTextRefresh(PlainTextChange change) {

    }

    //endregion

    //region override

    @Override
    public void onClose() {
        JamsApplication.getThemeManager().unregisterListeners(this);
        JamsApplication.getStage().xProperty().removeListener(autocompletionMoveListener);
        JamsApplication.getStage().yProperty().removeListener(autocompletionMoveListener);
        JamsApplication.getStage().widthProperty().removeListener(autocompletionMoveListener);
        JamsApplication.getStage().heightProperty().removeListener(autocompletionMoveListener);
        textRefreshSubscription.unsubscribe();
    }

    @Override
    public void save() {
        try {
            if (tab == null) return;
            FileUtils.writeAll(tab.getFile(), original = getText());
            tab.setSaveMark(false);
            tab.layoutDisplay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        replaceText(0, getText().length(), original = read(tab));
        tab.setSaveMark(false);
        tab.layoutDisplay();
    }

    @Override
    public void addNodesToTab(AnchorPane tabAnchorPane) {
        tabAnchorPane.getChildren().add(scrollPane);
        AnchorUtils.setAnchor(scrollPane, 0, 0, 0, 0);

        var optional = GUIReflectionUtils.getVerticalScrollBar(scrollPane);

        tabAnchorPane.getChildren().add(hintBar);
        hintBar.setPrefWidth(15);
        AnchorUtils.setAnchor(hintBar, 0, 0, -1, optional
                .map(bar -> bar.isVisible() ? bar.getWidth() : 0.0).orElse(0.0));

        if (optional.isEmpty()) return;
        var bar = optional.get();

        bar.widthProperty().addListener((obs, old, val) ->
                AnchorUtils.setAnchor(hintBar, 0, 0, -1, bar.isVisible() ? val.doubleValue() : 0));
        bar.visibleProperty().addListener((obs, old, val) ->
                AnchorUtils.setAnchor(hintBar, 0, 0, -1, val ? bar.getWidth() : 0));
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.TEXT_EDITOR.equals(region) ||
                RegionTags.EDITOR.equals(region) ||
                RegionTags.EDITOR_TAB.equals(region);
    }

    public VirtualizedScrollPane<?> getScrollPane() {
        return scrollPane;
    }


    //endregion

    //region configuration

    public ScaledVirtualized<?> getZoom() {
        return zoom;
    }

    private void applyOldTextListener() {
        textProperty().addListener((obs, old, value) -> this.old = old);
    }

    protected void applyAutoIndent() {
        Pattern whiteSpace = Pattern.compile("^\\s+");
        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                int caretPosition = getCaretPosition();
                int currentParagraph = getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find()) Platform.runLater(() -> insertText(caretPosition, m0.group()));
            }
        });
    }

    protected void applyIndentRemover() {
        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {

                int caretPosition = getCaretPosition();

                //If shift is pressed, then just execute a normal backspace.
                if (event.isShiftDown()) {
                    return;
                }

                int currentParagraph = getCurrentParagraph();
                Position position = offsetToPosition(caretPosition, Bias.Forward);

                String s = old.substring(caretPosition - position.getMinor(), caretPosition + 1);
                if (s.trim().isEmpty()) {
                    int to = caretPosition - position.getMinor() - 1;
                    if (to < 0) to = 0;

                    boolean lastParagraphEmpty = currentParagraph != 0 && getParagraph(currentParagraph - 1).getText().isEmpty();

                    replaceText(to, caretPosition, lastParagraphEmpty ? s : "");
                }
            }
        });
    }

    protected void initializeAutocompletionPopupListeners() {

        //AUTO COMPLETION
        addEventHandler(KeyEvent.KEY_TYPED, event -> {
            if (autocompletionPopup != null && autocompletionPopup.manageTypeEvent(event)) event.consume();
            if (documentationPopup != null) documentationPopup.hide();
        });

        //AUTOCOMPLETION MOVEMENT
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (autocompletionPopup != null && autocompletionPopup.managePressEvent(event)) {
                event.consume();
                if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                    if (documentationPopup != null) documentationPopup.hide();
                }
            } else {
                if (documentationPopup != null) documentationPopup.hide();
            }
        });

        //FOCUS
        focusedProperty().addListener((obs, old, val) -> {
            if (autocompletionPopup != null) autocompletionPopup.hide();
            if (documentationPopup != null) documentationPopup.hide();
        });
        //CLICK
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (autocompletionPopup != null) autocompletionPopup.hide();
            if (documentationPopup != null) documentationPopup.hide();
        });

        //MOVE
        autocompletionMoveListener = (obs, old, val) -> {
            if (autocompletionPopup != null) autocompletionPopup.hide();
            if (documentationPopup != null) documentationPopup.hide();
        };
        JamsApplication.getStage().xProperty().addListener(autocompletionMoveListener);
        JamsApplication.getStage().yProperty().addListener(autocompletionMoveListener);
        JamsApplication.getStage().widthProperty().addListener(autocompletionMoveListener);
        JamsApplication.getStage().heightProperty().addListener(autocompletionMoveListener);
    }

    protected void initializeActionsListeners() {
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            try {
                KeyCodeCombination combination = new KeyCombinationBuilder(event).build();
                if (JamsApplication.getActionManager().executeAction(combination, this)) {

                    KeyCode c = event.getCode();
                    if (c == KeyCode.UP || c == KeyCode.DOWN || c == KeyCode.LEFT || c == KeyCode.RIGHT
                            || c == KeyCode.DELETE || c == KeyCode.BACK_SPACE) return;
                    event.consume();
                }
            } catch (IllegalArgumentException ignore) {
            }
        });
        addEventFilter(MouseEvent.MOUSE_CLICKED, event -> JamsApplication.hideContextMenu());
    }

    protected void applySaveMarkListener() {
        addEventHandler(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().isEmpty()) return;
            if (tab != null) {
                tab.setSaveMark(!getText().equals(original));
                tab.layoutDisplay();
            }
        });
    }

    private void createContextMenu(double screenX, double screenY) {
        Set<ContextAction> set = getSupportedContextActions();
        if (set.isEmpty()) return;
        ContextMenu main = new ContextActionMenuBuilder(this).addAll(set).build();
        JamsApplication.openContextMenu(main, this, screenX, screenY);
    }

    private Set<ContextAction> getSupportedContextActions() {
        Set<Action> actions = JamsApplication.getActionManager();
        Set<ContextAction> set = new HashSet<>();
        for (Action action : actions) {
            if (action instanceof ContextAction && supportsActionRegion(action.getRegionTag())
                    && ((ContextAction) action).supportsTextEditorState(this)) {
                set.add((ContextAction) action);
            }
        }
        return set;
    }

    @Listener
    private void onThemeRefresh(ThemeRefreshEvent event) {
        //JamsApplication.getThemeManager().apply(this);
    }

    //endregion
}
