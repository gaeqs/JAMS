/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.plugin.Plugin;
import net.jamsimulator.jams.plugin.PluginClassLoader;
import net.jamsimulator.jams.plugin.PluginHeader;
import net.jamsimulator.jams.plugin.event.PluginRegisterEvent;
import net.jamsimulator.jams.plugin.event.PluginUnregisterEvent;
import net.jamsimulator.jams.plugin.exception.InvalidPluginHeaderException;
import net.jamsimulator.jams.plugin.exception.PluginLoadException;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.jar.JarFile;

/**
 * This singleton manages all {@link Plugin}s loaded by JAMS.
 * <p>
 * To load a {@link Plugin} use the method {@link #loadPlugin(File)}. Loaded plugins are not loaded yet,
 * so you must use the method {@link #add(Plugin)} to register and enable it.
 * <p>
 * For a plugin to be loaded correctly, all its dependencies should be already loaded and registered in
 * this manager.
 * <p>
 * To unregister an {@link Plugin} use {@link #remove(Object)}. This action disables the plugin.
 * Plugins that act as a dependency or as a soft dependency cannot be unregistered unless all dependent
 * plugins are unregistered before.
 */
public class PluginManager extends Manager<Plugin> {

    public static final File PLUGIN_FOLDER = new File(Jams.getMainFolder(), "plugins");
    public static final PluginManager INSTANCE = new PluginManager();

    private PluginManager() {
        super(PluginRegisterEvent.Before::new, PluginRegisterEvent.After::new,
                PluginUnregisterEvent.Before::new, PluginUnregisterEvent.After::new);
    }

    /**
     * Attempts to register the given plugin.
     * If the plugin is null this method throws an {@link NullPointerException}.
     * If the plugin is already present or the register event is cancelled this method returns {@link false}.
     * If the operation was successful the method returns {@link true}.
     * <p>
     * If the plugin can be registered, this method calls {@link Plugin#onEnable()}
     *
     * @param plugin the plugin to register.
     * @return whether the operation was successful.
     * @throws NullPointerException when the given element is null.
     * @see HashSet#add(Object)
     */
    @Override
    public boolean add(Plugin plugin) {
        // The method fails if the plugin is already enabled (if the plugin is already added in a PluginManager).
        if (plugin.isEnabled()) return false;
        if (!super.add(plugin)) return false;

        try {
            System.out.println("Enabling plugin " + plugin.getName() + ".");
            plugin.setEnabled(true);
        } catch (Exception ex) {
            System.err.println(plugin.getName() + "'s onEnable() throwed an exception!");
            ex.printStackTrace();
        }

        return true;
    }

    /**
     * Attempts to unregister the given plugin.
     * If the element is null this method throws a {@link NullPointerException}.
     * If the element type is invalid this method returns {@link false}.
     * If the element is not present or the unregister event is cancelled this method returns {@link false}.
     * If the operation was successful the method returns {@link true}.
     * If the plugin is a dependency or a soft dependency of another plugin this method returns false.
     * <p>
     * If the plugin can be unregistered, this method calls {@link Plugin#onDisable()}
     *
     * @param o the element to unregister.
     * @return whether the operation was successful.
     * @throws NullPointerException when the given element is null.
     * @see HashSet#remove(Object)
     * @see Manager#remove(Object)
     */
    @Override
    public boolean remove(Object o) {
        if (o instanceof Plugin plugin) {
            if (parallelStream().anyMatch(other -> other.getDependencies().contains(plugin)
                    || other.getEnabledSoftDepenedencies().contains(plugin))) return false;
            if (!super.remove(o)) return false;
            try {
                System.out.println("Disabling plugin " + plugin.getName() + ".");
                plugin.setEnabled(false);
            } catch (Exception ex) {
                System.err.println(plugin.getName() + "'s onDisable() throwed an exception!");
                ex.printStackTrace();
            }
            return true;
        } else return super.remove(o);
    }

    @Override
    protected void loadDefaultElements() {
        if (!FolderUtils.checkFolder(PLUGIN_FOLDER)) throw new RuntimeException("Couldn't create plugins folder!");
        var headers = new HashSet<PluginHeader>();
        var files = PLUGIN_FOLDER.listFiles();
        if (files == null) return;
        var jars = Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> file.getName().toLowerCase().endsWith(".jar"));

        jars.forEach(jar -> {
            try {
                headers.add(loadPluginHeader(jar));
            } catch (InvalidPluginHeaderException e) {
                System.err.println("Error whiler loading the plugin " + jar + "!");
                e.printStackTrace();
            }
        });

