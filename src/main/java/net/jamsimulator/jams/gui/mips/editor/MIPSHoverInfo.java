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

package net.jamsimulator.jams.gui.mips.editor;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstruction;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.Collections;
import java.util.List;

/**
 * Represents the content JAMS shows to the user when them hovers over an {@link MIPSCodeElement} in the editor.
 */
public class MIPSHoverInfo extends VirtualizedScrollPane<StyleClassedTextArea> {

    /**
     * Creates the hover info for the given {@link MIPSCodeElement element}.
     *
     * @param element the given {@link MIPSCodeElement element}.
     */
    public MIPSHoverInfo(MIPSCodeElement element) {
        super(new StyleClassedTextArea());

        addGeneralInfo(element);
        addWarnings(element);
        addErrors(element);

        getContent().setWrapText(true);
        getContent().setEditable(false);
        getContent().getStyleClass().add("documentation");

        setPrefWidth(400);
        setPrefHeight(200);
    }

    private void addGeneralInfo(MIPSCodeElement element) {
        //{TYPE} {TEXT} ({NAME})
        getContent().append(element.getTranslatedName() + " ", List.of("bold"));
        getContent().append(element.getSimpleText().trim(), Collections.emptyList());

        //If the element is an instruction show the name too.
        if (element instanceof MIPSInstruction && ((MIPSInstruction) element).getMostCompatibleInstruction().isPresent()) {
            getContent().append(" (" + ((MIPSInstruction) element).getMostCompatibleInstruction().get().getName() + ")\n", Collections.emptyList());
        } else getContent().append("\n", Collections.emptyList());

    }

    private void addWarnings(MIPSCodeElement element) {
        if (element.hasWarnings()) {
            getContent().append("\n" + Jams.getLanguageManager().getSelected().getOrDefault("MIPS_ELEMENT_WARNINGS"), List.of("bold"));

            element.forEachInspection(inspection -> {
                if (!inspection.getBuilder().isError()) {
                    getContent().append("\n- " + inspection.getParsedDescription(), Collections.emptyList());
                }
            });
        }
    }

    private void addErrors(MIPSCodeElement element) {
        if (element.hasErrors()) {

            getContent().append("\n" + Jams.getLanguageManager().getSelected().getOrDefault("MIPS_ELEMENT_ERRORS"), List.of("bold"));

            element.forEachInspection(inspection -> {
                if (inspection.getBuilder().isError()) {
                    getContent().append("\n- " + inspection.getParsedDescription(), Collections.emptyList());
                }
            });
        }
    }


}
