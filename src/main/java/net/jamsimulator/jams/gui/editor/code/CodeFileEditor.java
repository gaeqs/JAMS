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

package net.jamsimulator.jams.gui.editor.code;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.ActionManager;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMenuBuilder;
import net.jamsimulator.jams.gui.editor.FileEditor;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionPopup;
import net.jamsimulator.jams.gui.editor.code.hint.EditorHintBar;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorLineChange;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorPendingChanges;
import net.jamsimulator.jams.gui.editor.code.indexing.event.IndexFinishEditEvent;
import net.jamsimulator.jams.gui.editor.code.indexing.event.IndexRequestRefreshEvent;
import net.jamsimulator.jams.gui.editor.code.popup.DocumentationPopup;
import net.jamsimulator.jams.gui.editor.code.top.CodeFileEditorReplace;
import net.jamsimulator.jams.gui.editor.code.top.CodeFileEditorSearch;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.GUIReflectionUtils;
import net.jamsimulator.jams.gui.util.KeyCombinationBuilder;
import net.jamsimulator.jams.gui.util.ZoomUtils;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.project.GlobalIndexHolder;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.utils.FileUtils;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledAreaBehaviorParameters;
import org.reactfx.Subscription;

import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static net.jamsimulator.jams.gui.util.CodeFileEditorUtils.read;

public abstract class CodeFileEditor extends CodeArea implements FileEditor {

    private static final GenericStyledAreaBehaviorParameters PARAMETERS = new GenericStyledAreaBehaviorParameters();

    static {
        PARAMETERS.setIncludeCopy(false);
        PARAMETERS.setIncludeCut(false);
        PARAMETERS.setIncludePaste(false);
        PARAMETERS.setIncludeRedo(false);
        PARAMETERS.setIncludeUndo(false);
        PARAMETERS.setIncludeSelectAll(false);
        PARAMETERS.setIncludeShiftZRedo(false);
    }

    protected final FileEditorTab tab;
    protected final ScaledVirtualized<CodeFileEditor> zoom = new ScaledVirtualized<>(this);
    protected final VirtualizedScrollPane<ScaledVirtualized<CodeFileEditor>> scrollPane = new VirtualizedScrollPane<>(zoom);

    protected final Rectangle lineBackground;

    protected final EditorIndex index;

    protected final EditorHintBar hintBar = new EditorHintBar(this);
    protected final CodeFileEditorSearch search = new CodeFileEditorSearch(this);
    protected final CodeFileEditorReplace replace = new CodeFileEditorReplace(this);

    protected AutocompletionPopup autocompletionPopup;
    protected DocumentationPopup documentationPopup;

    protected EditorPendingChanges pendingChanges = new EditorPendingChanges();

    protected EventHandler<?> popupHideHandler = event -> {
        JamsApplication.hideContextMenu();
        if (autocompletionPopup != null) autocompletionPopup.hide();
        if (documentationPopup != null) documentationPopup.hide();
    };

    protected ChangeListener<?> popupHideListener =
            (obs, old, val) -> popupHideHandler.handle(null);

    protected final Subscription subscription;
    protected final AnimationTimer styleTimer;

    protected int modificationCount = 0;
    protected boolean shouldOpenAutocompletionAfterEdit = false;

