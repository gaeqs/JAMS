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
import java.util.Set;

public class Theme implements Labeled {

    private final ThemeHeader header;
    private final String globalData;
    private final String filesData;

    private final Set<ThemeAttachment> globalAttachments;
    private final Set<ThemeAttachment> filesAttachments;

    public Theme(ThemeHeader header, String globalData, String filesData) {
        Validate.notNull(header, "Header cannot be null!");
        this.header = header;
        this.globalData = globalData == null ? "" : globalData;
        this.filesData = filesData == null ? "" : filesData;

        this.globalAttachments = new HashSet<>();
        this.filesAttachments = new HashSet<>();
    }

    public ThemeHeader getHeader() {
        return header;
    }

    @Override
    public String getName() {
        return header.name();
    }

    public String getGlobalData() {
        return globalData;
    }

    public String getFinalGlobalData(ThemeManager manager) {
        return buildFinalGlobalData(manager, new HashSet<>());
    }

    public String getFilesData() {
        return filesData;
    }

    public String getFinalFilesData(ThemeManager manager) {
        return buildFinalFilesData(manager, new HashSet<>());
    }

    public Set<ThemeAttachment> getGlobalAttachments() {
        return globalAttachments;
    }

    public Set<ThemeAttachment> getFilesAttachments() {
        return filesAttachments;
    }

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
