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

package net.jamsimulator.jams.gui.theme;

import javafx.scene.Node;
import javafx.scene.Scene;
import net.jamsimulator.jams.gui.theme.exception.ThemeFailedLoadException;
import net.jamsimulator.jams.utils.TempUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Theme {

	private final String name;
	private final String css;

	public Theme(String name, String css) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(css, "Css cannot be null!");
		this.name = name;
		this.css = css;
	}

	public Theme(Path path) throws ThemeFailedLoadException {
		Validate.notNull(path, "Path cannot be null!");

		try {
			List<String> lines = Files.readAllLines(path);
			if (lines.isEmpty()) throw new ThemeFailedLoadException("File is empty.");
			name = lines.get(0);
			lines.remove(0);

			StringBuilder builder = new StringBuilder();
			lines.forEach(builder::append);
			css = builder.toString();
		} catch (IOException ex) {
			throw new ThemeFailedLoadException(ex);
		}
	}

	public Theme(InputStream inputStream) throws ThemeFailedLoadException {
		Validate.notNull(inputStream, "InputStream cannot be null!");

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			name = reader.readLine();
			if (name == null) throw new ThemeFailedLoadException("InputStream is empty");

			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) builder.append(line);
			css = builder.toString();
			reader.close();
		} catch (IOException e) {
			throw new ThemeFailedLoadException(e);
		}
	}

	public String getName() {
		return name;
	}

	public String getCss() {
		return css;
	}

	public String getFinalCss() {
		return css.replace("{FONT_GENERAL}", "Noto Sans")
				.replace("{FONT_CODE}", "JetBrains Mono");
	}

	public void apply(Node node) {
		node.setStyle(css);
	}

	public void apply(Scene scene) {
		File file = TempUtils.createTemporalFile("currentTheme");
		try {
			Writer writer = new FileWriter(file);
			writer.write(getFinalCss());
			writer.close();

			scene.getStylesheets().setAll(file.toURI().toURL().toExternalForm());

		} catch (IOException e) {
			System.err.println("Error while applying theme " + name + ": ");
			e.printStackTrace();
		}

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Theme theme = (Theme) o;
		return name.equals(theme.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