    public CodeFileEditor(FileEditorTab tab) {
        super(read(tab), PARAMETERS);
        this.tab = tab;
        this.index = getOrGenerateIndex();

        if (index != null) {
            index.registerListeners(this, true);
            index.withLock(false, i -> i.setHintBar(hintBar));
        }

        ZoomUtils.applyZoomListener(this, zoom);
        var factory = CustomLineNumberFactory.get(this);
        lineBackground = factory.getBackground();
        setParagraphGraphicFactory(factory);


        initializeAutocompletionPopupListeners();

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

        subscription = plainTextChanges()
                .conditionOn(editableProperty())
                .reduceSuccessions(it -> {
                    modificationCount++;
                    var list = new LinkedList<EditorLineChange>();
                    EditorLineChange.of(it, this, list);
                    return list;
                }, (list, it) -> {
                    EditorLineChange.of(it, this, list);
                    return list;
                }, Duration.ofMillis(100))
                .subscribe(this::accept);

        styleTimer = new StyleAnimation();
        styleTimer.start();

        tab.selectedProperty().addListener((obs, old, val) -> {
            if (val) Platform.runLater(this::styleVisibleArea);
        });

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

    /**
     * Executes the autocompletion if this editor is not being modified.
     *
     * @return whether the autocompletion has not been modified.
     */
    public boolean tryToAutocomplete() {
        if (!autocompletionPopup.isShowing()) return false;
        if (isIndexBeingModified()) return false;
        return autocompletionPopup.autocomplete();
    }

    /**
     * Returns the {@link Popup} showing the current documentation.
     *
     * @return the popup.
     */
    public DocumentationPopup getDocumentationPopup() {
        return documentationPopup;
    }

    /**
     * Returns the pending changes of this code editor.
     *
     * @return the pending changes to index.
     */
    public EditorPendingChanges getPendingChanges() {
        return pendingChanges;
    }

    /**
     * Returns the indexed representation of this editor.
     *
     * @return the indexed representation.
     */
    public EditorIndex getIndex() {
        return index;
    }

    /**
     * Returns whether this editor's index is being modififed.
     *
     * @return whether this editor's index is being modififed.
     */
    public boolean isIndexBeingModified() {
        return index != null && modificationCount > 0;
    }

    /**
     * Returns the {@link Project} of this file editor.
     * <p>
     * This is a shortcut for {@code tab.getWorkingPane().getprojectTab().getProject()}.
     *
     * @return the {@link Project}.
     */
    public Project getProject() {
        return tab.getWorkingPane().getProjectTab().getProject();
    }

    /**
     * Returns the {@link ScaledVirtualized} instance that manages the zoom
     * if this editor.
     *
     * @return the {@code ScaledVirtualized}.
     */
    public ScaledVirtualized<CodeFileEditor> getZoom() {
        return zoom;
    }

    /**
     * Duplicates the current line.
     * If a selection is made, the selection will be duplicated instead.
     */
    public void duplicateCurrentLine() {
        IndexRange selection = getSelection();

        if (selection.getStart() == selection.getEnd()) {
            int start = getCaretPosition() - getCaretColumn();
            int end = start + getParagraphLength(getCurrentParagraph());
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

    public void moveCurrentLineUp() {
        int line = getCurrentParagraph();
        if (line == 0) return;

        int column = getCaretColumn();
        int start = getCaretPosition() - column;

        var textPrevious = getParagraph(line - 1).getText();
        var textCurrent = getParagraph(line).getText();

        int startPrevious = start - textPrevious.length() - 1;

        if (!textCurrent.equals(textPrevious)) {
            int end = start + textCurrent.length();
            replaceText(startPrevious, end, textCurrent + "\n" + textPrevious);
        }

        moveTo(startPrevious + column);
    }

    public void moveCurrentLineDown() {
        int line = getCurrentParagraph();
        if (line == getParagraphs().size() - 1) return;

        int column = getCaretColumn();
        int start = getCaretPosition() - column;

        var textNext = getParagraph(line + 1).getText();
        var textCurrent = getParagraph(line).getText();

        if (!textNext.equals(textCurrent)) {
            int end = start + textCurrent.length();
            int endNext = end + 1 + textNext.length();
            replaceText(start, endNext, textNext + "\n" + textCurrent);
        }

        moveTo(start + textNext.length() + 1 + column);
    }

    public void deleteCurrentLine() {
        int start = getCaretPosition() - getCaretColumn();
        int end = start + getParagraphLength(getCurrentParagraph());
        replaceText(start == 0 ? 0 : start - 1, end, "");
    }

    /**
     * Reformats the file.
     * This method should be overridden by these children's classes.
     */
    public void reformat() {
        pendingChanges.markForReformat();
        setEditable(false);
        tab.getWorkingPane().getProjectTab().getProject().getTaskExecutor().executeIndexing(this);
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.TEXT_EDITOR.equals(region) ||
                RegionTags.EDITOR.equals(region) ||
                RegionTags.EDITOR_TAB.equals(region);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClose() {
        JamsApplication.getStage().xProperty().removeListener((ChangeListener<? super Number>) popupHideListener);
        JamsApplication.getStage().yProperty().removeListener((ChangeListener<? super Number>) popupHideListener);
        JamsApplication.getStage().widthProperty().removeListener((ChangeListener<? super Number>) popupHideListener);
        JamsApplication.getStage().heightProperty().removeListener((ChangeListener<? super Number>) popupHideListener);
        subscription.unsubscribe();
        styleTimer.stop();
        hintBar.dispose();
    }

    @Override
    public void save() {
        try {
            if (tab == null) return;
            FileUtils.writeAll(tab.getFile(), getText());
            tab.setSaveMark(false);
            tab.layoutDisplay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        var read = read(tab);
        var text = getText();
        if (text.equals(read)) return;
        replaceText(0, text.length(), read);
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

    protected abstract EditorIndex generateIndex();

    protected EditorIndex getOrGenerateIndex() {
        var data = getProject().getData();
        if (data instanceof GlobalIndexHolder holder) {
            var index = holder.getGlobalIndex().getIndex(tab.getFile());
            if (index.isPresent()) {
                return index.get();
            }
        }
        var index = generateIndex();
        tab.getWorkingPane().getProjectTab().getProject().getTaskExecutor()
                .executeIndexing(index, tab.getFile().getName(), getText());
        return index;
    }

    @SuppressWarnings("unchecked")
    protected void initializeAutocompletionPopupListeners() {
        //AUTOCOMPLETION MOVEMENT
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            try {
                if (Manager.get(ActionManager.class).executeAction(
                        new KeyCombinationBuilder(event).build(), this)) {
                    event.consume();
                }
            } catch (IllegalArgumentException ignore) {
                return;
            }
            var c = event.getCode();
            if (autocompletionPopup != null && autocompletionPopup.isShowing()) {
                boolean populate = false;
                int offset = 0;
                String extra = "";
                if (c == KeyCode.LEFT || c == KeyCode.RIGHT) {
                    populate = true;
                    offset = c == KeyCode.LEFT ? -1 : 1;
                } else if (c == KeyCode.BACK_SPACE) {
                    populate = true;
                    offset = -1;
                } else if (!c.isArrowKey() && !c.isKeypadKey()
                        && !c.isFunctionKey() && !c.isMediaKey() && !c.isModifierKey() && !c.isNavigationKey()) {
                    shouldOpenAutocompletionAfterEdit = true;
                }
                if (populate) {
                    if (autocompletionPopup.populate(offset, false)) {
                        autocompletionPopup.showPopup();
                    } else {
                        autocompletionPopup.hide();
                    }
                }
            } else {
                shouldOpenAutocompletionAfterEdit = !event.getText().isBlank();
                if (documentationPopup != null) documentationPopup.hide();
            }
        });

        // FOCUS, CLICK AND MOVE
        focusedProperty().addListener((ChangeListener<? super Boolean>) popupHideListener);
        addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<? super MouseEvent>) popupHideHandler);
        JamsApplication.getStage().xProperty().addListener((ChangeListener<? super Number>) popupHideListener);
        JamsApplication.getStage().yProperty().addListener((ChangeListener<? super Number>) popupHideListener);
        JamsApplication.getStage().widthProperty().addListener((ChangeListener<? super Number>) popupHideListener);
        JamsApplication.getStage().heightProperty().addListener((ChangeListener<? super Number>) popupHideListener);
    }

    protected void createContextMenu(double screenX, double screenY) {
        Set<ContextAction> set = getSupportedContextActions();
        if (set.isEmpty()) return;
        ContextMenu main = new ContextActionMenuBuilder(this).addAll(set).build();
        JamsApplication.openContextMenu(main, this, screenX, screenY);
    }

    protected Set<ContextAction> getSupportedContextActions() {
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

    protected void styleVisibleArea() {
        try {
            // The index is not initialized or is in edit mode. The indexer will warn
            // us later that the index is initialized or the edit mode has finished, asking for a refresh.
            if (index == null || !index.isInitialized() || index.isInEditMode()) return;

            int from = firstVisibleParToAllParIndex();
            int to = lastVisibleParToAllParIndex();

            index.withLockF(false, i -> i.getStyleRange(from, to))
                    .ifPresent(s -> setStyleSpans(from, 0, s));
        } catch (IllegalArgumentException ignore) {
            Platform.runLater(this::styleVisibleArea);
        }
    }

    @Override
    protected void layoutChildren() {
        try {
            var children = getChildren();
            if (!children.get(0).equals(lineBackground)) children.add(0, lineBackground);
            var index = visibleParToAllParIndex(0);
            var wd = getParagraphGraphic(index).prefWidth(-1);
            lineBackground.setWidth(wd);
        } catch (Exception ignore) {
        }
        super.layoutChildren();
    }

    @Listener
    private void onIndexRefresh(IndexRequestRefreshEvent event) {
        if (!tab.isSelected()) return;
        Platform.runLater(() -> {
            styleVisibleArea();
            if (shouldOpenAutocompletionAfterEdit) {
                shouldOpenAutocompletionAfterEdit = false;
                if (autocompletionPopup != null) {
                    if (autocompletionPopup.populate(0, false)) {
                        autocompletionPopup.showPopup();
                    } else {
                        autocompletionPopup.hide();
                    }
                }
            }
        });
    }

    @Listener
    private void onModificationFinish(IndexFinishEditEvent event) {
        Platform.runLater(() -> {
            modificationCount--;
            if (modificationCount < 0) {
                modificationCount = 0;
            }
        });
    }


    private void accept(LinkedList<EditorLineChange> list) {
        pendingChanges.addAll(list);
        getProject().getTaskExecutor().executeIndexing(this);
    }

    private class StyleAnimation extends AnimationTimer {

        private static final long DELAY = 100000000L;

        private long nextTick = System.nanoTime();
        private double from = -1, to = -1;

        @Override
        public void handle(long now) {
            if (!tab.isSelected()) return;
            if (now >= nextTick) {
                nextTick = now + DELAY;
            } else return;

            try {
                int from = firstVisibleParToAllParIndex();
                int to = lastVisibleParToAllParIndex();

                if (from == this.from && to == this.to) return;

                this.from = from;
                this.to = to;
            } catch (IllegalArgumentException ignore) {
                return;
            }
            styleVisibleArea();
        }
    }

}
