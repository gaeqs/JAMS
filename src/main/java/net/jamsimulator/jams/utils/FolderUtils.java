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
