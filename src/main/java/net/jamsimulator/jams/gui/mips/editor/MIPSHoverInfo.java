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

import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexStyleableElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.InspectionLevel;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.Manager;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.Collections;
import java.util.List;

/**
 * Represents the content JAMS shows to the user when them hovers over an {@link EditorIndexedElement} in the editor.
 */
public class MIPSHoverInfo extends VirtualizedScrollPane<StyleClassedTextArea> {

    /**
     * Creates the hover info for the given {@link EditorIndexedElement element}.
     *
     * @param element the given {@link EditorIndexedElement element}.
     */
    public MIPSHoverInfo(EditorIndexedElement element) {
        super(new StyleClassedTextArea());

        element.getIndex().withLock(false, i -> {
            addHeader(element);
            addErrors(element);
            addWarnings(element);
            addFooter(element);
        });

        getContent().setWrapText(true);
        getContent().setEditable(false);

        getContent().getStyleClass().addAll("documentation", "code-area", "hover-popup");

        setPrefWidth(400);
        setMaxHeight(600);

        getContent().totalHeightEstimateProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        getContent().totalHeightEstimateProperty().addListener((obs, old, val) -> setPrefHeight(val + 10));
    }

    private void addHeader(EditorIndexedElement element) {
        getContent().appendText(element.getTranslatedTypeName() + " ");
        getContent().append(element.getIdentifier(), element instanceof EditorIndexStyleableElement s
                ? s.getStyles() : Collections.emptyList());
    }

    private void addWarnings(EditorIndexedElement element) {
        var inspections = element.getMetadata().inspections();
        var warnings = inspections.stream()
                .filter(it -> it.level().ordinal() < InspectionLevel.ERROR.ordinal()).toList();
        if (!warnings.isEmpty()) {
            getContent().append("\n\n" + Manager.ofS(Language.class).getSelected()
                    .getOrDefault(Messages.MIPS_ELEMENT_WARNINGS), List.of("warning"));
            warnings.forEach(inspection -> getContent().append("\n- " + inspection.buildMessage(),
                    Collections.emptyList()));
        }
    }

    private void addErrors(EditorIndexedElement element) {
        var inspections = element.getMetadata().inspections();
        var warnings = inspections.stream()
                .filter(it -> it.level().ordinal() >= InspectionLevel.ERROR.ordinal()).toList();
        if (!warnings.isEmpty()) {
            getContent().append("\n\n" + Manager.ofS(Language.class).getSelected()
                    .getOrDefault(Messages.MIPS_ELEMENT_ERRORS), List.of("error"));
            warnings.forEach(inspection -> getContent().append("\n- " + inspection.buildMessage(),
                    Collections.emptyList()));
        }
    }

    private void addFooter(EditorIndexedElement element) {
        var scope = element.getReferencedScope();
        getContent().appendText("\n\n");
        getContent().append(scope.getFullName(), Collections.emptyList());
    }


}
