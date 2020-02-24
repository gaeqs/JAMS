package net.jamsimulator.jams.gui.project;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Represents a file explorer.
 * A file explorer is a VBox containing all {@link ExplorerPaneFile}
 * representing the main folder's files.
 */
public class ExplorerPane extends VBox {

	private File mainFolder;
	private ExplorePaneFolder explorerMainFolder;

	//EXPLORER WATCHER
	private WatchService watchService;
	private boolean runnning;

	/**
	 * Creates an explorer using the main folder.
	 *
	 * @param mainFolder the main folder.
	 * @throws NullPointerException     whether the main folder is null.
	 * @throws IllegalArgumentException whether the main folder is not a folder.
	 */
	public ExplorerPane(File mainFolder) {
		Validate.notNull(mainFolder, "Main folder cannot be null!");
		Validate.isTrue(mainFolder.isDirectory(), "Main folder is not a folder!");
		this.mainFolder = mainFolder;
		explorerMainFolder = addFiles(mainFolder, 0, null);
		watchFolder();
	}

	/**
	 * Returns the main folder of this explorer.
	 *
	 * @return the main folder.
	 */
	public File getMainFolder() {
		return mainFolder;
	}

	public Optional<ExplorerPaneFile> getFile(File file) {
		return getChildren().stream().filter(target -> target instanceof ExplorerPaneFile)
				.map(target -> (ExplorerPaneFile) target)
				.filter(target -> target.file.equals(file)).findFirst();
	}

	public Optional<ExplorerPaneFile> getBaseFile(String name) {
		return getChildren().stream().filter(target -> target instanceof ExplorerPaneFile)
				.map(target -> (ExplorerPaneFile) target)
				.filter(target -> target.getHierarchyLevel() == 1
						&& target.getName().equals(name)).findFirst();
	}

	/**
	 * Kills the file watcher.
	 */
	public void kill() {
		runnning = false;
		System.out.println("KILL");
	}

	/**
	 * Returns the index of the given file inside this explorer.
	 *
	 * @param file the given file.
	 * @return the index.
	 * @see java.util.List#indexOf(Object)
	 */
	int indexOf(ExplorerPaneFile file) {
		return getChildren().indexOf(file);
	}


	/**
	 * Adds the given folder and its children into the explorer.
	 *
	 * @param folder       the given folder.
	 * @param level        the hierarchy level. If the given folder is the main folder this parameter must be 0.
	 * @param folderParent the parent of the given folder. Null if the given folder is the main folder.
	 * @return the main folder.
	 */
	private ExplorePaneFolder addFiles(File folder, int level, ExplorePaneFolder folderParent) {
		File[] files = folder.listFiles();
		if (files == null) return null;

		//Create the parent instance.
		ExplorePaneFolder parent = new ExplorePaneFolder(this, level, folder, folderParent);
		if (folderParent != null) {
			folderParent.getFiles().add(parent);
		}
		getChildren().add(parent);

		level++;
		for (File file : files) {
			//If the file is a directory, call this method again using the folder.
			if (file.isDirectory()) {
				addFiles(file, level, parent);
			}
			//Else, add the file into the parent.
			else {
				ExplorerPaneFile explorerFile = new ExplorerPaneFile(this, level, file, parent);
				parent.getFiles().add(explorerFile);
				getChildren().add(explorerFile);
			}
		}
		return parent;
	}

	private void addFile(File file, String relativePath) {
		String[] folders = relativePath.split(File.separator);

		if (folders.length == 1) {
			if (file.isDirectory())
				addFiles(file, 1, explorerMainFolder);
			else
				getChildren().add(new ExplorerPaneFile(this, 1, file, explorerMainFolder));
			return;
		}

		ExplorerPaneFile parent = getBaseFile(folders[0]).orElse(null);
		if (parent == null) {
			parent = new ExplorerPaneFile(this, 1, file, explorerMainFolder);
			getChildren().add(parent);
		}
		if (!(parent instanceof ExplorePaneFolder))
			throw new IllegalStateException("The file " + folders[0] + " should be a folder!");

		ExplorerPaneFile explorerFile;
		for (int i = 1; i < folders.length - 1; i++) {
			String name = folders[i];
			explorerFile = ((ExplorePaneFolder) parent).getFiles().stream()
					.filter(target -> target.getName().equals(name)).findFirst().orElse(null);

			if (explorerFile == null) {
				explorerFile = new ExplorerPaneFile(this, i + 1, file, (ExplorePaneFolder) parent);
				getChildren().add(explorerFile);
			}
			if (!(explorerFile instanceof ExplorePaneFolder))
				throw new IllegalStateException("The file " + name + " should be a folder!");
			parent = explorerFile;
		}

		if (file.isDirectory())
			addFiles(file, folders.length, (ExplorePaneFolder) parent);
		else
			getChildren().add(new ExplorerPaneFile(this, folders.length, file, (ExplorePaneFolder) parent));
	}

	private void watchFolder() {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			registerRecursive(mainFolder.toPath());

			runnning = true;
			new Thread(() -> {
				while (runnning) {
					try {
						runnning &= processKey(watchService.take());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void registerRecursive(final Path root) throws IOException {
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private boolean processKey(WatchKey key) {
		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();
			if (kind == OVERFLOW) continue;

			WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;

			Path dir = (Path) key.watchable();
			Path path = dir.resolve(watchEvent.context());
			System.out.println(kind.name() + " -> " + path);
			File file = path.toFile();

			if (kind == ENTRY_DELETE) {
				getFile(file).ifPresent(target -> Platform.runLater(() -> target.remove(true)));
			} else if (kind == ENTRY_CREATE) {
				String relative = mainFolder.toPath().relativize(path).toString();
				Platform.runLater(() -> addFile(file, relative));
			}

		}
		return key.reset();
	}

}
