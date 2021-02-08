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
