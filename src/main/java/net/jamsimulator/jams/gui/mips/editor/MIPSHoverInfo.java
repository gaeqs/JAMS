package net.jamsimulator.jams.gui.mips.editor;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSCodeElement;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.Collections;
import java.util.List;

public class MIPSHoverInfo extends VirtualizedScrollPane<StyleClassedTextArea> {

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
        getContent().append(element.getTranslatedName() + " " , List.of("bold"));
        getContent().append(element.getSimpleText().trim() + "\n", Collections.emptyList());
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
