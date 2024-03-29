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

package net.jamsimulator.jams.mips.architecture;

import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationData;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

/**
 * Architectures tell JAMS how instructions are executed.
 * <p>
 * Each architecture is made by different elements and can run instructions
 * in a completely different way.
 */
public abstract class Architecture implements ManagerResource {

    protected final ResourceProvider provider;
    protected final String name;

    public Architecture(ResourceProvider provider, String name) {
        Validate.notNull(provider, "Provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        this.provider = provider;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return provider;
    }

    /**
     * Creates a simulation of this architecture using the given parameters.
     *
     * @param data the construction data for the simulation.
     * @return the {@link MIPSSimulation}.
     */
    public abstract MIPSSimulation<? extends Architecture> createSimulation(MIPSSimulationData data);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Architecture that = (Architecture) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
