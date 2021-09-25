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
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMenuBuilder;
import net.jamsimulator.jams.gui.editor.FileEditor;
import net.jamsimulator.jams.gui.editor.code.hint.EditorHintBar;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorLineChange;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorPendingChanges;
import net.jamsimulator.jams.gui.editor.code.indexing.IndexingThread;
import net.jamsimulator.jams.gui.editor.code.indexing.event.IndexRequestRefreshEvent;
import net.jamsimulator.jams.gui.editor.code.popup.AutocompletionPopup;
import net.jamsimulator.jams.gui.editor.code.popup.DocumentationPopup;
import net.jamsimulator.jams.gui.editor.code.top.CodeFileEditorReplace;
import net.jamsimulator.jams.gui.editor.code.top.CodeFileEditorSearch;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.GUIReflectionUtils;
import net.jamsimulator.jams.gui.util.ZoomUtils;
import net.jamsimulator.jams.project.GlobalIndexHolder;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.utils.FileUtils;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.reactfx.Subscription;

import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static net.jamsimulator.jams.gui.util.CodeFileEditorUtils.read;

public abstract class CodeFileEditor extends CodeArea implements FileEditor {

    protected final FileEditorTab tab;
    protected final ScaledVirtualized<CodeFileEditor> zoom = new ScaledVirtualized<>(this);
    protected final VirtualizedScrollPane<ScaledVirtualized<CodeFileEditor>> scrollPane =
            new VirtualizedScrollPane<>(zoom);

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

    protected final IndexingThread indexingThread;
    protected final Subscription subscription;
    protected final AnimationTimer styleTimer;

    protected boolean shouldOpenAutocompletionAfterEdit = false;

    public CodeFileEditor(FileEditorTab tab) {
        super(read(tab));
        this.tab = tab;
        this.index = getOrGenerateIndex();

        index.registerListeners(this, true);
        index.withLock(false, i -> i.setHintBar(hintBar));

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

        indexingThread = new IndexingThread(this);
        indexingThread.setDaemon(true);
        indexingThread.start();

        subscription = plainTextChanges()
                .reduceSuccessions(it -> {
                    var list = new LinkedList<EditorLineChange>();
                    EditorLineChange.of(it, this, list);
                    return list;
                }, (list, it) -> {
                    EditorLineChange.of(it, this, list);
                    return list;
                }, Duration.ofMillis(100))
                .subscribe(list -> pendingChanges.addAll(list));

        styleTimer = new StyleAnimation();
        styleTimer.start();

        tab.selectedProperty().addListener((obs, old, val) -> {
            if (val) styleVisibleArea();
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

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.TEXT_EDITOR.equals(region) ||
                RegionTags.EDITOR.equals(region) ||
                RegionTags.EDITOR_TAB.equals(region);
    }

    @Override
    public void onClose() {
        JamsApplication.getStage().xProperty().removeListener((ChangeListener<? super Number>) popupHideListener);
        JamsApplication.getStage().yProperty().removeListener((ChangeListener<? super Number>) popupHideListener);
        JamsApplication.getStage().widthProperty().removeListener((ChangeListener<? super Number>) popupHideListener);
        JamsApplication.getStage().heightProperty().removeListener((ChangeListener<? super Number>) popupHideListener);
        subscription.unsubscribe();
        indexingThread.kill();
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
        replaceText(0, getText().length(), read(tab));
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
            if (index.isPresent()) return index.get();
        }

        return generateIndex();
    }

    protected void initializeAutocompletionPopupListeners() {
        //AUTO COMPLETION
        //addEventHandler(KeyEvent.KEY_TYPED, event -> {
        //    if (autocompletionPopup != null) {
        //        if (autocompletionPopup.manageTypeEvent(event)) {
        //            event.consume();
        //        } else {
        //            System.out.println(event.getCode());
        //        }
        //    }
        //    if (documentationPopup != null) documentationPopup.hide();
        //});

        //AUTOCOMPLETION MOVEMENT
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (autocompletionPopup != null) {
                if (autocompletionPopup.managePressEvent(event)) {
                    event.consume();
                    if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                        if (documentationPopup != null) documentationPopup.hide();
                    }
                } else {
                    shouldOpenAutocompletionAfterEdit =
                            event.getCode().isLetterKey() || event.getCode().isDigitKey();

                    if (documentationPopup != null) documentationPopup.hide();
                }
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
            if (!index.isInitialized() || index.isInEditMode()) return;

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
        } catch (Exception ex) {
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
                    autocompletionPopup.execute(0, false);
                }
            }
        });
    }

    private class StyleAnimation extends AnimationTimer {

        private static final long DELAY = 100000000L;

        private long nextTick = System.nanoTime();
        private double zoomY = -1, zoomX = -1, scrollY = -1, scrollX = -1;

        @Override
        public void handle(long now) {
            if (!tab.isSelected()) return;
            if (now >= nextTick) {
                nextTick = now + DELAY;
            } else return;

            try {
                double scrollX = scrollPane.getEstimatedScrollX();
                double scrollY = scrollPane.getEstimatedScrollY();
                double zoomX = zoom.getZoom().getX();
                double zoomY = zoom.getZoom().getY();

                if (this.scrollX == scrollX && this.scrollY == scrollY && this.zoomX == zoomX && this.zoomY == zoomY)
                    return;

                this.scrollX = scrollX;
                this.scrollY = scrollY;
                this.zoomX = zoomX;
                this.zoomY = zoomY;
            } catch (IllegalArgumentException ignore) {
                return;
            }
            styleVisibleArea();
        }
    }

}
