/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.theme;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.exception.ThemeFailedLoadException;
import net.jamsimulator.jams.manager.Labeled;
import net.jamsimulator.jams.manager.ThemeManager;
import net.jamsimulator.jams.utils.Validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Represents a JAMS windows theme. Themes contain CSS code used by JavaFX to style nodes.
 * A Theme is represented by a name, this name must be unique.
 * If the theme is loaded from a file, this must contain a specific syntax:
 * <p>
 * - The first line must be the name of the theme.
 * <p>
 * - The other lines must be the CSS code.
 * <p>
 * Example:
 * <p>
 * Test Theme
 * <p>
 * .test {
 * <p>
 * -fx-background-color: red;
 * <p>
 * }
 * <p>
 * You can use {FONT_GENERAL} and {FONT_CODE} as placeholders for the corresponding fonts.
 * JAMS will manage these placeholders automatically.
 */
public class Theme implements Labeled {

    private final String name;
    private String css;

    /**
     * Creates a Theme.
     *
     * @param name the name of the theme.
     * @param css  the css code of the theme.
     */
    public Theme(String name, String css) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(css, "Css cannot be null!");
        this.name = name;
        this.css = css;
    }

    /**
     * Creates a Theme using the contents of the {@link java.io.File file} represented by the given {@link Path}.
     * <p>
     * The contents must have a concrete syntax. See {@link Theme the main documentaiton} for more information.
     *
     * @param path the path representing the {@link java.io.File file} to load.
     * @throws ThemeFailedLoadException whether the theme couldn't be loaded.
     */
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

    /**
     * Creates a Theme using the contents of the given {@link InputStream}.
     * <p>
     * The contents must have a concrete syntax. See {@link Theme the main documentaiton} for more information.
     *
     * @param inputStream the  {@link InputStream}.
     * @throws ThemeFailedLoadException whether the theme couldn't be loaded.
     */
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

    @Override
    public String getName() {
        return name;
    }

    /**
     * Retuns the CCS code of this theme without replacing its placeholders.
     * <p>
     * Use {@link #getFinalCss()} if you want the CSS code with its placeholders replaced.
     *
     * @return the plain CSS code.
     */
    public String getCss() {
        return css;
    }

    /**
     * Returns the CSS code of this theme with its placeholders replaced by the actual data.
     * <p>
     * Use {@link #getCss()} if you want the CSS without its placeholders replaced.
     *
     * @return the modified CSS code.
     */
    public String getFinalCss() {
        return css.replace("{FONT_GENERAL}", JamsApplication.getThemeManager().getGeneralFont())
                .replace("{FONT_CODE}", JamsApplication.getThemeManager().getCodeFont());
    }

    /**
     * Appends the given CSS code to the CSS code of this theme.
     * <p>
     * This CSS code will be placed at the end of the theme's code, so it will have the maximum priority.
     * <p>
     * Nodes containing the CSS of the code WON'T be updated automatically.
     * Use {@link ThemeManager#triggerRefresh()} to refresh all JAMS's nodes.
     *
     * @param css the CSS code to append.
     */
    public void append(String css) {
        this.css += "\n" + css;
    }

    /**
     * Appends the given CSS code to the CSS code of this theme.
     * <p>
     * This CSS code will be placed at the start of the theme's code, so it will have the minimum priority.
     * <p>
     * Nodes containing the CSS of the code WON'T be updated automatically.
     * Use {@link ThemeManager#triggerRefresh()} to refresh all JAMS's nodes.
     *
     * @param css the CSS code to append.
     */
    public void appendAtStart(String css) {
        this.css += css + "\n" + this.css;
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
