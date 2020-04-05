package net.jamsimulator.jams.gui.display;

import javafx.scene.control.TabPane;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileDisplayList extends TabPane {

	private WorkingPane workingPane;
	private final List<FileDisplayTab> displays;

	public FileDisplayList(WorkingPane workingPane) {
		this.workingPane = workingPane;
		this.displays = new ArrayList<>();
		setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
	}

	public void setWorkingPane(WorkingPane workingPane) {
		this.workingPane = workingPane;
	}

	public WorkingPane getWorkingPane() {
		return workingPane;
	}

	public Optional<FileDisplayTab> getFileDisplayTab(File file) {
		return displays.stream().filter(target -> target.getFile().equals(file)).findAny();
	}

	public boolean isFileOpen(File file) {
		return displays.stream().anyMatch(target -> target.getFile().equals(file));
	}

	public boolean openFile(File file) {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");
		Validate.isTrue(file.isFile(), "File must be a file!");

		Optional<FileDisplayTab> optional = getFileDisplayTab(file);

		if (optional.isPresent()) {
			getSelectionModel().select(optional.get());
			return false;
		}

		FileDisplayTab tab = new FileDisplayTab(this, file);
		displays.add(tab);
		getTabs().add(tab);
		getSelectionModel().select(tab);
		return true;
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

	void closeFileInternal(FileDisplayTab tab) {
		displays.remove(tab);
	}

	public void closeAll() {
		displays.clear();
		getTabs().clear();
	}
}
