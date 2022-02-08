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

package net.jamsimulator.jams.language.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.utils.Validate;

/**
 * Event called when the selected or default language has changed and requires a refresh.
 */
public class LanguageRefreshEvent extends Event {

    private final Language selectedLanguage, defaultLanguage;

    public LanguageRefreshEvent(Language selectedLanguage, Language defaultLanguage) {
        Validate.notNull(selectedLanguage, "Selected language cannot be null!");
        Validate.notNull(defaultLanguage, "Default language cannot be null!");
        this.selectedLanguage = selectedLanguage;
        this.defaultLanguage = defaultLanguage;
    }

    public Language getSelectedLanguage() {
        return selectedLanguage;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }
}
