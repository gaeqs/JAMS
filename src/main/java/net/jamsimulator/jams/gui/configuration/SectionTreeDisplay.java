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

package net.jamsimulator.jams.gui.configuration;

import javafx.scene.control.Label;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerDefaultElementChangeEvent;
import net.jamsimulator.jams.manager.event.ManagerSelectedElementChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class SectionTreeDisplay extends Label {

    public static final String STYLE_CLASS = "tree-display";

    private ExplorerSection current;

    public SectionTreeDisplay() {
        super("");
        getStyleClass().add(STYLE_CLASS);
        Manager.of(Language.class).registerListeners(this, true);
    }

    public void setSection(ExplorerSection section) {
        current = section;
        List<String> sections = new ArrayList<>();

        while (section != null) {
            sections.add(section.getVisibleName());
            section = section.getParentSection().orElse(null);
        }

        StringBuilder builder = new StringBuilder();
        for (int i = sections.size() - 1; i >= 0; i--) {
            builder.append(sections.get(i)).append(" > ");
        }

        if (builder.length() == 0) return;
        setText(builder.substring(0, builder.length() - 3));
    }

    @Listener(priority = -1)
    private void onLanguageChange(ManagerSelectedElementChangeEvent.After<Language> event) {
        setSection(current);
    }

    private void onLanguageChange(ManagerDefaultElementChangeEvent.After<Language> event) {
        setSection(current);
    }

}
