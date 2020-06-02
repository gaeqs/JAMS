/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.utils;

import java.io.*;
import java.nio.file.*;

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

	public static boolean isValidPath(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}
		return true;
	}
}

