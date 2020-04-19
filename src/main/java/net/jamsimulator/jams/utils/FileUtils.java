package net.jamsimulator.jams.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtils {

	public static boolean isChild(File child, File parent) {
		return isChild(child.toPath().toAbsolutePath(), parent.toPath().toAbsolutePath());
	}

	public static boolean isChild(Path child, Path parent) {
		return child.startsWith(parent);
	}

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

	public static boolean copyFile(File folder, File target) {
		try {
			Path from = target.toPath();
			Path to = new File(folder, target.getName()).toPath();
			if (target.isDirectory()) {
				Files.walkFileTree(from, new CopyFileVisitor(from, to));
			} else {
				Files.copy(from, to, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}

