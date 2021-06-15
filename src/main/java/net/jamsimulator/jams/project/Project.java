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

package net.jamsimulator.jams.project;

import javafx.scene.control.Tab;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


/**
 * Represents a JAMS's project.
 * Use this class to create your own project types.
 */
public interface Project {

    /**
     * Returns the {@link ProjectType type} of this project.
     *
     * @return the {@link ProjectType type}.
     */
    ProjectType<?> getType();

    /**
     * Returns the name of this project. This name must be selected by the user.
     *
     * @return the name.
     */
    String getName();

    /**
     * Returns the main {@link File folder} of this project.
     *
     * @return the main {@link File folder}.
     */
    File getFolder();

    /**
     * Returns the {@link ProjectData metadata} of this project.
     * The data stores project configurations, build settings and miscellaneous data.
     *
     * @return the {@link ProjectData metadata}.
     */
    ProjectData getData();

    /**
     * Assembles this project and creates a simulation pane in the project pane.
     * <p>
     *
     * @param log The log debug messages will be print on. This log may be null.
     * @throws IOException      any {@link IOException} occurred on assembly.
     * @throws RuntimeException any assembler exception thrown by the assembler.
     */
    void generateSimulation(Log log) throws IOException;

    /**
     * Returns the assigned {@link ProjectTab}, if present.
     *
     * @return the assigned {@link ProjectTab}.
     */
    Optional<ProjectTab> getProjectTab();

    /**
     * Assigns the project to the given tab. Used to store
     * the {@link ProjectTab}.
     *
     * @param tab the tab.
     */
    void assignProjectTab(ProjectTab tab);

    /**
     * This method is called when the project tab is closed.
     * The implementation of this method should clear all
     * listeners from its inner {@link net.jamsimulator.jams.event.EventBroadcast}s.
     */
    void onClose();


    /**
     * Generates the main project pane of this project.
     * <p>
     * This pane is a JavaFX node displaying the whole project.
     *
     * @param tab        the {@link Tab} containing this pane.
     * @param projectTab the {@link Tab} containing the project.
     * @return the main project pane.
     */
    WorkingPane generateMainProjectPane(Tab tab, ProjectTab projectTab);

}
