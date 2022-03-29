/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.gui.editor.code.autocompletion;

import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.utils.CollectionUtils;
import net.jamsimulator.jams.utils.StringSearch;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a controller for an {@link AutocompletionPopup}.
 * <p>
 * A controller provides candidates for the {@link AutocompletionPopup}.
 */
public abstract class AutocompletionPopupController {

    protected Set<AutocompletionCandidate<?>> candidates = new HashSet<>();

    /**
     * Returns for candidates that match the given search.
     * <p>
     * This method invokes {@link #isCandidateValidForContext(EditorIndexedElement, AutocompletionCandidate)}
     * and it should be invoked before {@link #refreshCandidates(EditorIndexedElement, int)}.
     *
     * @param context the search context.
     * @param search  the match to search.
     * @return the found candidates.
     */
    public List<AutocompletionOption<?>> searchOptions(EditorIndexedElement context, String search) {
        var validCandidates = candidates.stream()
                .filter(it -> isCandidateValidForContext(context, it))
                .toList();

        var keys = validCandidates.stream().map(AutocompletionCandidate::key).toList();
        var result = StringSearch.search(search, keys);

        return CollectionUtils.zip(validCandidates, result)
                .filter(pair -> pair.getValue().found())
                .map(pair -> new AutocompletionOption<>(pair.getKey(), pair.getValue()))
                .sorted(Comparator.comparingInt(o -> o.searchResult().priority()))
                .collect(Collectors.toList());
    }


    /**
     * Returns whether the given {@link AutocompletionCandidate} is valid for the current context.
     * <p>
     * No valid candidates won't be shown in the autocompletion popup.
     *
     * @param context   the context.
     * @param candidate the {@link AutocompletionCandidate candidate}.
     * @return whether the given {@link AutocompletionCandidate} is valid for the current contetx.
     */
    public abstract boolean isCandidateValidForContext(EditorIndexedElement context,
                                                       AutocompletionCandidate<?> candidate);

    /**
     * Refresh the candidates.
     *
     * @param context    the current context of the {@link AutocompletionPopup}.
     * @param caretStart the start position of the caret in the editor.
     */
    public abstract void refreshCandidates(EditorIndexedElement context, int caretStart);

}
