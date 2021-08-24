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
import net.jamsimulator.jams.utils.NumberRepresentationManager;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Registry {

    private final Map<String, Manager<?>> managers;
    private final Map<String, Boolean> primary;
    private final Map<Class<? extends Manager<?>>, Manager<?>> primaryManagersByClass;
    private final Map<Class<? extends ManagerResource>, Manager<?>> primaryManagersByManaged;


    public Registry() {
        managers = new HashMap<>();
        primary = new HashMap<>();
        primaryManagersByClass = new HashMap<>();
        primaryManagersByManaged = new HashMap<>();
        addDefaultManagers();
    }

    public synchronized void registerPrimary(Manager<?> manager) {
        if (managers.containsKey(manager.getName()))
            throw new IllegalArgumentException("There's already a manager with the name " + manager.getName() + "!");
        if (primaryManagersByClass.containsKey(manager.getClass()))
            throw new IllegalArgumentException("There's already a primary manager with the class " + manager.getClass() + "! " +
                    "Create a child class to add this manager.");
        if (primaryManagersByManaged.containsKey(manager.getManagedType()))
            throw new IllegalArgumentException("There's already a primary manager with the managed type " + manager.getManagedType() + "!");

        managers.put(manager.getName(), manager);
        primary.put(manager.getName(), true);
        primaryManagersByClass.put((Class<? extends Manager<?>>) manager.getClass(), manager);
        primaryManagersByManaged.put(manager.getManagedType(), manager);
    }

    public synchronized void registerSecondary(Manager<?> manager) {
        if (managers.containsKey(manager.getName()))
            throw new IllegalArgumentException("There's already a manager with the name " + manager.getName() + "!");
        managers.put(manager.getName(), manager);
        primary.put(manager.getName(), false);
    }

    public synchronized boolean unregister(String name) {
        if (name.equals(PluginManager.NAME))
            throw new IllegalArgumentException("You cannot unregister the plugin manager!");
        var manager = managers.remove(name);
        if (manager == null) return false;
        if (primary.remove(name)) {
            primaryManagersByClass.remove(manager.getClass());
            primaryManagersByManaged.remove(manager.getManagedType());
        }
        return true;
    }

    public synchronized <T extends Manager<?>> T get(Class<T> clazz) {
        try {
            var manager = (T) primaryManagersByClass.get(clazz);
            if (manager == null) throw new NoSuchElementException("Manager " + clazz + " not found.");
            return manager;
        } catch (ClassCastException ex) {
            throw new NoSuchElementException("Manager " + clazz + " not found.", ex);
        }
    }

    public synchronized <T extends ManagerResource> Manager<T> of(Class<T> clazz) {
        try {
            var manager = (Manager<T>) primaryManagersByManaged.get(clazz);
            if (manager == null) throw new NoSuchElementException("Manager " + clazz + " not found.");
            return manager;
        } catch (ClassCastException ex) {
            throw new NoSuchElementException("Manager " + clazz + " not found.", ex);
        }
    }

    public synchronized Manager<?> of(String name) {
        var manager = (Manager<?>) managers.get(name);
        if (manager == null) throw new NoSuchElementException("Manager " + name + " not found.");
        return manager;
    }

    public synchronized <T extends ManagerResource> Manager<T> of(String name, Class<T> clazz) {
        try {
            var manager = (Manager<T>) managers.get(name);
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
        for (Manager<?> manager : managers.values()) {
            if (!manager.isLoaded() && !manager.shouldLoadOnFXThread()) {
                manager.load();
            }
        }
    }

    public void loadJAMSApplicationManagers() {
        for (Manager<?> manager : managers.values()) {
            if (!manager.isLoaded() && manager.shouldLoadOnFXThread()) {
                manager.load();
            }
        }
    }

    private void addDefaultManagers() {
        registerPrimary(PluginManager.INSTANCE);
        registerPrimary(ActionManager.INSTANCE);
        registerPrimary(ArchitectureManager.INSTANCE);
        registerPrimary(AssemblerBuilderManager.INSTANCE);
        registerPrimary(BarSnapshotViewModeManager.INSTANCE);
        registerPrimary(CacheBuilderManager.INSTANCE);
        registerPrimary(DirectiveSetManager.INSTANCE);
        registerPrimary(FileTypeManager.INSTANCE);
        registerPrimary(InstructionSetManager.INSTANCE);
        registerPrimary(LanguageManager.INSTANCE);
        registerPrimary(MemoryBuilderManager.INSTANCE);
        registerPrimary(MIPSEditorInspectionBuilderManager.INSTANCE);
        registerPrimary(NumberRepresentationManager.INSTANCE);
        registerPrimary(ProjectTypeManager.INSTANCE);
        registerPrimary(RegistersBuilderManager.INSTANCE);
        registerPrimary(SyscallExecutionBuilderBundleManager.INSTANCE);
        registerPrimary(SyscallExecutionBuilderManager.INSTANCE);
        registerPrimary(ThemeManager.INSTANCE);
    }

}
