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

import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.code.popup.DocumentationPopup;

public class MIPSDocumentationPopup extends DocumentationPopup {
    /**
     * Creates the documentation popup.
     *
     * @param display the code display where this popup is displayed.
     */
    public MIPSDocumentationPopup(CodeFileEditor display) {
        super(display);
    }

    //private final MIPSAutocompletionPopup autocompletionPopup;

   ///**
   // * Creates the documentation popup.
   // *
   // * @param display the code display where this popup is displayed.
   // */
   //public MIPSDocumentationPopup(CodeFileEditor display, MIPSAutocompletionPopup autocompletionPopup) {
   //    super(display);
   //    this.autocompletionPopup = autocompletionPopup;
   //    autocompletionPopup.registerListeners(this, true);
   //    content.focusedProperty().addListener((obs, old, val) -> {
   //        if (val) {
   //            autocompletionPopup.requestFocus();
   //        }
   //    });

   //    scroll.focusedProperty().addListener((obs, old, val) -> {
   //        if (val) {
   //            autocompletionPopup.requestFocus();
   //        }
   //    });
   //}

   //@Listener
   //private void onSelect(AutocompletionPopupSelectElementEvent event) {
   //    var element = event.getSelectedElement().getElement();
   //    if (element instanceof Instruction) {
   //        topMessage.setMaxHeight(0);
   //        StringStyler.style(((Instruction) element).getDocumentation(), content);
   //    } else if (element instanceof Directive) {
   //        StringStyler.style(((Directive) element).getDocumentation(), content);
   //    }
   //}

   //@Override
   //public void execute(int caretOffset) {
   //    if (refreshData(caretOffset)) {
   //        super.execute(caretOffset);
   //    }
   //}

   //public boolean refreshData(int caretOffset) {
   //    if (autocompletionPopup.isShowing()) {
   //        var optional = autocompletionPopup.getSelected();
   //        if (optional.isEmpty()) return false;
   //        if (optional.get().getElement() instanceof Instruction) {
   //            topMessage.setMaxHeight(0);
   //            StringStyler.style(((Instruction) optional.get().getElement()).getDocumentation(), content);
   //            return true;
   //        } else if (optional.get().getElement() instanceof Directive) {
   //            topMessage.setMaxHeight(0);
   //            StringStyler.style(((Directive) optional.get().getElement()).getDocumentation(), content);
   //            return true;
   //        }
   //    } else if (display instanceof MIPSFileEditor) {
   //        var index = display.getIndex();
   //        var optional = index.withLockF(false,
   //                        i -> i.getElementAt(display.getCaretPosition() + caretOffset - 1));

   //        if (optional.isEmpty()) return false;

   //        var element = optional.get();
   //        if (element instanceof MIPSEditorInstructionMnemonic) {
   //            var parent = element.getParentOfType(MIPSEditorInstruction.class).orElse(null);
   //            if(parent == null) return false;

   //            topMessage.clear();


   //            element.getMetadata().inspections().forEach(inspection -> {
   //                topMessage.append(inspection.buildMessage() + "\n", "");
   //            });

   //            topMessage.setMaxHeight(topMessage.getLength() > 0 ? 50 : 0);

   //            var instruction = parent.getInstruction();
   //            if (instruction.isEmpty()) {
   //                content.clear();
   //                return topMessage.getMaxHeight() > 0;
   //            }

   //            StringStyler.style(instruction.get().getDocumentation(), content);
   //            return true;
   //        } else if (element instanceof MIPSEditorDirectiveMnemonic) {
   //            var parent = element.getParentOfType(MIPSEditorDirective.class).orElse(null);
   //            if(parent == null) return false;

   //            topMessage.clear();

   //            element.getMetadata().inspections().forEach(inspection ->
   //                    topMessage.append(inspection.buildMessage() + "\n", ""));

   //            topMessage.setMaxHeight(topMessage.getLength() > 0 ? 50 : 0);

   //            var directive = parent.getDirective();
   //            if (directive.isEmpty()) {
   //                content.clear();
   //                return topMessage.getMaxHeight() > 0;
   //            }

   //            StringStyler.style(directive.get().getDocumentation(), content);
   //            return true;
   //        }
   //    }
   //    return false;
   //}
}
