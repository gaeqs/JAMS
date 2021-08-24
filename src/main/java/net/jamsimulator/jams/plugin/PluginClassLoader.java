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

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.plugin.exception.PluginLoadException;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * This {@link ClassLoader} allows {@link Plugin}s to see each other's classes.
 * <p>
 * It is also responsible for creating the {@link Plugin} instance.
 */
public class PluginClassLoader extends URLClassLoader {

    private final Plugin plugin;

    private final Map<String, Class<?>> loadedClasses;

    public PluginClassLoader(ClassLoader parent, PluginHeader header) throws MalformedURLException, PluginLoadException {
        super(new URL[]{header.file().toURI().toURL()}, parent);
        loadedClasses = new HashMap<>();

        try {
            var clazz = Class.forName(header.mainClass(), true, this);
            var pluginMainClass = clazz.asSubclass(Plugin.class);

            plugin = pluginMainClass.getConstructor().newInstance();
            plugin.init(this, header);
        } catch (ClassNotFoundException e) {
            throw new PluginLoadException(e, "Cannot find main class " + header.mainClass() + "!", header);
        } catch (ClassCastException e) {
            throw new PluginLoadException(e, "Main class " + header.mainClass() + " doesn't extend Plugin!", header);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new PluginLoadException(e, header.name() + "'s Main class has no empty public constructor!", header);
        } catch (InvocationTargetException | InstantiationException e) {
            throw new PluginLoadException(e, header.name() + "'s Main class couldn't be loaded correctly!", header);
        }
    }

    /**
     * Returns the {@link Plugin} managed by this class loader.
     *
     * @return the {@link Plugin}.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    private Class<?> findClass(String name, boolean checkOtherPlugins) throws ClassNotFoundException {
        var local = loadedClasses.get(name);
        if (local != null) return local;

        // Jams.getPluginManager() -> Avoids NullPointerException when the manager is being initialized.
        if (checkOtherPlugins) {
            for (Plugin plugin : Manager.of(Plugin.class)) {
                try {
                    return plugin.getClassLoader().findClass(name, false);
                } catch (ClassNotFoundException ignore) {
                }
            }
        }

        local = super.findClass(name);
        loadedClasses.put(name, local);
        return local;
    }

}
