package net.jamsimulator.jams.utils;

import net.jamsimulator.jams.Jams;

import java.io.File;
import java.io.IOException;

public class TempUtils {

	private static final String TEMPORAL_FOLDER = "temp";
	private static File folder = null;

	public static void loadTemporalFolder() {
		if (folder != null) return;
		folder = new File(Jams.getMainFolder(), TEMPORAL_FOLDER);

		if (!FolderUtils.checkFolder(folder))
			throw new RuntimeException("Couldn't create temporal folder!");
	}

	public static File createTemporalFile(String name) {
		File file = new File(folder, name);
		if (file.exists()) {
			boolean delete = file.delete();
			if (!delete) throw new RuntimeException("Old temporal file " + name + " couldn't be deleted");
		}

		try {
			boolean created = file.createNewFile();
			if (!created) throw new RuntimeException("Couldn't create temporal file " + name + "!");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}


}
