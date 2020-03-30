package net.jamsimulator.jams.gui.project.display;

import javafx.scene.control.TabPane;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileDisplayList extends TabPane {

	private WorkingPane workingPane;
	private final List<FileDisplayTab> displays;

	public FileDisplayList(WorkingPane workingPane) {
		this.workingPane = workingPane;
		this.displays = new ArrayList<>();
		setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
	}

	public void setWorkingPane(WorkingPane workingPane) {
		this.workingPane = workingPane;
	}

	public WorkingPane getWorkingPane() {
		return workingPane;
	}

	public void openFile(File file) {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");
		Validate.isTrue(file.isFile(), "File must be a file!");

		FileDisplayTab tab = new FileDisplayTab(this, file);
		displays.add(tab);
		getTabs().add(tab);
	}

	public int closeFile(File file) {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");
		Validate.isTrue(file.isFile(), "File must be a file!");

		List<FileDisplayTab> tabs = displays.stream().filter(target -> target.getFile().equals(file)).collect(Collectors.toList());
		for (FileDisplayTab tab : tabs) {
			displays.remove(tab);
			getTabs().remove(tab);
		}
		return tabs.size();
	}

	public void closeAll() {
		displays.clear();
		getTabs().clear();
	}
}
