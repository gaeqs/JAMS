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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.file.FileTypeManager;
import net.jamsimulator.jams.gui.action.ActionManager;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModeManager;
import net.jamsimulator.jams.gui.mips.inspection.MIPSEditorInspectionBuilderManager;
import net.jamsimulator.jams.gui.theme.ThemeManager;
import net.jamsimulator.jams.language.LanguageManager;
import net.jamsimulator.jams.mips.architecture.ArchitectureManager;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilderManager;
import net.jamsimulator.jams.mips.directive.set.DirectiveSetManager;
import net.jamsimulator.jams.mips.instruction.set.InstructionSetManager;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilderManager;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilderManager;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilderManager;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilderManager;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundleManager;
import net.jamsimulator.jams.plugin.PluginManager;
import net.jamsimulator.jams.project.ProjectTypeManager;
import net.jamsimulator.jams.utils.Labeled;
import net.jamsimulator.jams.utils.NumberRepresentationManager;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;
import java.util.function.Consumer;

public class Registry {

    private final List<Manager<?>> managers;
    private final Map<Class<? extends Manager<?>>, Manager<?>> managersByClass;
    private final Map<Class<? extends Labeled>, Manager<?>> managersByManaged;
    private final Map<String, Manager<?>> managersByName;

    public Registry() {
        managers = new LinkedList<>();
        managersByClass = new HashMap<>();
        managersByManaged = new HashMap<>();
        managersByName = new HashMap<>();
        addDefaultManagers();
    }

    public void forEach(Consumer<Manager<?>> consumer) {
        managers.forEach(consumer);
    }

    public <T extends ManagerResource> void registerFully(Manager<T> manager, String name,
                                                          boolean registerByClass, boolean registerByManaged) {
        Validate.notNull(manager, "Manager cannot be null!");
        registerFully(manager, registerByClass ? (Class<? extends Manager<T>>) manager.getClass() : null,
                name, registerByManaged);
    }

    public <T extends ManagerResource> void registerFully(Manager<T> manager, Class<? extends Manager<T>> managerClass,
                                                          String name, boolean registerByManaged) {
        Validate.notNull(manager, "Manager cannot be null!");
        Validate.isTrue(managerClass == null || managerClass.isInstance(manager),
                "Manager must be a instance of managerClass!");
        managers.add(manager);
        if (managerClass != null) {
            managersByClass.put(managerClass, manager);
        }
        if (registerByManaged) {
            managersByManaged.put(manager.getManagedType(), manager);
        }
        if (name != null) {
            managersByName.put(name, manager);
        }
    }

    public void unregister(Manager<?> manager) {
        if (managers.remove(manager)) {
            managersByClass.values().remove(manager);
            managersByManaged.values().remove(manager);
            managersByName.values().remove(manager);
        }
    }

    public <T extends Manager<?>> T get(Class<T> clazz) {
        try {
            var manager = (T) managersByClass.get(clazz);
            if (manager == null) throw new NoSuchElementException("Manager " + clazz + " not found.");
            return manager;
        } catch (ClassCastException ex) {
            throw new NoSuchElementException("Manager " + clazz + " not found.", ex);
        }
    }

    public <T extends ManagerResource> Manager<T> of(Class<T> clazz) {
        try {
            var manager = (Manager<T>) managersByManaged.get(clazz);
            if (manager == null) throw new NoSuchElementException("Manager " + clazz + " not found.");
            return manager;
        } catch (ClassCastException ex) {
            throw new NoSuchElementException("Manager " + clazz + " not found.", ex);
        }
    }

    public Manager<?> of(String name) {
        var manager = (Manager<?>) managersByName.get(name);
        if (manager == null) throw new NoSuchElementException("Manager " + name + " not found.");
        return manager;
    }

    public <T extends ManagerResource> Manager<T> of(String name, Class<T> clazz) {
        try {
            var manager = (Manager<T>) managersByName.get(name);
            if (manager == null) throw new NoSuchElementException("Manager " + clazz + " not found.");
            return manager;
        } catch (ClassCastException ex) {
            throw new NoSuchElementException("Manager " + clazz + " not found.", ex);
        }
    }

    public void loadPluginManager() {
        get(PluginManager.class).load();
    }

    public void loadJAMSManagers() {
        for (Manager<?> manager : managers) {
            if (!manager.isLoaded() && !manager.shouldLoadOnFXThread()) {
                manager.load();
            }
        }
    }

    public void loadJAMSApplicationManagers() {
        for (Manager<?> manager : managers) {
            if (!manager.isLoaded() && manager.shouldLoadOnFXThread()) {
                manager.load();
            }
        }
    }

    private void addDefaultManagers() {
        registerFully(PluginManager.INSTANCE, PluginManager.NAME, true, true);
        registerFully(ActionManager.INSTANCE, ActionManager.NAME, true, true);
        registerFully(ArchitectureManager.INSTANCE, ArchitectureManager.NAME, true, true);
        registerFully(AssemblerBuilderManager.INSTANCE, AssemblerBuilderManager.NAME, true, true);
        registerFully(BarSnapshotViewModeManager.INSTANCE, BarSnapshotViewModeManager.NAME, true, true);
        registerFully(CacheBuilderManager.INSTANCE, CacheBuilderManager.NAME, true, true);
        registerFully(DirectiveSetManager.INSTANCE, DirectiveSetManager.NAME, true, true);
        registerFully(FileTypeManager.INSTANCE, DirectiveSetManager.NAME, true, true);
        registerFully(InstructionSetManager.INSTANCE, InstructionSetManager.NAME, true, true);
        registerFully(LanguageManager.INSTANCE, LanguageManager.NAME, true, true);
        registerFully(MemoryBuilderManager.INSTANCE, MemoryBuilderManager.NAME, true, true);
        registerFully(MIPSEditorInspectionBuilderManager.INSTANCE, MIPSEditorInspectionBuilderManager.NAME, true, true);
        registerFully(NumberRepresentationManager.INSTANCE, NumberRepresentationManager.NAME, true, true);
        registerFully(ProjectTypeManager.INSTANCE, ProjectTypeManager.NAME, true, true);
        registerFully(RegistersBuilderManager.INSTANCE, RegistersBuilderManager.NAME, true, true);
        registerFully(SyscallExecutionBuilderBundleManager.INSTANCE, SyscallExecutionBuilderBundleManager.NAME, true, true);
        registerFully(SyscallExecutionBuilderManager.INSTANCE, SyscallExecutionBuilderManager.NAME, true, true);
        registerFully(ThemeManager.INSTANCE, ThemeManager.NAME, true, true);
    }

}
