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

package net.jamsimulator.jams.gui.util;

import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;

import java.util.Arrays;
import java.util.Collection;

public abstract class LanguageStringComboBox extends ComboBox<String> {

    private final String[] nodes;

    public LanguageStringComboBox(String... nodes) {
        this(Arrays.asList(nodes));
    }

    public LanguageStringComboBox(Collection<String> nodes) {
        this.nodes = new String[nodes.size()];

        int i = 0;
        for (String node : nodes) {
            this.nodes[i] = node;
            getItems().add(Jams.getLanguageManager().getSelected().getOrDefault(node));
        }

        setOnAction(event -> onSelect(getSelectionModel().getSelectedIndex(), getSelectionModel().getSelectedItem()));
        Jams.getLanguageManager().registerListeners(this, true);
    }


    public abstract void onSelect(int index, String node);

    @Listener
    private void onLanguageChange(DefaultLanguageChangeEvent.After event) {
        int selected = getSelectionModel().getSelectedIndex();
        for (int i = 0; i < nodes.length; i++) {
            getItems().set(i, Jams.getLanguageManager().getSelected().getOrDefault(nodes[i]));
        }
        getSelectionModel().select(selected);
    }

    @Listener
    private void onLanguageChange(SelectedLanguageChangeEvent.After event) {
        int selected = getSelectionModel().getSelectedIndex();
        for (int i = 0; i < nodes.length; i++) {
            getItems().set(i, Jams.getLanguageManager().getSelected().getOrDefault(nodes[i]));
        }
        getSelectionModel().select(selected);
    }
}
