package net.jamsimulator.jams.utils;

import java.io.File;
import java.nio.file.Files;

public class FileUtils {

	public static boolean deleteDirectory(File directory) {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (!Files.isSymbolicLink(file.toPath())) {
					if (!deleteDirectory(file)) return false;
				}
			}
		}
		return directory.delete();
	}
}
