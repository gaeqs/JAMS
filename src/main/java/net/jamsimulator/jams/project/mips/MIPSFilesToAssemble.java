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

import javafx.application.Platform;
import javafx.scene.Node;
import net.jamsimulator.jams.collection.Bag;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.editor.FileEditor;
import net.jamsimulator.jams.gui.editor.FileEditorHolder;
import net.jamsimulator.jams.gui.editor.FileEditorTab;
import net.jamsimulator.jams.gui.mips.editor.MIPSFileEditor;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.project.mips.event.FileAddToAssembleEvent;
import net.jamsimulator.jams.project.mips.event.FileRemoveFromAssembleEvent;
import net.jamsimulator.jams.utils.FileUtils;
import net.jamsimulator.jams.utils.Validate;
import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class MIPSFilesToAssemble extends SimpleEventBroadcast {

	public static final String FILE_NAME = "files_to_assemble.json";

	private final MIPSProject project;
	private final Map<File, MIPSFileElements> files;
	private final Bag<String> globalLabels;

	public MIPSFilesToAssemble(MIPSProject project) {
		this.project = project;
		files = new HashMap<>();
		globalLabels = new Bag<>();
	}

	public Optional<MIPSFileElements> getFileElements(File file) {
		return Optional.ofNullable(files.get(file));
	}

	public Bag<String> getGlobalLabels() {
		return globalLabels;
	}

	public Set<File> getFiles() {
		return Collections.unmodifiableSet(files.keySet());
	}

	public void addFile(File file, boolean refreshGlobalLabels) {
		Validate.notNull(file, "File cannot be null!");
		if (files.containsKey(file)) return;

		FileAddToAssembleEvent.Before before = callEvent(new FileAddToAssembleEvent.Before(file));
		if (before.isCancelled()) return;

		MIPSFileElements elements = new MIPSFileElements(project);
		elements.setFilesToAssemble(this);

		try {
			String text = FileUtils.readAll(file);
			if (!text.isEmpty()) {
				text = text.substring(0, text.length() - 1);
			}

			elements.refreshAll(text);
			files.put(file, elements);

			if (refreshGlobalLabels) {
				refreshGlobalLabels();
			}

			callEvent(new FileAddToAssembleEvent.After(file));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void addFile(File file, MIPSFileElements elements, boolean refreshGlobalLabels) {
		Validate.notNull(file, "File cannot be null!");
		Validate.notNull(elements, "Elements cannot be null!");

		FileAddToAssembleEvent.Before before = callEvent(new FileAddToAssembleEvent.Before(file));
		if (before.isCancelled()) return;

		if (files.containsKey(file)) return;
		files.put(file, elements);
		elements.setFilesToAssemble(this);

		if (refreshGlobalLabels) {
			refreshGlobalLabels();
		}

		callEvent(new FileAddToAssembleEvent.After(file));
	}

	public void addFile(File file, FileEditorHolder holder, boolean refreshGlobalLabels) {
		Validate.notNull(file, "File cannot be null!");
		Validate.notNull(holder, "List cannot be null!");

		Optional<FileEditorTab> tab = holder.getFileDisplayTab(file, true);
		if (tab.isEmpty() || !(tab.get().getDisplay() instanceof MIPSFileEditor)) {
			addFile(file, refreshGlobalLabels);
			return;
		}
		addFile(file, ((MIPSFileEditor) tab.get().getDisplay()).getElements(), refreshGlobalLabels);
	}

	public void removeFile(File file) {
		Validate.notNull(file, "File cannot be null!");

		FileRemoveFromAssembleEvent.Before before = callEvent(new FileRemoveFromAssembleEvent.Before(file));
		if (before.isCancelled()) return;

		if (!files.containsKey(file)) return;
		MIPSFileElements elements = files.remove(file);
		elements.setFilesToAssemble(null);
		refreshDeletedDisplay(file, elements);

		refreshGlobalLabels();

		callEvent(new FileRemoveFromAssembleEvent.After(file));
	}

	public void refreshGlobalLabels() {
		Set<String> toUpdate = new HashSet<>(globalLabels);

		globalLabels.clear();
		for (MIPSFileElements elements : files.values()) {
			globalLabels.addAll(elements.getExistingGlobalLabels());
		}

		toUpdate.addAll(globalLabels);

		ProjectTab tab = JamsApplication.getProjectsTabPane().getProjectTab(project).orElse(null);

		if (tab == null) return;
		Node node = tab.getProjectTabPane().getWorkingPane().getCenter();
		if (!(node instanceof FileEditorHolder)) return;
		FileEditorHolder holder = (FileEditorHolder) node;

		files.forEach((file, elements) -> {
			elements.searchForUpdates(toUpdate);
			Optional<FileEditorTab> fTab = holder.getFileDisplayTab(file, true);
			if (fTab.isPresent()) {
				FileEditor display = fTab.get().getDisplay();
				if (display instanceof MIPSFileEditor) {
					elements.update(((MIPSFileEditor) display));
				}
			}
		});
	}

	public void load(File folder) throws IOException {
		File file = new File(folder, FILE_NAME);
		if (!file.isFile()) return;

		String value = FileUtils.readAll(file);

		JSONArray array = new JSONArray(value);
		for (Object element : array) {
			file = new File(project.getFolder(), element.toString());
			if (!file.isFile()) continue;
			addFile(file, false);
		}

		Platform.runLater(this::refreshGlobalLabels);
	}

	public void save(File folder) throws IOException {
		Validate.notNull(folder, "Folder cannot be null!");
		File file = new File(folder, FILE_NAME);
		JSONArray array = new JSONArray();
		Path projectPath = project.getFolder().toPath();
		files.keySet().stream().map(target -> projectPath.relativize(target.toPath())).forEach(array::put);

		Writer writer = new FileWriter(file);
		writer.write(array.toString(1));
		writer.close();
	}

	public void checkFiles() {
		List<File> toRemove = files.keySet().stream().filter(target -> !target.isFile()).collect(Collectors.toList());
		for (File file : toRemove) {
			removeFile(file);
		}
	}

	private void refreshDeletedDisplay(File file, MIPSFileElements elements) {
		ProjectTab tab = JamsApplication.getProjectsTabPane().getProjectTab(project).orElse(null);
		if (tab == null) return;
		Node node = tab.getProjectTabPane().getWorkingPane().getCenter();
		if (!(node instanceof FileEditorHolder)) return;
		FileEditorHolder holder = (FileEditorHolder) node;

		Optional<FileEditorTab> fTab = holder.getFileDisplayTab(file, true);

		elements.searchForUpdates(globalLabels);
		if (fTab.isPresent()) {
			FileEditor display = fTab.get().getDisplay();
			if (display instanceof MIPSFileEditor) {
				elements.update(((MIPSFileEditor) display));
			}
		}
	}
}
