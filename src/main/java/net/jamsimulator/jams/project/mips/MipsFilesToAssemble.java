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

package net.jamsimulator.jams.project.mips;

import javafx.scene.Node;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayList;
import net.jamsimulator.jams.gui.display.FileDisplayTab;
import net.jamsimulator.jams.gui.mips.display.MipsFileDisplay;
import net.jamsimulator.jams.gui.mips.display.element.MipsFileElements;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.project.mips.event.FileAddToAssembleEvent;
import net.jamsimulator.jams.project.mips.event.FileRemoveFromAssembleEvent;
import net.jamsimulator.jams.utils.Validate;
import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.*;

public class MipsFilesToAssemble extends SimpleEventBroadcast {

	public static final String FILE_NAME = "files_to_assemble.json";

	private final MipsProject project;
	private final Map<File, MipsFileElements> files;
	private final List<String> globalLabels;

	public MipsFilesToAssemble(MipsProject project) {
		this.project = project;
		files = new HashMap<>();
		globalLabels = new ArrayList<>();
	}

	public Optional<MipsFileElements> getFileElements(File file) {
		return Optional.ofNullable(files.get(file));
	}

	public List<String> getGlobalLabels() {
		return Collections.unmodifiableList(globalLabels);
	}

	public Set<File> getFiles() {
		return Collections.unmodifiableSet(files.keySet());
	}

	public void addFile(File file) {
		Validate.notNull(file, "File cannot be null!");
		if (files.containsKey(file)) return;

		FileAddToAssembleEvent.Before before = callEvent(new FileAddToAssembleEvent.Before(file));
		if (before.isCancelled()) return;

		MipsFileElements elements = new MipsFileElements(file, project);

		try {
			List<String> lines = Files.readAllLines(file.toPath());
			StringBuilder builder = new StringBuilder();
			for (String line : lines) {
				builder.append(line).append('\n');
			}

			String text = builder.toString();
			if (!text.isEmpty()) {
				text = text.substring(0, text.length() - 1);
			}

			elements.refreshAll(text, null);
			files.put(file, elements);
			refreshGlobalLabels();
			callEvent(new FileAddToAssembleEvent.After(file));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void addFile(File file, MipsFileElements elements) {
		Validate.notNull(file, "File cannot be null!");
		Validate.notNull(elements, "Elements cannot be null!");

		FileAddToAssembleEvent.Before before = callEvent(new FileAddToAssembleEvent.Before(file));
		if (before.isCancelled()) return;

		if (files.containsKey(file)) return;
		files.put(file, elements);
		refreshGlobalLabels();

		callEvent(new FileAddToAssembleEvent.After(file));
	}

	public void addFile(File file, FileDisplayList list) {
		Validate.notNull(file, "File cannot be null!");
		Validate.notNull(list, "List cannot be null!");

		Optional<FileDisplayTab> tab = list.getFileDisplayTab(file);
		if (!tab.isPresent() || !(tab.get().getDisplay() instanceof MipsFileDisplay)) {
			addFile(file);
			return;
		}
		addFile(file, ((MipsFileDisplay) tab.get().getDisplay()).getElements());
	}

	public void removeFile(File file) {
		Validate.notNull(file, "File cannot be null!");

		FileRemoveFromAssembleEvent.Before before = callEvent(new FileRemoveFromAssembleEvent.Before(file));
		if (before.isCancelled()) return;

		if (!files.containsKey(file)) return;
		MipsFileElements elements = files.remove(file);
		refreshDeletedDisplay(file, elements);
		refreshGlobalLabels();

		callEvent(new FileRemoveFromAssembleEvent.After(file));
	}

	public void refreshGlobalLabels() {
		globalLabels.clear();
		for (MipsFileElements elements : files.values()) {
			globalLabels.addAll(elements.getExistingGlobalLabels());
		}

		ProjectTab tab = JamsApplication.getProjectsTabPane().getProjectTab(project).orElse(null);
		if (tab == null) return;
		Node node = tab.getProjectTabPane().getWorkingPane().getCenter();
		if (!(node instanceof FileDisplayList)) return;
		FileDisplayList list = (FileDisplayList) node;

		files.forEach((file, elements) -> {
			Optional<FileDisplayTab> fTab = list.getFileDisplayTab(file);
			if (fTab.isPresent()) {
				FileDisplay display = fTab.get().getDisplay();
				if (display instanceof MipsFileDisplay) {
					((MipsFileDisplay) display).refreshGlobalLabelErrorsAndParameters();
					return;
				}
			}
			//If the display is not present, just update the elements.
			elements.refreshGlobalLabelsChanges();
		});
	}


	public void save(File folder) throws IOException {
		Validate.notNull(folder, "Folder cannot be null!");
		File file = new File(folder, FILE_NAME);
		JSONArray array = new JSONArray();
		files.keySet().stream().map(File::getAbsolutePath).forEach(array::put);

		Writer writer = new FileWriter(file);
		writer.write(array.toString());
		writer.close();
	}

	private void refreshDeletedDisplay(File file, MipsFileElements elements) {
		ProjectTab tab = JamsApplication.getProjectsTabPane().getProjectTab(project).orElse(null);
		if (tab == null) return;
		Node node = tab.getProjectTabPane().getWorkingPane().getCenter();
		if (!(node instanceof FileDisplayList)) return;
		FileDisplayList list = (FileDisplayList) node;

		Optional<FileDisplayTab> fTab = list.getFileDisplayTab(file);
		if (fTab.isPresent()) {
			FileDisplay display = fTab.get().getDisplay();
			if (display instanceof MipsFileDisplay) {
				((MipsFileDisplay) display).refreshGlobalLabelErrorsAndParameters();
				return;
			}
		} else {
			elements.refreshGlobalLabelsChanges();
		}
	}
}
