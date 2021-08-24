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

package net.jamsimulator.jams.mips.syscall.bundle;

import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SyscallExecutionBuilderBundle implements ManagerResource {

    protected final ResourceProvider provider;
    protected final String name;
    protected final Map<Integer, String> builders;

    public SyscallExecutionBuilderBundle(ResourceProvider provider, String name) {
        Validate.notNull(provider, "Provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        this.provider = provider;
        this.name = name;
        this.builders = new HashMap<>();
    }

    public SyscallExecutionBuilderBundle(ResourceProvider provider, String name, Map<Integer, String> builders) {
        Validate.notNull(provider, "Provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(builders, "Builders cannot be null!");
        this.provider = provider;
        this.name = name;
        this.builders = new HashMap<>(builders);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return provider;
    }

    public void addBuilder(int id, String name) {
        Validate.notNull(name, "Name cannot be null!");
        builders.put(id, name);
    }

    public void removeBuilder(int id) {
        builders.remove(id);
    }

    public Map<Integer, SyscallExecutionBuilder<?>> buildBundle() {
        var map = new HashMap<Integer, SyscallExecutionBuilder<?>>();

        builders.forEach((id, name) ->
                Manager.of(SyscallExecutionBuilder.class).get(name).ifPresent(builder -> map.put(id, builder)));

        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyscallExecutionBuilderBundle that = (SyscallExecutionBuilderBundle) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
