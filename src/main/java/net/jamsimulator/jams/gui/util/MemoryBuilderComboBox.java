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
import javafx.util.StringConverter;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.memory.builder.event.MemoryBuilderRegisterEvent;
import net.jamsimulator.jams.mips.memory.builder.event.MemoryBuilderUnregisterEvent;

public class MemoryBuilderComboBox extends ComboBox<MemoryBuilder> {

    public MemoryBuilderComboBox(MemoryBuilder selected) {
        getItems().addAll(Jams.getMemoryBuilderManager());
        getSelectionModel().select(selected);

        setConverter(new StringConverter<>() {
            @Override
            public String toString(MemoryBuilder object) {
                return object.getName();
            }

            @Override
            public MemoryBuilder fromString(String string) {
                return Jams.getMemoryBuilderManager().get(string)
                        .orElse(Jams.getMemoryBuilderManager().getDefault());
            }
        });
        Jams.getMemoryBuilderManager().registerListeners(this, true);
    }

    @Listener
    private void onRegister(MemoryBuilderRegisterEvent.After event) {
        getItems().add(event.getMemoryBuilder());
    }

    @Listener
    private void onUnregister(MemoryBuilderUnregisterEvent.After event) {
        if (event.getMemoryBuilder().equals(getSelectionModel().getSelectedItem())) {
            getSelectionModel().select(Jams.getMemoryBuilderManager().getDefault());
        }
        getItems().remove(event.getMemoryBuilder());
    }


}
