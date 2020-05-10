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

package net.jamsimulator.jams.project;

import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


/**
 * Represents a JAMS's project.
 * Use this class to create your own project types.
 */
public interface Project {

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
	 * Assembles this project, creating a {@link Simulation}.
	 *
	 * @return the {@link Simulation}.
	 * @throws IOException                                                       any {@link IOException} occurred on assembly.
	 * @throws net.jamsimulator.jams.mips.assembler.exception.AssemblerException any assembler exception thrown by the assembler.
	 */
	Simulation<?> assemble() throws IOException;

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

}
