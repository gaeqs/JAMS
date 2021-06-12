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

package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledInstructionViewerElement;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledInstructionViewerOrder;

import java.util.Optional;

public class MIPSAssembledInstructionViewerOrderValueConverter extends ValueConverter<MIPSAssembledInstructionViewerOrder> {

    public static final String NAME = "assembled_instruction_viewer_order";

    @Override
    public String toString(MIPSAssembledInstructionViewerOrder value) {
        if (value == null) return null;
        var elements = value.getElements();
        var builder = new StringBuilder();
        for (MIPSAssembledInstructionViewerElement element : elements) {
            builder.append(element.name()).append(";");
        }

        return builder.length() == 0 ? "" : builder.substring(0, builder.length() - 1);
    }

    @Override
    public Optional<MIPSAssembledInstructionViewerOrder> fromStringSafe(String value) {
        var elements = new MIPSAssembledInstructionViewerOrder();
        var split = value.split(";");

        for (String element : split) {
            var optional =
                    MIPSAssembledInstructionViewerElement.getByName(element);
            optional.ifPresent(elements::addElement);
        }

        return Optional.of(elements);
    }

    @Override
    public Class<?> conversionClass() {
        return Action.class;
    }
}
