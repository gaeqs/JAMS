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

import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.model.TwoDimensional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a change in a {@link net.jamsimulator.jams.gui.editor.code.CodeFileEditor CodeFileEditor}.
 */
public record EditorLineChange(Type type, int line, String text) {

    /**
     * Returns a new {@link List} with the {@link EditorLineChange changes} made by the
     * given {@link PlainTextChange}.
     * <p>
     * The given list is a new mutable list.
     *
     * @param change the change.
     * @param area   the {@link CodeArea} where the change has occurred.
     * @return the list with the {@link EditorLineChange}.
     */
    public static List<EditorLineChange> of(PlainTextChange change, CodeArea area) {
        return of(change, area, new ArrayList<>());
    }

    /**
     * Calculates the {@link EditorLineChange changes} made by the given {@link PlainTextChange} and
     * adds them into the given {@link Collection}.
     *
     * @param change     the change.
     * @param area       the {@link CodeArea} where the change has occurred.
     * @param collection the collection where the changes will be added.
     * @param <T>        the type of the collection.
     * @return the given collection.
     */
    public static <T extends Collection<? super EditorLineChange>> T of(PlainTextChange change, CodeArea area, T collection) {
        var added = change.getInserted();
        var removed = change.getRemoved();

        var position = area.offsetToPosition(change.getPosition(), TwoDimensional.Bias.Forward);
        var line = position.getMajor();

        collection.add(new EditorLineChange(Type.EDIT, line, area.getParagraph(line).getText()));

        int addedLines = StringUtils.charCount(added, '\n', '\r');
        int removedLines = StringUtils.charCount(removed, '\n', '\r');
        if (addedLines == 0 && removedLines == 0) return collection;

        line++;

        int editedLines = Math.min(addedLines, removedLines);
        int linesToAdd = Math.max(0, addedLines - removedLines);
        int linesToRemove = Math.max(0, removedLines - addedLines);

        for (int i = 0; i < editedLines; i++) {
            collection.add(new EditorLineChange(Type.EDIT, line + i, area.getParagraph(line + i).getText()));
        }

        for (int i = 0; i < linesToRemove; i++) {
            collection.add(new EditorLineChange(Type.REMOVE, line + editedLines, null));
        }
        for (int i = 0; i < linesToAdd; i++) {
            int n = line + i + editedLines;
            collection.add(new EditorLineChange(Type.ADD, n, area.getParagraph(n).getText()));
        }
        return collection;
    }

    /**
     * Represents the type of the change. It may be an addition, a removal or an edit.
     */
    public enum Type {

        EDIT,
        REMOVE,
        ADD

    }


}
