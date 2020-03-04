package net.jamsimulator.jams.gui.explorer.folder;

import javafx.application.Platform;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.ExplorerSectionRepresentation;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

import static java.nio.file.StandardWatchEventKinds.*;

public class ExplorerFolder extends ExplorerSection {

	private File folder;
	protected WatchService service;
	protected boolean serviceRunning;

	/**
	 * Creates the explorer folder.
	 *
	 * @param explorer       the {@link Explorer} of this folder.
	 * @param parent         the {@link ExplorerSection} containing this folder. This may be null.
	 * @param folder         the folder to represent.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ExplorerFolder(Explorer explorer, ExplorerSection parent, File folder, int hierarchyLevel) {
		super(explorer, parent, folder.getName(), hierarchyLevel, ElementsComparator.INSTANCE);
		this.folder = folder;
		loadChildren();
		loadWatcher();
		refreshAllElements();
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

		for (ExplorerElement element : elements) {
			if (element instanceof ExplorerFolder)
				((ExplorerFolder) element).killWatchService();
		}
	}

	/**
	 * Adds the given file into this folderº explorer.
	 * <p>
	 * This method is used by the {@link WatchService}.
	 *
	 * @param file the file.
	 */
	public void add(File file) {
		if (file.isDirectory()) {
			elements.add(new ExplorerFolder(explorer, this, file, hierarchyLevel + 1));
		} else {
			elements.add(new ExplorerFile(this, file, hierarchyLevel + 1));
		}
		refreshAllElements();
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
		ExplorerFolder folder = (ExplorerFolder) elements.stream().filter(target ->
				target instanceof ExplorerFolder && ((ExplorerFolder) target).folder.equals(file))
				.findFirst().orElse(null);
		if (folder != null) {
			elements.remove(folder);
			try {
				folder.killWatchService();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			refreshAllElements();
		} else {
			if (elements.removeIf(target ->
					target instanceof ExplorerFile && ((ExplorerFile) target).getFile().equals(file)))
				refreshAllElements();
		}
	}

	private void loadChildren() {
		File[] folderFiles = folder.listFiles();
		if (folderFiles == null) return;

		for (File file : folderFiles) {
			if (file.isDirectory())
				elements.add(new ExplorerFolder(explorer, this, file, hierarchyLevel + 1));
			else if (file.isFile())
				elements.add(new ExplorerFile(this, file, hierarchyLevel + 1));
		}
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

	@Override
	protected ExplorerSectionRepresentation loadRepresentation() {
		return new ExplorerFolderRepresentation(this, hierarchyLevel);
	}

	private static class ElementsComparator implements Comparator<ExplorerElement> {

		public static final ElementsComparator INSTANCE = new ElementsComparator();

		private ElementsComparator() {
		}

		@Override
		public int compare(ExplorerElement o1, ExplorerElement o2) {
			if (o1 instanceof ExplorerFolder && !(o2 instanceof ExplorerFolder))
				return -1;
			if (!(o1 instanceof ExplorerFolder) && o2 instanceof ExplorerFolder)
				return 1;
			return o1.getName().compareTo(o2.getName());
		}
	}
}
