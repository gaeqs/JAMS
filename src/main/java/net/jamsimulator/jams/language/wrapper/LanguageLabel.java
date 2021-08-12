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

package net.jamsimulator.jams.language.wrapper;

import javafx.scene.control.Label;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.utils.StringUtils;

public class LanguageLabel extends Label {

    private String node;
    private String[] replacements;

    public LanguageLabel(String node, String... replacements) {
        this.node = node;
        this.replacements = replacements;
        Jams.getLanguageManager().registerListeners(this, true);
        refreshMessage();
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
        refreshMessage();
    }

    public void setReplacements(String[] replacements) {
        this.replacements = replacements;
        refreshMessage();
    }

    private void refreshMessage() {
        if (node == null) {
            setText(null);
            return;
        }
        var parsed = StringUtils.parseEscapeCharacters(Jams.getLanguageManager().getSelected().getOrDefault(node));

        for (int i = 0; i < replacements.length - 1; i += 2) {
            parsed = parsed.replace(replacements[i], replacements[i + 1]);
        }

        setText(StringUtils.addLineJumps(parsed, 70));
    }

    @Listener
    public void onSelectedLanguageChange(SelectedLanguageChangeEvent.After event) {
        refreshMessage();
    }

    @Listener
    public void onDefaultLanguageChange(DefaultLanguageChangeEvent.After event) {
        refreshMessage();
    }
}
