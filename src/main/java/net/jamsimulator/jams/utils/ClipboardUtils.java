package net.jamsimulator.jams.utils;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClipboardUtils {

	public static void copy(File file) {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");

		Map<DataFormat, Object> contentMap = new HashMap<>();
		List<File> files = new ArrayList<>();
		files.add(file);
		contentMap.put(DataFormat.FILES, files);

		Clipboard.getSystemClipboard().setContent(contentMap);

	}

	public static void cut(File file) {
		Validate.notNull(file, "File cannot be null!");
		Validate.isTrue(file.exists(), "File must exist!");


		Map<DataFormat, Object> contentMap = new HashMap<>();
		List<File> files = new ArrayList<>();
		files.add(file);
		contentMap.put(DataFormat.FILES, files);

		Clipboard.getSystemClipboard().setContent(contentMap);
	}

	public static void paste(File folder) {
		Validate.notNull(folder, "Folder cannot be null!");
		Validate.isTrue(folder.isDirectory(), "Folder must be a directory!");

		Clipboard clipboard = Clipboard.getSystemClipboard();

		if (!clipboard.hasContent(DataFormat.FILES)) return;
		clipboard.getContentTypes().forEach(System.out::println);
		List<File> files = clipboard.getFiles();

		files.forEach(file -> {
			try {
				Path from = file.toPath();
				Path to = new File(folder, file.getName()).toPath();
				if (file.isDirectory()) {
					Files.walkFileTree(from, new CopyFileVisitor(from, to));
				} else {
					Files.copy(from, to, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
