package net.jamsimulator.jams.utils;

import java.io.*;

public class FolderUtils {

	public static final String JAMS_FOLDER_NAME = "JAMS";

	public static File checkMainFolder() {
		String home = System.getProperty("user.home");
		File folder = new File(home, JAMS_FOLDER_NAME);

		if (!folder.exists()) {
			boolean created = folder.mkdirs();
			if (!created) throw new RuntimeException("JAMS couldn't create the main folder!");
			return folder;
		}
		if (!folder.isDirectory()) {
			throw new RuntimeException("Couldn't create main folder. " +
					"A file with the name JAMS has been found in the home folder.");
		}
		return folder;
	}

	public static boolean checkFolder(File folder) {
		if (!folder.exists()) {
			return folder.mkdirs();
		}
		return folder.isDirectory();
	}

	public static boolean moveFromResources(Class<?> clazz, String origin, File target) {
		InputStream in = clazz.getResourceAsStream(origin);

		try {
			if (!target.exists()) {
				boolean created = target.createNewFile();
				if (!created) return false;
			}
			if (target.isDirectory()) return false;

			OutputStream out = new FileOutputStream(target);

			int i;
			while ((i = in.read()) != -1) {
				out.write(i);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