        var loaded = new HashSet<PluginHeader>();
        for (PluginHeader header : headers) {
            if (loaded.stream().anyMatch(target -> target.name().equals(header.name()))) {
                System.err.println("Error while loading the plugin " + header.file().getPath()
                        + ". A plugin with the same id (" + header.name() + ") is already loaded!");
            }
            if (!loaded.contains(header)) {
                try {
                    add(loadPluginAndDependencies(header, headers, loaded));
                } catch (PluginLoadException e) {
                    System.err.println("Error while loading plugin " + header.name() + "!");
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Loads the {@link Plugin} in the given {@link File}.
     * <p>
     * WARNING! This method only loads the {@link Plugin}. To register it use {@link #add(Plugin)}.
     * Plugins requires all it's dependencies registered in this manager to be loaded.
     *
     * @param file the {@link File} of the {@link Plugin}.
     * @return the loaded {@link Plugin}.
     * @throws InvalidPluginHeaderException when the {@link PluginHeader} of the {@link Plugin} couldn't be loaded.
     * @throws PluginLoadException          when the {@link Plugin} couldn't be loaded.
     */
    public Plugin loadPlugin(File file) throws InvalidPluginHeaderException, PluginLoadException {
        Validate.notNull(file, "Plugin cannot be null!");
        Validate.isTrue(file.exists(), "File must exist!");
        return loadPlugin(loadPluginHeader(file));
    }

    /**
     * Loads the {@link Plugin} represented by the given {@link PluginHeader}.
     * <p>
     * WARNING! This method only loads the {@link Plugin}. To register it use {@link #add(Plugin)}.
     * Plugins requires all it's dependencies registered in this manager to be loaded.
     *
     * @param header the {@link PluginHeader} of the {@link Plugin}.
     * @return the loaded {@link Plugin}.
     * @throws PluginLoadException when the {@link Plugin} couldn't be loaded.
     */
    public Plugin loadPlugin(PluginHeader header) throws PluginLoadException {
        Validate.notNull(header, "Header cannot be null!");
        for (String dependency : header.dependencies()) {
            if (get(dependency).isEmpty())
                throw new PluginLoadException("Couldn't find dependency " + dependency + "!", header);
        }

        try {
            return new PluginClassLoader(Jams.class.getClassLoader(), header).getPlugin();
        } catch (MalformedURLException ex) {
            throw new PluginLoadException(ex, header);
        }
    }

    /**
     * Unregisters and unloads the given {@link Plugin} and deletes its file.
     *
     * @param plugin the {@link Plugin}.
     * @return whether the operation was sucessfull.
     * @see #remove(Object)
     */
    public boolean unistallPlugin(Plugin plugin) {
        // Unregisters
        if (contains(plugin)) {
            if (!remove(plugin)) return false;
        }

        // Unloads
        try {
            plugin.getClassLoader().close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // And deletes!
        return plugin.getHeader().file().delete();
    }

    /**
     * Copies the given {@link Plugin} to the plugins file, loads it and registers it.
     * <p>
     * Before copying, this method will check the "plugin.json" file of the plugin.
     * If a {@link Plugin} with the same id is already registered, this {@link Plugin} won't be installed.
     *
     * @param file the file of the {@link Plugin}.
     * @return whether the operation was sucessful.
     */
    public boolean installPLugin(File file) {
        try {
            var header = loadPluginHeader(file);
            if (stream().anyMatch(plugin -> plugin.getHeader().name().equals(header.name())))
                return false;

            var to = new File(PLUGIN_FOLDER, header.name() + "-" + header.version() + ".jar");
            int i = 1;
            while (to.exists()) {
                to = new File(PLUGIN_FOLDER, header.name() + "-" + header.version() + " (" + i++ + ").jar");
            }

            Files.copy(file.toPath(), to.toPath(), StandardCopyOption.COPY_ATTRIBUTES);

            try {
                return add(loadPlugin(to));
            } catch (PluginLoadException e) {
                to.delete();
                e.printStackTrace();
                return false;
            }

        } catch (InvalidPluginHeaderException | IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private Plugin loadPluginAndDependencies(PluginHeader header, HashSet<PluginHeader> plugins,
                                             HashSet<PluginHeader> loaded) throws PluginLoadException {

        // Searches for dependencies
        for (String dependency : header.dependencies()) {
            if (loaded.stream().anyMatch(t -> t.name().equals(dependency))) continue;

            var dependencyHeader = plugins.stream().filter(t -> t.name().equals(dependency)).findAny()
                    .orElseThrow(() -> new PluginLoadException("Couldn't find dependency " + dependency + "!", header));

            // Here we don't capture any exception. If the dependency fails to load, the dependent plugin fails too!
            loadPluginAndDependencies(dependencyHeader, plugins, loaded);
        }

        // Searches for soft dependencies
        for (String dependency : header.softDependencies()) {
            if (loaded.stream().anyMatch(t -> t.name().equals(dependency))) continue;

            var optional = plugins.stream().filter(t -> t.name().equals(dependency)).findAny();

            // Here we don't capture any exception. If the dependency fails to load, the dependent plugin fails too!
            if (optional.isPresent()) loadPluginAndDependencies(optional.get(), plugins, loaded);
        }

        try {
            var plugin = new PluginClassLoader(Jams.class.getClassLoader(), header).getPlugin();
            loaded.add(header);
            return plugin;
        } catch (MalformedURLException ex) {
            throw new PluginLoadException(ex, header);
        }
    }

    private PluginHeader loadPluginHeader(File file) throws InvalidPluginHeaderException {
        JarFile jar = null;
        InputStream in = null;
        try {
            jar = new JarFile(file);
            var entry = jar.getJarEntry("plugin.json");
            if (entry == null)
                throw new InvalidPluginHeaderException(
                        new FileNotFoundException("Plugins must contain a plugin.json inside its jar!"));

            in = jar.getInputStream(entry);
            return PluginHeader.loadJSON(new RootConfiguration(new InputStreamReader(in)), file);
        } catch (IOException e) {
            throw new InvalidPluginHeaderException(e);
        } finally {
            if (jar != null) try {
                jar.close();
            } catch (IOException ignore) {
            }
            if (in != null) try {
                in.close();
            } catch (IOException ignore) {
            }
        }
    }

}
