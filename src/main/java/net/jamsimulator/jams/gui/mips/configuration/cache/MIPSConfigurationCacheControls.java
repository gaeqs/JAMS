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

package net.jamsimulator.jams.gui.mips.configuration.cache;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;


public class MIPSConfigurationCacheControls extends AnchorPane {

    public static final String STYLE_CLASS = "controls";
    public static final String BUTTONS_STYLE_CLASS = "buttons";

    private final MIPSConfigurationDisplayCacheTab cacheTab;
    private final HBox buttonsHbox;

    public MIPSConfigurationCacheControls(MIPSConfigurationDisplayCacheTab cacheTab) {
        this.cacheTab = cacheTab;

        getStyleClass().add(STYLE_CLASS);

        buttonsHbox = new HBox();
        buttonsHbox.getStyleClass().add(BUTTONS_STYLE_CLASS);
        AnchorUtils.setAnchor(buttonsHbox, 0, 0, 0, -1);
        buttonsHbox.setAlignment(Pos.CENTER_LEFT);
        getChildren().add(buttonsHbox);

        populate();
    }


    private void populate() {
        generateAddButton();
        generateRemoveButton();
    }

    private void generateAddButton() {
        var button = new Button(null, new QualityImageView(Icons.CONTROL_ADD, 16, 16));
        button.setTooltip(new LanguageTooltip(Messages.GENERAL_ADD));
        button.getStyleClass().add("button-bold");

        button.setOnAction(event -> {
            var builder = Manager.of(CacheBuilder.class).stream().findAny()
                    .map(CacheBuilder::makeNewInstance).orElse(null);

            cacheTab.getConfiguration().getCacheBuilders().add(builder);
            cacheTab.getContents().add(builder);
        });

        buttonsHbox.getChildren().add(button);
    }


    private void generateRemoveButton() {
        var button = new Button(null, new QualityImageView(Icons.CONTROL_REMOVE, 16, 16));
        button.setTooltip(new LanguageTooltip(Messages.GENERAL_REMOVE));
        button.getStyleClass().add("button-bold");

        button.setOnAction(event -> {
            var contents = cacheTab.getContents();
            var selected = contents.getSelectedElements();

            if (selected.isEmpty()) return;
            for (ExplorerElement element : selected) {
                if (!(element instanceof MIPSConfigurationCacheContents.Representation)) continue;

                var previous = element.getPrevious();
                contents.remove((MIPSConfigurationCacheContents.Representation) element);
                cacheTab.getConfiguration().getCacheBuilders()
                        .remove(((MIPSConfigurationCacheContents.Representation) element).getIndex());

                if (previous.isPresent()) {
                    contents.selectElementAlone(previous.get());
                } else {
                    contents.getMainSection().getElementByIndex(0).ifPresent(contents::selectElementAlone);

                    if (contents.getMainSection().isEmpty()) {
                        cacheTab.display(null);
                    }
                }
            }
        });

        buttonsHbox.getChildren().add(button);
    }

}
