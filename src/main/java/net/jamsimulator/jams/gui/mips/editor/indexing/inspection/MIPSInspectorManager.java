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

package net.jamsimulator.jams.gui.mips.editor.indexing.inspection;

import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.defaults.*;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;

public class MIPSInspectorManager extends Manager<Inspector> {

    public static final String NAME = "mips_inspector";
    public static final MIPSInspectorManager INSTANCE = new MIPSInspectorManager(ResourceProvider.JAMS, NAME);

    public MIPSInspectorManager(ResourceProvider provider, String name) {
        super(provider, name, Inspector.class, true);
    }

    @Override
    protected void loadDefaultElements() {
        add(new DuplicatedLabelInspector(ResourceProvider.JAMS));
        add(new DuplicatedMacroInspector(ResourceProvider.JAMS));
        add(new MacroNotFoundInspector(ResourceProvider.JAMS));
        add(new IllegalMacroParameterInspector(ResourceProvider.JAMS));
        add(new MIPSIllegalLabelInspector(ResourceProvider.JAMS));
        add(new MIPSInstructionNotFoundInspector(ResourceProvider.JAMS));
        add(new MIPSInstructionLabelNotFoundInspector(ResourceProvider.JAMS));
        add(new MIPSDirectiveNotFoundInspector(ResourceProvider.JAMS));
        add(new MIPSEqvUseInspector(ResourceProvider.JAMS));
        add(new MIPSRegisterAtUseInspector(ResourceProvider.JAMS));
    }
}
