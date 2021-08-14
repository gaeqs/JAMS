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

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class KeyCombinationBuilder {

    private final KeyCode code;
    private final boolean shift, alt, meta, shortcut;

    public KeyCombinationBuilder(KeyCode code, boolean shift, boolean alt, boolean meta, boolean shortcut) {
        this.code = code;
        this.shift = shift;
        this.alt = alt;
        this.meta = meta;
        this.shortcut = shortcut;
    }

    public KeyCombinationBuilder(KeyEvent event) {
        this.code = event.getCode();
        this.shift = event.isShiftDown();
        this.alt = event.isAltDown();
        this.meta = event.isMetaDown();
        this.shortcut = event.isShortcutDown();
    }

    public KeyCode getCode() {
        return code;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isAlt() {
        return alt;
    }

    public boolean isMeta() {
        return meta;
    }

    public boolean isShortcut() {
        return shortcut;
    }

    public KeyCodeCombination build() {
        List<KeyCombination.Modifier> modifiers = new ArrayList<>();
        if (shift) modifiers.add(KeyCombination.SHIFT_DOWN);
        if (alt) modifiers.add(KeyCombination.ALT_DOWN);
        if (meta) modifiers.add(KeyCombination.META_DOWN);
        if (shortcut) modifiers.add(KeyCombination.SHORTCUT_DOWN);
        return new KeyCodeCombination(code, modifiers.toArray(new KeyCombination.Modifier[0]));
    }
}
