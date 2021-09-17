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

package net.jamsimulator.jams.gui.editor.code.indexing.inspection;

import java.util.Optional;

public enum InspectionLevel {

    NONE(null, null),
    INFO("hint-bar-info", null),
    LOW_WARNING("hint-bar-warning", "warning"),
    WARNING("hint-bar-warning", "warning"),
    ERROR("hint-bar-error", "error"),
    BIG_ERROR("hint-bar-error", "error");

    private final String hintStyle, elementStyle;

    InspectionLevel(String hintStyle, String elementStyle) {
        this.hintStyle = hintStyle;
        this.elementStyle = elementStyle;
    }

    public Optional<String> getHintStyle() {
        return Optional.ofNullable(hintStyle);
    }

    public Optional<String> getElementStyle() {
        return Optional.ofNullable(elementStyle);
    }
}
