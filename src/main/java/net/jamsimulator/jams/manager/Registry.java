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
import net.jamsimulator.jams.gui.mips.editor.indexing.inspection.MIPSInspectorManager;
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
import java.util.Optional;

/**
 * The registry class stores all {@link Manager}s JAMS is currently using.
 * <p>
 * There are two types of managers: primary and secondary managers.
 * <p>
 * Both type of managers can't collide in its names: you can't have two managers with the same name.
 *
 * <h2>Primary managers</h2>
 * Primary managers are managers that can be indexed using its class or its managed element type.
 * <p>
 * Primary managers can't collide in its class or its managed element type. For example: you can only have one
 * primary {@link ThemeManager} and one primary manager that manages
 * {@link net.jamsimulator.jams.gui.theme.Theme Themes}.
 *
 * <h2>Secondary managers</h2>
 * Secondary managers can only be indexed by its name. You can have multiple secondary managers that manages the same
 * element type.
 *
 * <h2>JAMS's Registry</h2>
 * You can access the JAMS's Registry using {@link net.jamsimulator.jams.Jams#REGISTRY Jams.REGISTRY}.
 */
public class Registry {

    private final Map<String, Manager<?>> managers;
    private final Map<String, Boolean> primary;
    private final Map<Class<? extends Manager<?>>, Manager<?>> primaryManagersByClass;
    private final Map<Class<? extends ManagerResource>, Manager<?>> primaryManagersByManaged;

    /**
     * Creates a registry.
     */
    public Registry(boolean loadDefaultManagers) {
        managers = new HashMap<>();
        primary = new HashMap<>();
        primaryManagersByClass = new HashMap<>();
        primaryManagersByManaged = new HashMap<>();
        if (loadDefaultManagers) {
            addDefaultManagers();
        }
    }

    /**
     * Registers the given {@link Manager} as a primary {@link Manager}.
     * <p>
     * Manager must have a unique name.
     * Primary managers must have a unique managed type and a unique class.
     *
     * @param manager the manager.
     * @throws IllegalArgumentException when the manager collides with another manager.
     */
    @SuppressWarnings("unchecked")
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

    /**
     * Registers the given {@link Manager} as a secondary {@link Manager}.
     * <p>
     * Manager must have a unique name.
     *
     * @param manager the manager.
     * @throws IllegalArgumentException when there's already a manager registered with the same name.
     */
    public synchronized void registerSecondary(Manager<?> manager) {
        if (managers.containsKey(manager.getName()))
            throw new IllegalArgumentException("There's already a manager with the name " + manager.getName() + "!");
        managers.put(manager.getName(), manager);
        primary.put(manager.getName(), false);
    }

    /**
     * Unregister the registered manager that matched the given name.
     * <p>
     * You cannot unregister the plugin manager.
     *
     * @param name the name.
     * @return the manager if found.
     */
    public synchronized Optional<Manager<?>> unregister(String name) {
        if (name.equals(PluginManager.NAME))
            throw new IllegalArgumentException("You cannot unregister the plugin manager!");
        var manager = managers.remove(name);
        if (manager == null) return Optional.empty();
        if (primary.remove(name)) {
            primaryManagersByClass.remove(manager.getClass());
            primaryManagersByManaged.remove(manager.getManagedType());
        }
        return Optional.of(manager);
    }

    /**
     * Returns the primary manager who is instance the given class.
     *
     * @param clazz the clazz.
     * @param <T>   the type of the manager.
     * @return the manager.
     * @throws NoSuchElementException when the manager is not found.
     */
    @SuppressWarnings("unchecked")
    public synchronized <T extends Manager<?>> T get(Class<T> clazz) {
        try {
            var manager = (T) primaryManagersByClass.get(clazz);
            if (manager == null) throw new NoSuchElementException("Manager " + clazz + " not found.");
            return manager;
        } catch (ClassCastException ex) {
            throw new NoSuchElementException("Manager " + clazz + " not found.", ex);
        }
    }

    /**
     * Returns the primary manager who is instance the given class.
     *
     * @param clazz the clazz.
     * @param <T>   the type of the manager.
     * @return the manager if found.
     */
    public <T extends Manager<?>> Optional<T> getSafe(Class<T> clazz) {
        try {
            return Optional.of(get(clazz));
        } catch (NoSuchElementException ex) {
            return Optional.empty();
        }
    }

    /**
     * Returns the primary manager that manages the given type.
     *
     * @param clazz the managed type class.
     * @param <T>   the managed type.
     * @return the manager.
     * @throws NoSuchElementException when the manager is not found.
     */
    @SuppressWarnings("unchecked")
    public synchronized <T extends ManagerResource> Manager<T> of(Class<T> clazz) {
        try {
            var manager = (Manager<T>) primaryManagersByManaged.get(clazz);
            if (manager == null) throw new NoSuchElementException("Manager " + clazz + " not found.");
            return manager;
        } catch (ClassCastException ex) {
            throw new NoSuchElementException("Manager " + clazz + " not found.", ex);
        }
    }

