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

package net.jamsimulator.jams.mips.instruction.data;

import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.utils.Validate;

public record APUType(
        ResourceProvider provider,
        String name,
        int defaultCyclesPerExecution
) implements ManagerResource {

    public static final APUType INTEGER = new APUType(ResourceProvider.JAMS, "integer", 1);
    public static final APUType FLOAT_ADDTION = new APUType(ResourceProvider.JAMS, "float_adition", 1);
    public static final APUType FLOAT_MULTIPLICATION = new APUType(ResourceProvider.JAMS, "float_multiplication", 4);
    public static final APUType FLOAT_DIVISION = new APUType(ResourceProvider.JAMS, "float_division", 9);

    public APUType {
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
