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

package net.jamsimulator.jams.mips.directive.defaults;

import net.jamsimulator.jams.mips.assembler.MIPS32AssemblerLine;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.utils.LabelUtils;

import java.util.Map;

public class DirectiveLab extends Directive {

    public static final String NAME = "lab";
    private static final DirectiveParameterType[] PARAMETERS = {DirectiveParameterType.LABEL};

    public DirectiveLab() {
        super(NAME, PARAMETERS, false, false, false);
    }

    @Override
    public void onDiscovery(MIPS32AssemblerLine line, String[] parameters, String rawParameters, Map<String, String> equivalents) {
        if (parameters.length != 1)
            throw new AssemblerException(line.getIndex(), "." + NAME + " must have one parameter.");

        if (!LabelUtils.isLabelDeclarationLegal(parameters[0]))
            throw new AssemblerException("Label " + parameters[0] + " is not legal.");

        var label = new Label(parameters[0], line.getScope(), 0, line.getFile().getName(), line.getIndex());
        line.getLabels().add(label);
    }

}
