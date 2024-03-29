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

package net.jamsimulator.jams.mips.assembler.builder;

import net.jamsimulator.jams.gui.util.log.Log;
import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.utils.RawFileData;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

/**
 * Represents an assembler builder. Assembler builders are used to create several {@link Assembler}
 * using the given parameters.
 * <p>
 * If a plugin want to add a custom MIPS32 assembler to JAMS, it should create a child of this class and register
 * it on the {@link AssemblerBuilderManager}.
 */
public abstract class AssemblerBuilder implements ManagerResource {

    private final ResourceProvider resourceProvider;
    private final String name;

    /**
     * Creates an assembler builder using a name.
     * This name must be unique for each assembler builder.
     *
     * @param name the name.
     */
    public AssemblerBuilder(ResourceProvider resourceProvider, String name) {
        Validate.notNull(resourceProvider, "ResourceProvider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        this.resourceProvider = resourceProvider;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    /**
     * Creates an {@link Assembler} using a {@link DirectiveSet}, an {@link InstructionSet}, a {@link Registers}
     * and a {@link Memory}.
     *
     * @param rawFiles       the raw text of all files to assemble.
     * @param directiveSet   the directive set.
     * @param instructionSet the instruction set.
     * @param registerSet    the register set.
     * @param memory         the memory.
     * @param log            the log used by the assembler to inform about errors and warnings.
     * @return the new {@link Assembler}.
     */
    public abstract Assembler createAssembler(Iterable<RawFileData> rawFiles, DirectiveSet directiveSet,
                                              InstructionSet instructionSet, Registers registerSet,
                                              Memory memory, Log log);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssemblerBuilder that = (AssemblerBuilder) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
