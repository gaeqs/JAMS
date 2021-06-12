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

package net.jamsimulator.jams.plugin;

import javafx.application.Platform;
import javafx.scene.image.Image;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.image.icon.IconManager;
import net.jamsimulator.jams.manager.Labeled;

import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a JAMS's plugin.
 * <p>
 * Plugins can add new funcionalities or change aspects of the application.
 * To create a plugin you must create a new project containing at least a class extending this class and
 * a "plugin.json" file.
 * <p>
 * The "plugin.json" file must contain at least three fields:
 * <p>
 * name: the name of the plugin. Spaces are allowed and they will replaced by underscores when the plugin is loaded.
 * <p>
 * version: the version of the plugin as a string.
 * <p>
 * main: the main class of the plugin. This is the class extending {@link Plugin}.
 * <p>
 * For more fields, check {@link PluginHeader}.
 * <p>
 * Plugins are loaded by a {@link PluginClassLoader}.
 * This makes them accessible to the classes of other plugins.
 * <p>
 * When a plugin is enabled it is registered in the {@link Jams#getGeneralEventBroadcast() general event broadcast}.
 */
public class Plugin implements Labeled {

    private PluginClassLoader classLoader;
    private PluginHeader header;
    private boolean enabled;
    private Set<Plugin> dependencies, enabledSoftDepenedencies;
    private Image favicon;

    /**
     * Runs the given code in the JavaFX application thread.
     * <p>
     * This allows the code to modify JavaFX nodes.
     *
     * @param runnable the code to run.
     */
    public static void runInApplicationThread(Runnable runnable) {
        Platform.runLater(runnable);
    }

    @Override
    public String getName() {
        return header == null ? "NOT FOUND" : header.name();
    }

    /**
     * Returns the {@link PluginClassLoader} of this plugin.
     * <p>
     * This {@link ClassLoader} gives acces to the classes of all plugins.
     *
     * @return the {@link PluginClassLoader}.
     */
    public PluginClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Returns the {@link PluginHeader} of this plugin.
     * <p>
     * The {@link PluginHeader} contains all the metadata of a plugin.
     * (Name, version, main class, url, authors...)
     *
     * @return the {@link PluginHeader} of this plugin.
     */
    public PluginHeader getHeader() {
        return header;
    }

    /**
     * Returns whether this plugin is enabled.
     *
     * @return whether this plugin is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * WARNING! This method should be used only by a {@link net.jamsimulator.jams.manager.PluginManager}!
     * <p>
     * Enables or disables this plugin.
     *
     * @param enabled whether the plugin should be enabled or disabled.
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) {
            loadDependencies();
            onEnable();
            Jams.getGeneralEventBroadcast().registerListeners(this, false);
        } else {
            onDisable();
            dependencies.clear();
            enabledSoftDepenedencies.clear();
            Jams.getGeneralEventBroadcast().unregisterListeners(this);
        }
    }

    /**
     * Retuns an unmodifiable {@link Set} with all dependencies of this plugin.
     *
     * @return the dependencies.
     */
    public Set<Plugin> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }

    /**
     * Retuns an unmodifiable {@link Set} with all loaded soft dependencies of this plugin.
     * <p>
     * Unloaded soft dependencies are not present in this {@link Set}.
     *
     * @return the soft dependencies.
     */
    public Set<Plugin> getEnabledSoftDepenedencies() {
        return Collections.unmodifiableSet(enabledSoftDepenedencies);
    }

    public Optional<Image> getFavicon() {
        return Optional.ofNullable(favicon);
    }

    /**
     * This method is called when a plugin is enabled. Override it to implement funcionality to your plugin!
     * <p>
     * Events are usually loaded before JAMS. Use {@link net.jamsimulator.jams.event.general.JAMSPostInitEvent} if
     * you need to register elements to JAMS.
     */
    public void onEnable() {

    }

    // region helper methods

    /**
     * This method is called when a plugin is disabled. Override it to implement funcionality to your plugin!
     */
    public void onDisable() {

    }

    /**
     * Returns an {@link InputStream} representing the resource in the plugin JAR matching the given path if present.
     *
     * @param path the path.
     * @return the resource's {@link InputStream} if present.
     */
    public Optional<InputStream> resource(String path) {
        return Optional.ofNullable(getClass().getResourceAsStream(path));
    }

    // endregion

    void init(PluginClassLoader classLoader, PluginHeader header) {
        this.classLoader = classLoader;
        this.header = header;
        this.enabled = false;
        try {
            if (header.favicon() != null) {
                var in = getClass().getResourceAsStream(header.favicon());
                if (in != null) {
                    favicon = new Image(in, IconManager.SIZE, IconManager.SIZE, false, false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadDependencies() {
        this.dependencies = header.dependencies().stream()
                .map(target -> Jams.getPluginManager().get(target).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        this.enabledSoftDepenedencies = header.softDependencies().stream()
                .map(target -> Jams.getPluginManager().get(target).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
