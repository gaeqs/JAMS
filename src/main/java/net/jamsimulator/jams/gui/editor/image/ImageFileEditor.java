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

package net.jamsimulator.jams.gui.editor.image;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.editor.FileEditor;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTab;
import net.jamsimulator.jams.gui.image.nearest.NearestImageView;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.PixelScrollPane;

import java.net.MalformedURLException;

public class ImageFileEditor extends NearestImageView implements FileEditor {

    private final FileEditorTab tab;

    public ImageFileEditor(FileEditorTab tab) throws MalformedURLException, IllegalArgumentException {
        super(new Image(tab.getFile().toURI().toURL().toString()));
        this.tab = tab;

        focusedProperty().addListener((obs, old, val) -> {
            if (val) {
                getTab().getList().getHolder().setLastFocusedEditor(this);
            }
        });
    }

    @Override
    public FileEditorTab getTab() {
        return tab;
    }

    @Override
    public void onClose() {
    }

    @Override
    public void save() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void addNodesToTab(AnchorPane tabAnchorPane) {
        var scroll = new PixelScrollPane(this);
        tabAnchorPane.getChildren().add(scroll);
        AnchorUtils.setAnchor(scroll, 0, 0, 0, 0);
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.EDITOR_TAB.equals(region);
    }
}
