package net.jamsimulator.jams.plugin;

import net.jamsimulator.jams.Jams;
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
 * It is also responsible of creating the {@link Plugin} instance.
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
        if (checkOtherPlugins && Jams.getPluginManager() != null) {
            for (Plugin plugin : Jams.getPluginManager()) {
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
