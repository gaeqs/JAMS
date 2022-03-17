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

package net.jamsimulator.jams.gui.editor.code.indexing;

import javafx.application.Platform;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;

/**
 * Small thread implementation used to send changes to a {@link EditorIndex} in an asynchronous way.
 */
public class IndexingThread extends Thread {

    private final CodeFileEditor editor;
    private volatile boolean running;

    public IndexingThread(CodeFileEditor editor, String fileName) {
        this.editor = editor;
        this.running = true;
        setName(fileName +" 's indexing thread");
    }

    /**
     * Stops the thread.
     */
    public void kill() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        if (editor.getIndex() == null || !waitForInitialization()) return;
        while (running) {
            try {
                if (!waitForElements()) return;
                editor.getIndex().withLock(true, i -> {
                    editor.getPendingChanges().flushAll(i::change);

                    if (editor.getPendingChanges().isMarkedForReformat(true)) {
                        var text = editor.getIndex().reformat();
                        flushReformattedCode(text);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void flushReformattedCode(String text) {
        Platform.runLater(() -> editor.getIndex().withLock(false, i -> {
            editor.replaceText(text);
            editor.setEditable(!editor.getPendingChanges().isMarkedForReformat(false));
        }));
    }


    private boolean waitForInitialization() {
        try {
            editor.getIndex().waitForInitialization();
            return true;
        } catch (InterruptedException e) {
            running = false;
            return false;
        }

    }

    private boolean waitForElements() {
        try {
            editor.getPendingChanges().waitForElements();
            return true;
        } catch (InterruptedException e) {
            running = false;
            return false;
        }

    }
}
