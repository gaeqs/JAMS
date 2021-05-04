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

		file.deleteOnExit();

		return file;
	}


}
