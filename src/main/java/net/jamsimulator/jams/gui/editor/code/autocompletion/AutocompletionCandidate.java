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

import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.utils.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Represents an element that may be represented in a {@link AutocompletionPopup}.
 *
 * @param element        the represented element.
 * @param key            the key of the element.
 * @param replacement    the text that will override the query.
 * @param displayStrings a list with extra names of the candidates.
 * @param icon           the icon of the candidate.
 * @param <T>            the type of the element.
 */
public record AutocompletionCandidate<T>(
        T element,
        String key,
        String replacement,
        List<String> displayStrings,
        @Nullable IconData icon
) {

    public AutocompletionCandidate {
        Validate.notNull(element, "Element cannot be null!");
        Validate.notNull(key, "Key cannot be null!");
        Validate.notNull(replacement, "Replacement cannot be null!");
        Validate.notNull(displayStrings, "Display strings cannot be null!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutocompletionCandidate<?> that = (AutocompletionCandidate<?>) o;
        return element.equals(that.element) && key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, key);
    }
}
