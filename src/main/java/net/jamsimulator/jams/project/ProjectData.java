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

import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.utils.FolderUtils;

import java.io.File;
import java.io.IOException;

public abstract class ProjectData extends SimpleEventBroadcast {

	public static final String METADATA_FOLDER_NAME = ".jams";
	public static final String FILES_FOLDER_NAME = "files";
	public static final String METADATA_DATA_NAME = "data.json";

	protected final File folder;
	protected final File filesFolder;
	protected boolean loaded;
	protected RootConfiguration data;

	protected String name;

	public ProjectData(File projectFolder) {
		folder = new File(projectFolder, METADATA_FOLDER_NAME);
		if (!FolderUtils.checkFolder(folder)) {
			throw new RuntimeException("Couldn't create data folder!");
		}
		filesFolder = new File(projectFolder, FILES_FOLDER_NAME);
		if (!FolderUtils.checkFolder(filesFolder)) {
			throw new RuntimeException("Couldn't create files folder!");
		}
	}

	public File getFolder() {
		return folder;
	}

	public File getFilesFolder() {
		return filesFolder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void save() {
		data.set("name", name);
		try {
			data.save(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load() {
		if (loaded) return;
		loaded = true;
		try {
			data = new RootConfiguration(new File(folder, METADATA_DATA_NAME));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		name = data.getString("name").orElse(folder.getParentFile().getName());
	}
}
