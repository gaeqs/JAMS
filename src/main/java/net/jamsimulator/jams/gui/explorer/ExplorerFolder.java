package net.jamsimulator.jams.gui.explorer;

import javafx.application.Platform;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Represents a folder inside an {@link Explorer}.
 */
public class ExplorerFolder extends VBox {

	public static final int SPACING = 1;

	private Explorer explorer;
	private ExplorerFolder parent;

	private File folder;
	private ExplorerFolderRepresentation representation;

	private List<ExplorerFolder> folders;
	private List<ExplorerFile> files;

	private WatchService service;
	private boolean serviceRunning;

	private VBox contents;
	private boolean expanded;

	//HIERARCHY
	private int hierarchyLevel;

	/**
	 * Creates the explorer folder.
	 *
	 * @param explorer       the {@link Explorer} of this folder.
	 * @param parent         the {@link ExplorerFolder} containing this folder. This may be null.
	 * @param folder         the folder to represent.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ExplorerFolder(Explorer explorer, ExplorerFolder parent, File folder, int hierarchyLevel) {
		this.explorer = explorer;
		this.parent = parent;
		this.folder = folder;
		this.hierarchyLevel = hierarchyLevel;

		representation = new ExplorerFolderRepresentation(this, hierarchyLevel);
		folders = new ArrayList<>();
		files = new ArrayList<>();

		contents = new VBox();
		expanded = false;

		setSpacing(SPACING);
		contents.setSpacing(SPACING);

		loadChildren();
		loadElements();
		loadWatcher();
		refreshAllFilesAndFolders(true, true);

		setOnContextMenuRequested(request -> {
			explorer.setSelectedElement(representation);
			explorer.createContextMenu(this).
					show(this, request.getScreenX(), request.getScreenY());
			request.consume();
		});

		explorer.widthProperty().addListener((target, old, val) -> {
			setPrefWidth(val.doubleValue());
		});
	}

	/**
	 * Returns the {@link Explorer} of this folder.
	 *
	 * @return the {@link Explorer}.
	 */
	public Explorer getExplorer() {
		return explorer;
	}

	/**
	 * Returns the {@link ExplorerFolder} containing this folder.
	 * This value is null when this folder is the root folder.
	 *
	 * @return the {@link ExplorerFolder}.
	 */
	public ExplorerFolder getParentFolder() {
		return parent;
	}

	/**
	 * Returns the represented folder.
	 *
	 * @return the represented folder.
	 */
	public File getFolder() {
		return folder;
	}

	/**
	 * Returns the hierarchy level.
	 *
	 * @return the hierarchy level.
	 */
	public int getHierarchyLevel() {
		return hierarchyLevel;
	}

	/**
	 * Returns whether this folder is expanded.
	 * If a explorer folder is expanded all its files will be shown on the {@link Explorer}.
	 *
	 * @return whether this folder is expanded.
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Contracts the folder if this is expanded, removing all its files from the {@link Explorer} view.
	 * <p>
	 * This also contracts children explorer folders.
	 */
	public void contract() {
		if (!expanded) return;
		contents.getChildren().clear();
		expanded = false;

		folders.forEach(ExplorerFolder::contract);

		representation.refreshStatusIcon();
	}

	/**
	 * Contrasts the folder if this is contracted, adding all its files from the {@link Explorer} view.
	 */
	public void expand() {
		if (expanded) return;
		addAllFilesToContents();
		expanded = true;
		representation.refreshStatusIcon();
	}

	/**
	 * Contracts or expands the folder depending the whether the folder is contracted or expanded.
	 *
	 * @see #isExpanded()
	 * @see #contract()
	 * @see #expand()
	 */
	public void expandOrContract() {
		if (expanded) contract();
		else expand();
	}

	/**
	 * Kills the {@link WatchService} of this folder. This prevents this folder
	 * from receiving alerts from changes inside the folder.
	 * <p>
	 * This method should be used when the folder is not longer used.
	 *
	 * @throws IOException any exception thrown by {@link WatchService#close()}.
	 */
	public void killWatchService() throws IOException {
		serviceRunning = false;
		service.close();

		for (ExplorerFolder explorerFolder : folders) {
			explorerFolder.killWatchService();
		}
	}

	/**
	 * Adds the given file into this folder explorer.
	 * <p>
	 * This method is used by the {@link WatchService}.
	 *
	 * @param file the file.
	 */
	public void add(File file) {
		if (file.isDirectory()) {
			folders.add(new ExplorerFolder(explorer, this, file, hierarchyLevel + 1));
			refreshAllFilesAndFolders(false, true);
		} else {
			files.add(new ExplorerFile(this, file, hierarchyLevel + 1));
			refreshAllFilesAndFolders(true, false);
		}
	}


	/**
	 * Removes the given file from this folder explorer.
	 * <p>
	 * This method is used by the {@link WatchService}.
	 *
	 * @param file the file.
	 */
	public void remove(File file) {
		//Tries to remove a folder. If the folder doesn't exist, tries to remove a file.
		ExplorerFolder folder = folders.stream().filter(target ->
				target.getFolder().equals(file)).findFirst().orElse(null);
		if (folder != null) {
			folders.remove(folder);
			try {
				folder.killWatchService();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			refreshAllFilesAndFolders(false, true);
		} else {
			if (files.removeIf(target -> target.getFile().equals(file)))
				refreshAllFilesAndFolders(true, false);
		}
	}

	private void loadChildren() {
		File[] folderFiles = folder.listFiles();
		if (folderFiles == null) return;

		for (File file : folderFiles) {
			if (file.isDirectory())
				folders.add(new ExplorerFolder(explorer, this, file, hierarchyLevel + 1));
			else if (file.isFile())
				files.add(new ExplorerFile(this, file, hierarchyLevel + 1));
		}
	}

	private void loadElements() {
		getChildren().clear();
		getChildren().add(representation);
		getChildren().add(contents);
	}

	private void refreshAllFilesAndFolders(boolean sortFiles, boolean sortFolders) {
		//Clears, sorts and adds the files.
		contents.getChildren().clear();

		if (sortFiles)
			files.sort(Comparator.comparing(o -> o.getFile().getName()));
		if (sortFolders)
			folders.sort(Comparator.comparing(o -> o.getFolder().getName()));

		if (expanded) addAllFilesToContents();
	}

	private void addAllFilesToContents() {
		contents.getChildren().addAll(folders);
		contents.getChildren().addAll(files);
	}

	private void loadWatcher() {
		//Loads the watch service.
		try {
			service = FileSystems.getDefault().newWatchService();
			folder.toPath().register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

			serviceRunning = true;

			new Thread(() -> {
				//While the folder is not killed, run the service.
				while (serviceRunning) {
					try {
						serviceRunning &= processKey(service.take());
					} catch (ClosedWatchServiceException ignore) {
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}).start();


		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private boolean processKey(WatchKey key) {
		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();
			if (kind == OVERFLOW) continue;

			WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;

			Path dir = (Path) key.watchable();
			Path path = dir.resolve(watchEvent.context());
			File file = path.toFile();

			if (kind == ENTRY_DELETE) {
				Platform.runLater(() -> remove(file));
			} else if (kind == ENTRY_CREATE) {
				Platform.runLater(() -> add(file));
			}
		}
		return key.reset();
	}
}
