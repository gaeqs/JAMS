package net.jamsimulator.jams.project.mips;

import javafx.scene.Node;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayList;
import net.jamsimulator.jams.gui.display.FileDisplayTab;
import net.jamsimulator.jams.gui.mips.display.MipsFileDisplay;
import net.jamsimulator.jams.gui.mips.display.element.MipsFileElements;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MipsFilesToAssemble {

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

		MipsFileElements elements = new MipsFileElements(file, project);

		try {
			List<String> lines = Files.readAllLines(file.toPath());
			StringBuilder builder = new StringBuilder();
			for (String line : lines) {
				builder.append(line).append('\n');
			}

			String text = builder.toString();
			text = text.substring(0, text.length() - 1);

			elements.refreshAll(text, null);
			files.put(file, elements);
			refreshGlobalLabels();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void addFile(File file, MipsFileElements elements) {
		Validate.notNull(file, "File cannot be null!");
		Validate.notNull(elements, "Elements cannot be null!");
		if (files.containsKey(file)) return;
		files.put(file, elements);
		refreshGlobalLabels();
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
			elements.refreshLabelsChanges();
		});
	}

}
