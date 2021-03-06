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
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.Optional;

public abstract class BasicProject implements Project {

	protected final File folder;
	protected ProjectTab projectTab;

	protected ProjectData data;

	public BasicProject(File folder, boolean loadDataOnConstructor) {
		//Validate.notNull(folder, "Folder cannot be null!");
		//Validate.isTrue(folder.exists(), "Folder " + folder.getName() + " must exist!");
		//Validate.isTrue(folder.isDirectory(), "Folder must be a directory!");

		this.folder = folder;
		this.projectTab = null;

		if (loadDataOnConstructor) {
			loadData(null);
		}
	}

	@Override
	public String getName() {
		return data.getName();
	}

	@Override
	public File getFolder() {
		return folder;
	}

	@Override
	public ProjectData getData() {
		return data;
	}

	@Override
	public Optional<ProjectTab> getProjectTab() {
		return Optional.ofNullable(projectTab);
	}

	@Override
	public void assignProjectTab(ProjectTab tab) {
		this.projectTab = tab;
	}

	/**
	 * Loads the data of this project.
	 *
	 * @param name the name of the project, or null if the name should be get from the data.
	 */
	protected abstract void loadData(String name);
}
