package net.jamsimulator.jams.gui.mips.editor;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.popup.DocumentationPopup;
import net.jamsimulator.jams.gui.editor.popup.event.AutocompletionPopupSelectElementEvent;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSDirective;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSInstruction;
import net.jamsimulator.jams.gui.util.StringStyler;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.instruction.Instruction;

public class MIPSDocumentationPopup extends DocumentationPopup {

    private final MIPSAutocompletionPopup autocompletionPopup;

    /**
     * Creates the documentation popup.
     *
     * @param display the code display where this popup is displayed.
     */
    public MIPSDocumentationPopup(CodeFileEditor display, MIPSAutocompletionPopup autocompletionPopup) {
        super(display);
        this.autocompletionPopup = autocompletionPopup;
        autocompletionPopup.registerListeners(this, true);
        content.focusedProperty().addListener((obs, old, val) -> {
            if (val) {
                autocompletionPopup.requestFocus();
            }
        });

        scroll.focusedProperty().addListener((obs, old, val) -> {
            if (val) {
                autocompletionPopup.requestFocus();
            }
        });
    }

    @Listener
    private void onSelect(AutocompletionPopupSelectElementEvent event) {
        var element = event.getSelectedElement().getElement();
        if (element instanceof Instruction) {
            topMessage.setMaxHeight(0);
            StringStyler.style(((Instruction) element).getDocumentation(), content);
        } else if(element instanceof Directive) {
            StringStyler.style(((Directive) element).getDocumentation(), content);
        }
    }

    @Override
    public void execute(int caretOffset) {
        if (refreshData(caretOffset)) {
            super.execute(caretOffset);
        }
    }

    public boolean refreshData(int caretOffset) {
        if (autocompletionPopup.isShowing()) {
            var optional = autocompletionPopup.getSelected();
            if (optional.isEmpty()) return false;
            if (optional.get().getElement() instanceof Instruction) {
                topMessage.setMaxHeight(0);
                StringStyler.style(((Instruction) optional.get().getElement()).getDocumentation(), content);
                return true;
            } else if (optional.get().getElement() instanceof Directive) {
                topMessage.setMaxHeight(0);
                StringStyler.style(((Directive) optional.get().getElement()).getDocumentation(), content);
                return true;
            }
        } else if (display instanceof MIPSFileEditor) {

            var optional = ((MIPSFileEditor) display).getElements()
                    .getElementAt(display.getCaretPosition() + caretOffset);

            if (optional.isEmpty()) return false;

            var element = optional.get();
            if (element instanceof MIPSInstruction) {

                topMessage.clear();
                element.populatePopupWithInspections(topMessage);
                topMessage.setMaxHeight(topMessage.getLength() > 0 ? 50 : 0);

                var instructions = ((MIPSInstruction) element).getMostCompatibleInstruction();
                if (instructions.isEmpty()) {
                    content.clear();
                    return topMessage.getMaxHeight() > 0;
                }

                var instruction = instructions.stream().findFirst().get();
                StringStyler.style(instruction.getDocumentation(), content);
                return true;
            } else if (element instanceof MIPSDirective) {
                topMessage.clear();
                element.populatePopupWithInspections(topMessage);
                topMessage.setMaxHeight(topMessage.getLength() > 0 ? 50 : 0);

                var directive = ((MIPSDirective) element).getDirective();
                if (directive.isEmpty()) {
                    content.clear();
                    return topMessage.getMaxHeight() > 0;
                }

                StringStyler.style(directive.get().getDocumentation(), content);
                return true;
            }
        }
        return false;
    }
}
