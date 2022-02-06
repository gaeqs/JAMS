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

package net.jamsimulator.jams;

import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.configuration.format.ConfigurationFormat;
import net.jamsimulator.jams.configuration.format.ConfigurationFormatJSON;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.event.general.JAMSPostInitEvent;
import net.jamsimulator.jams.event.general.JAMSPreInitEvent;
import net.jamsimulator.jams.event.general.JAMSShutdownEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.Registry;
import net.jamsimulator.jams.plugin.Plugin;
import net.jamsimulator.jams.plugin.PluginManager;
import net.jamsimulator.jams.plugin.exception.InvalidPluginHeaderException;
import net.jamsimulator.jams.plugin.exception.PluginLoadException;
import net.jamsimulator.jams.project.RecentProjects;
import net.jamsimulator.jams.task.TaskExecutor;
import net.jamsimulator.jams.utils.*;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Jams {

    public final static Registry REGISTRY = new Registry(true);

    private static String VERSION;

    private static File mainFolder;

    private static RootConfiguration mainConfiguration;

    private static RecentProjects recentProjects;
    private static TaskExecutor taskExecutor;
    private static SimpleEventBroadcast generalEventBroadcast;

    private static FileSystem fileSystem;
    private static ProtectedFileSystem fileSystemWrapper;

    //JAMS main method.
    public static void main(String[] args) {
        var data = new ArgumentsData(args);
        generalEventBroadcast = new SimpleEventBroadcast();
        taskExecutor = new TaskExecutor();

        loadVersion();
        System.out.println("Loading JAMS version " + getVersion());
        mainFolder = FolderUtils.checkMainFolder();
        TempUtils.loadTemporalFolder();

        try {
            var path = Jams.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (Files.isDirectory(Path.of(path))) {
                fileSystem = FileSystems.getDefault();
            } else {
                fileSystem = FileSystems.newFileSystem(URI.create("jar:" + path), Map.of("create", "true"));
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        fileSystemWrapper = new ProtectedFileSystem(fileSystem);
        REGISTRY.loadPluginManager();

        loadPluginsFromArguments(data);

        generalEventBroadcast.callEvent(new JAMSPreInitEvent());
        mainConfiguration = ConfigurationUtils.loadMainConfiguration();
        REGISTRY.loadJAMSManagers();
        recentProjects = new RecentProjects();
        generalEventBroadcast.callEvent(new JAMSPostInitEvent());

        JamsApplication.main(args);

        // This is called when the JavaFX manager finishes:
        onClose();
    }

    private static boolean testInit = false;

    public static void initForTests() {
        if (testInit) return;

        generalEventBroadcast = new SimpleEventBroadcast();
        taskExecutor = new TaskExecutor();

        loadVersion();
        System.out.println("Loading JAMS version " + getVersion());
        mainFolder = FolderUtils.checkMainFolder();
        TempUtils.loadTemporalFolder();

        try {
            var path = Jams.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (Files.isDirectory(Path.of(path))) {
                fileSystem = FileSystems.getDefault();
            } else {
                fileSystem = FileSystems.newFileSystem(URI.create("jar:" + path), Map.of("create", "true"));
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        fileSystemWrapper = new ProtectedFileSystem(fileSystem);
        generalEventBroadcast.callEvent(new JAMSPreInitEvent());
        mainConfiguration = ConfigurationUtils.loadMainConfiguration();
        REGISTRY.loadJAMSManagers();
        recentProjects = new RecentProjects();
        generalEventBroadcast.callEvent(new JAMSPostInitEvent());

        testInit = true;
    }

    /**
     * Returns the version of this instance of JAMS.
     *
     * @return the version of JAMS.
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * Returns the JAMS's jar {@link FileSystem}.
     * <p>
     * This {@link FileSystem} is used to access the data inside the JAMS's jar.
     * <p>
     * This {@link FileSystem} cannot be closed.
     *
     * @return the {@link FileSystem}.
     */
    public static FileSystem getFileSystem() {
        return fileSystemWrapper;
    }

    /**
     * Returns JAMS's main folder. This folder is used to store general data.
     *
     * @return JAMS's main folder.
     */
    public static File getMainFolder() {
        return mainFolder;
    }

    /**
     * Returns JAMS's main configuration.
     *
     * @return JAMS's main configuration.
     */
    public static RootConfiguration getMainConfiguration() {
        return mainConfiguration;
    }

    /**
     * Returns the {@link java.util.List list} containing the {@link net.jamsimulator.jams.project.ProjectSnapshot recent projects}.
     *
     * @return the recent projects.
     */
    public static RecentProjects getRecentProjects() {
        return recentProjects;
    }

    /**
     * Returns the general event broadcast.
     * <p>
     * This broadcast is used to send general events, such the initialization events.
     *
     * @return the general event broadcast.
     */
    public static SimpleEventBroadcast getGeneralEventBroadcast() {
        return generalEventBroadcast;
    }

    /**
     * Returns the global {@link TaskExecutor}.
     * <p>
     * Use this executor to run asynchronous tasks.
     *
     * @return the net.jamsimulator.jams.task executor.
     */
    public static TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    private static void loadVersion() {
        InputStream stream = Jams.class.getResourceAsStream("/info.json");
        try {
            JSONObject object = new JSONObject(FileUtils.readAll(stream));
            VERSION = object.getString("version");
        } catch (Exception e) {
            e.printStackTrace();
            VERSION = "NULL";
        }
    }

    /**
     * This method is called when the JavaFX application finishes.
     * Shutdowns everything and saves all savable elements.
     */
    private static void onClose() {
        getGeneralEventBroadcast().callEvent(new JAMSShutdownEvent.Before());
        //Save main configuration.
        try {
            getMainConfiguration().save(
                    Manager.of(ConfigurationFormat.class).getOrNull(ConfigurationFormatJSON.NAME),
                    true
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        JamsApplication.getProjectsTabPane().saveOpenProjects();
        getRecentProjects().save();
        for (ProjectTab project : JamsApplication.getProjectsTabPane().getProjects()) {
            project.getProject().onClose();
        }

        getGeneralEventBroadcast().callEvent(new JAMSShutdownEvent.After());

        // Disables all plugins
        Manager.of(Plugin.class).forEach(p -> p.setEnabled(false));

        try {
            fileSystem.close();
        } catch (UnsupportedOperationException ignore) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadPluginsFromArguments(ArgumentsData data) {
        // Load plugins from the arguments
        for (File plugin : data.getPluginsToLoad()) {
            try {
                var manager = REGISTRY.get(PluginManager.class);
                manager.add(manager.loadPlugin(plugin));
            } catch (InvalidPluginHeaderException | PluginLoadException e) {
                System.out.println("Couldn't load plugin " + plugin.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }
}
