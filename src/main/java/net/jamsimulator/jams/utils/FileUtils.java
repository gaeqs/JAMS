package net.jamsimulator.jams.utils;

import java.io.*;
import java.nio.file.Files;

public class FileUtils {

	public static boolean deleteDirectory(File directory) {
		Validate.notNull(directory, "Directory cannot be null!");
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

	public static String readAll(File file) throws IOException {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");
		Validate.isTrue(file.isFile(), "File must be a file!");

		StringBuilder builder = new StringBuilder();
		Reader reader = new FileReader(file);

		int c;
		while ((c = reader.read()) != -1) {
			builder.append((char) c);
		}

		reader.close();

		return builder.toString();
	}

	public static void writeAll(File file, String text) throws IOException {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(!file.exists() || file.isFile(), "File must not exist or be a file!");
		Writer writer = new FileWriter(file);
		writer.write(text);
		writer.close();
	}

}

