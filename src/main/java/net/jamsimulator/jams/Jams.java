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

package net.jamsimulator.jams;

import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.event.general.JAMSPostInitEvent;
import net.jamsimulator.jams.event.general.JAMSPreInitEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.manager.*;
import net.jamsimulator.jams.project.RecentProjects;
import net.jamsimulator.jams.utils.ConfigurationUtils;
import net.jamsimulator.jams.utils.FileUtils;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.TempUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

public class Jams {

    private static String VERSION;

    private static File mainFolder;

    private static RootConfiguration mainConfiguration;

    private static PluginManager pluginManager;

    private static LanguageManager languageManager;
    private static FileTypeManager fileTypeManager;

    private static ArchitectureManager architectureManager;
    private static AssemblerBuilderManager assemblerBuilderManager;
    private static MemoryBuilderManager memoryBuilderManager;
    private static CacheBuilderManager cacheBuilderManager;
    private static RegistersBuilderManager registersBuilderManager;
    private static ProjectTypeManager projectTypeManager;

    private static InstructionSetManager instructionSetManager;
    private static DirectiveSetManager directiveSetManager;
    private static SyscallExecutionBuilderManager syscallExecutionBuilderManager;
    private static NumberRepresentationManager numberRepresentationManager;

    private static RecentProjects recentProjects;

    private static SimpleEventBroadcast generalEventBroadcast;

    //JAMS main method.
    public static void main(String[] args) {
        generalEventBroadcast = new SimpleEventBroadcast();
        loadVersion();
        System.out.println("Loading JAMS version " + getVersion());
        mainFolder = FolderUtils.checkMainFolder();
        TempUtils.loadTemporalFolder();

        pluginManager = PluginManager.INSTANCE;

        generalEventBroadcast.callEvent(new JAMSPreInitEvent());

        mainConfiguration = ConfigurationUtils.loadMainConfiguration();

        languageManager = LanguageManager.INSTANCE;
        fileTypeManager = FileTypeManager.INSTANCE;

        architectureManager = ArchitectureManager.INSTANCE;
        assemblerBuilderManager = AssemblerBuilderManager.INSTANCE;
        memoryBuilderManager = MemoryBuilderManager.INSTANCE;
        cacheBuilderManager = CacheBuilderManager.INSTANCE;
        registersBuilderManager = RegistersBuilderManager.INSTANCE;
        projectTypeManager = ProjectTypeManager.INSTANCE;

        instructionSetManager = InstructionSetManager.INSTANCE;
        directiveSetManager = DirectiveSetManager.INSTANCE;
        syscallExecutionBuilderManager = SyscallExecutionBuilderManager.INSTANCE;
        numberRepresentationManager = NumberRepresentationManager.INSTANCE;
        recentProjects = new RecentProjects();

        generalEventBroadcast.callEvent(new JAMSPostInitEvent());

        JamsApplication.main(args);
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
     * Returns the {@link LanguageManager}.
     *
     * @return the {@link LanguageManager}.
     */
    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Returns the {@link LanguageManager}.
     *
     * @return the {@link LanguageManager}.
     */
    public static LanguageManager getLanguageManager() {
        return languageManager;
    }

    /**
     * Returns the {@link FileTypeManager}.
     *
     * @return the {@link FileTypeManager}.
     */
    public static FileTypeManager getFileTypeManager() {
        return fileTypeManager;
    }

    /**
     * Returns the {@link ArchitectureManager}.
     *
     * @return the {@link ArchitectureManager}.
     */
    public static ArchitectureManager getArchitectureManager() {
        return architectureManager;
    }

    /**
     * Return the {@link AssemblerBuilderManager}.
     *
     * @return the {@link AssemblerBuilderManager}.
     * @see AssemblerBuilderManager
     */
    public static AssemblerBuilderManager getAssemblerBuilderManager() {
        return assemblerBuilderManager;
    }

    /**
     * Returns the {@link MemoryBuilderManager}.
     *
     * @return the {@link MemoryBuilderManager}.
     */
    public static MemoryBuilderManager getMemoryBuilderManager() {
        return memoryBuilderManager;
    }

    /**
     * Returns the {@link CacheBuilderManager}.
     *
     * @return the {@link CacheBuilderManager}.
     */
    public static CacheBuilderManager getCacheBuilderManager() {
        return cacheBuilderManager;
    }

    /**
     * Returns the {@link RegistersBuilderManager}.
     *
     * @return the {@link RegistersBuilderManager}.
     */
    public static RegistersBuilderManager getRegistersBuilderManager() {
        return registersBuilderManager;
    }

    /**
     * Returns the {@link ProjectTypeManager}.
     *
     * @return the {@link ProjectTypeManager}.
     */
    public static ProjectTypeManager getProjectTypeManager() {
        return projectTypeManager;
    }

    /**
     * Returns the {@link InstructionSetManager}.
     *
     * @return the {@link InstructionSetManager}.
     */
    public static InstructionSetManager getInstructionSetManager() {
        return instructionSetManager;
    }

    /**
     * Returns the {@link DirectiveSetManager}.
     *
     * @return the {@link DirectiveSetManager}.
     */
    public static DirectiveSetManager getDirectiveSetManager() {
        return directiveSetManager;
    }

    /**
     * Returns the {@link SyscallExecutionBuilderManager}.
     *
     * @return the {@link SyscallExecutionBuilderManager}.
     */
    public static SyscallExecutionBuilderManager getSyscallExecutionBuilderManager() {
        return syscallExecutionBuilderManager;
    }

    /**
     * Returns the {@link SyscallExecutionBuilderManager}.
     *
     * @return the {@link SyscallExecutionBuilderManager}.
     */
    public static NumberRepresentationManager getNumberRepresentationManager() {
        return numberRepresentationManager;
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
}