    /**
     * Returns the primary manager that manages the given type.
     *
     * @param clazz the managed type class.
     * @param <T>   the managed type.
     * @return the manager if found.
     */
    public <T extends ManagerResource> Optional<Manager<T>> ofSafe(Class<T> clazz) {
        try {
            return Optional.of(of(clazz));
        } catch (NoSuchElementException ex) {
            return Optional.empty();
        }
    }

    /**
     * Returns the primary manager that matches the given name.
     *
     * @param name the name of the manager.
     * @return the manager.
     * @throws NoSuchElementException when the manager is not found.
     */
    public synchronized Manager<?> of(String name) {
        var manager = (Manager<?>) managers.get(name);
        if (manager == null) throw new NoSuchElementException("Manager " + name + " not found.");
        return manager;
    }

    /**
     * Returns the primary manager that matches the given name.
     *
     * @param name the name of the manager.
     * @return the manager if present.
     */
    public synchronized Optional<Manager<?>> ofSafe(String name) {
        try {
            return Optional.of(of(name));
        } catch (NoSuchElementException ex) {
            return Optional.empty();
        }
    }

    /**
     * Returns the primary manager that matched the given name cast to the given managed type.
     *
     * @param name  the name of the manager.
     * @param clazz the managed type class.
     * @param <T>   the managed type.
     * @return the manager if found.
     */
    @SuppressWarnings("unchecked")
    public synchronized <T extends ManagerResource> Manager<T> of(String name, Class<T> clazz) {
        try {
            var manager = (Manager<T>) managers.get(name);
            if (manager == null) throw new NoSuchElementException("Manager " + clazz + " not found.");
            return manager;
        } catch (ClassCastException ex) {
            throw new NoSuchElementException("Manager " + clazz + " not found.", ex);
        }
    }

    /**
     * Returns the primary manager that matches the given name cast to the given managed type.
     *
     * @param name  the name of the manager.
     * @param clazz the managed type class.
     * @param <T>   the managed type.
     * @return the manager if present.
     */
    public synchronized <T extends ManagerResource> Optional<Manager<T>> ofSafe(String name, Class<T> clazz) {
        try {
            return Optional.of(of(name, clazz));
        } catch (NoSuchElementException ex) {
            return Optional.empty();
        }
    }

    /**
     * Calls the {@link PluginManager}'s {@link Manager#load()}.
     * <p>
     * This method does nothing if the manages is already loaded.
     */
    public void loadPluginManager() {
        var manager = get(PluginManager.class);
        if (!manager.isLoaded()) {
            manager.load();
        }
    }

    /**
     * Calls all non JavaFX manager's {@link Manager#load()}.
     * <p>
     * This method does nothing if the manages is already loaded.
     */
    public void loadJAMSManagers() {
        for (Manager<?> manager : managers.values()) {
            if (!manager.isLoaded() && !manager.shouldLoadOnFXThread()) {
                manager.load();
            }
        }
    }

    /**
     * Calls all JavaFX manager's {@link Manager#load()}.
     * <p>
     * This method does nothing if the manages is already loaded.
     */
    public void loadJAMSApplicationManagers() {
        for (Manager<?> manager : managers.values()) {
            if (!manager.isLoaded() && manager.shouldLoadOnFXThread()) {
                manager.load();
            }
        }
    }

    /**
     * Removes all managers and resources provided by the given {@link ResourceProvider}.
     * <p>
     * This is automatically called for the main registry when a plugin is unloaded.
     *
     * @param provider the provider.
     */
    public void removeProvidedBy(ResourceProvider provider) {
        // Unregister all managers provided by this provider.
        var list = managers.entrySet().stream()
                .filter(it -> it.getValue().getResourceProvider().equals(provider)).toList();
        list.forEach(it -> unregister(it.getKey()));

        // Unregister all resources.
        managers.values().forEach(it -> it.removeProvidedBy(provider));

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
        registerPrimary(NumberRepresentationManager.INSTANCE);
        registerPrimary(ProjectTypeManager.INSTANCE);
        registerPrimary(RegistersBuilderManager.INSTANCE);
        registerPrimary(SyscallExecutionBuilderBundleManager.INSTANCE);
        registerPrimary(SyscallExecutionBuilderManager.INSTANCE);
        registerPrimary(ThemeManager.INSTANCE);
        registerPrimary(MIPSInspectorManager.INSTANCE);
    }

}
