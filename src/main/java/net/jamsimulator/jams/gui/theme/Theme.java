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

import net.jamsimulator.jams.manager.Labeled;
import net.jamsimulator.jams.manager.ThemeManager;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents a JAMS theme. Theses are piece of CSS codes JavaFX will use to style nodes.
 * You can load new themes using the {@link ThemeManager}.
 * <h2>Folder structure:</h2>
 * <p>
 * Themes will be stored insie a .ZIP file or inside a folder. They are composed by three components:
 * <h3>theme.json:</h3>
 * Contains information about the theme. Inside there's a string 'name' representing the name of the theme
 * and a 'files' list containing all CSS files the theme loader will parse in order.
 * <p>
 * You can also define a 'dependencies' list if you want to use another theme as a base.
 *
 * <p>
 * This file will be represened as a {@link ThemeHeader} inside the theme instance.
 *
 * <h3>global.css</h3>
 * A CSS code that will be placed inside a *{} braket. Useful to define variables used by the common theme.
 * You can also define your own variables and use them in your theme.
 *
 * <h3>CSS files</h3>
 * The css files defined in the 'files' list inside 'theme.json'. These files contain the actual theme's CSS code.
 *
 * <h2>Attachments</h2>
 * <p>
 * PLugins and external sources may attach CSS code to a theme. This is useful if you're making a plugin with
 * new JavaFX nodes, or you simply want to tweak existing themes.
 *
 * <h2>CSS Format:</h2>
 * <p>
 * A theme has the following CSS format:
 * <p>
 * [COMMON THEME]
 * <p>
 * * {
 * <p>
 * [DEPENDENCIES' GLOBAL DATA]
 * <p>
 * [GLOBAL DATA]
 * <p>
 * [GLOBAL ATTACHMENTS]
 * <p>
 * }
 * <p>
 * [DEPENDENCIES FILES DATA]
 * <p>
 * [FILES DATA]
 * <p>
 * [FILES ATTACHMENTS]
 */
public class Theme implements Labeled {

    private final ThemeHeader header;
    private final String globalData;
    private final String filesData;

    private final List<ThemeAttachment> globalAttachments;
    private final List<ThemeAttachment> filesAttachments;

    /**
     * Creates a theme.
     *
     * @param header     the header representing the 'theme.json' file.
     * @param globalData the global CSS code.
     * @param filesData  the merged CSS code in the theme's files.
     */
    public Theme(ThemeHeader header, String globalData, String filesData) {
        Validate.notNull(header, "Header cannot be null!");
        this.header = header;
        this.globalData = globalData == null ? "" : globalData;
        this.filesData = filesData == null ? "" : filesData;

        this.globalAttachments = new LinkedList<>();
        this.filesAttachments = new LinkedList<>();
    }

    /**
     * Returns the {@link ThemeHeader} of this theme.
     * <p>
     * This header contains basic information about the theme.
     *
     * @return the {@link ThemeHeader}.
     */
    public ThemeHeader getHeader() {
        return header;
    }

    /**
     * Returns the name of the theme.
     * This method is equivalent to {@code getHeader().getName()}.
     *
     * @return the name of the theme.
     * @see Labeled#getName()
     */
    @Override
    public String getName() {
        return header.name();
    }

    /**
     * Returns the global data of this theme without attachments or dependencies.
     *
     * @return the global data.
     */
    public String getGlobalData() {
        return globalData;
    }

    /**
     * Returns the global data of this theme with attachments and dependencies.
     *
     * @param manager the {@link ThemeManager} where the dependencies are loccated.
     * @return the final global data.
     */
    public String getFinalGlobalData(ThemeManager manager) {
        return buildFinalGlobalData(manager, new HashSet<>());
    }

    /**
     * Returns the files' data of this theme without attachments or dependencies.
     *
     * @return the files' data.
     */
    public String getFilesData() {
        return filesData;
    }

    /**
     * Returns the files' data of this theme with attachments and dependencies.
     *
     * @param manager the {@link ThemeManager} where the dependencies are loccated.
     * @return the final files' data.
     */
    public String getFinalFilesData(ThemeManager manager) {
        return buildFinalFilesData(manager, new HashSet<>());
    }

    /**
     * Returns a mutable list with all global attachments of this theme.
     * <p>
     * You may like to call {@link ThemeManager#refresh()} after finishing modifications
     * on this list.
     *
     * @return the global attachments.
     */
    public List<ThemeAttachment> getGlobalAttachments() {
        return globalAttachments;
    }

    /**
     * Returns a mutable list with all files' attachments of this theme.
     * <p>
     * You may like to call {@link ThemeManager#refresh()} after finishing modifications
     * on this list.
     *
     * @return the files' attachments.
     */
    public List<ThemeAttachment> getFilesAttachments() {
        return filesAttachments;
    }

    /**
     * Builds the final CSS code ready to use by JavaFX.
     *
     * @param manager     the manager where this theme's dependencies are located.
     * @param buildCommon whether the common theme sould be appended at the start of the code. This must
     *                    be false if the theme to build is the common theme.
     * @return the final CSS code.
     */
    public String build(ThemeManager manager, boolean buildCommon) {
        var builder = new StringBuilder();
        if (buildCommon) builder.append(manager.getDefault().build(manager, false));
        builder.append("*{").append(getFinalGlobalData(manager)).append("}").append(getFinalFilesData(manager));
        return builder.toString()
                .replace("{FONT_GENERAL}", manager.getGeneralFont())
                .replace("{FONT_CODE}", manager.getCodeFont());
    }

    private String buildFinalGlobalData(ThemeManager manager, Set<String> addedThemes) {
        addedThemes.add(header.name());

        var builder = new StringBuilder();

        header.dependencies().stream()
                .filter(it -> !addedThemes.contains(it))
                .forEach(it -> {
                    var theme = manager.get(it);
                    if (theme.isPresent()) {
                        builder.append(theme.get().buildFinalGlobalData(manager, addedThemes));
                    } else {
                        System.err.println("Couldn't find theme dependency " + it + ".");
                    }
                });

        builder.append(globalData);
        globalAttachments.forEach(it -> builder.append(it.data()));
        return builder.toString();
    }

    private String buildFinalFilesData(ThemeManager manager, Set<String> addedThemes) {
        addedThemes.add(header.name());

        var builder = new StringBuilder();

        header.dependencies().stream()
                .filter(it -> !addedThemes.contains(it))
                .forEach(it -> {
                    var theme = manager.get(it);
                    if (theme.isPresent()) {
                        builder.append(theme.get().buildFinalFilesData(manager, addedThemes));
                    } else {
                        System.err.println("Couldn't find theme dependency " + it + ".");
                    }
                });

        builder.append(filesData);
        filesAttachments.forEach(it -> builder.append(it.data()));
        return builder.toString();
    }
}
