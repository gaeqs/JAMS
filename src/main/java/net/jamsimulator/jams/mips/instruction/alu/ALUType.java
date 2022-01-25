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

package net.jamsimulator.jams.mips.instruction.alu;

import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.utils.Validate;

public record ALUType(
        ResourceProvider provider,
        String name,
        String languageNode,
        int defaultCyclesPerExecution
) implements ManagerResource {

    public static final ALUType INTEGER = new ALUType(ResourceProvider.JAMS, "integer",
            "ALU_TYPE_INTEGER", 1);
    public static final ALUType FLOAT_ADDTION = new ALUType(ResourceProvider.JAMS, "float_addition",
            "ALU_TYPE_FLOAT_ADDITION", 4);
    public static final ALUType FLOAT_MULTIPLICATION = new ALUType(ResourceProvider.JAMS, "float_multiplication",
            "ALU_TYPE_FLOAT_MULTIPLICATION", 9);
    public static final ALUType FLOAT_DIVISION = new ALUType(ResourceProvider.JAMS, "float_division",
            "ALU_TYPE_FLOAT_DIVISION", 17);

    public ALUType {
        Validate.notNull(provider, "Provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return provider;
    }

    @Override
    public String getName() {
        return name;
    }

}
