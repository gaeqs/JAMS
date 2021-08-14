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

package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.utils.StringUtils;

/**
 * Represents the header that is showed above the linked {@link CacheMemoryTable}.
 */
public class CacheMemoryHeader extends AnchorPane {

    public CacheMemoryHeader(CacheMemoryTable table) {
        var blockLabel = new LanguageLabel(Messages.MEMORY_CACHE_BLOCK, "{BLOCK}", "");
        var tagLabel = new LanguageLabel(Messages.MEMORY_CACHE_TAG, "{TAG}", "");

        var infoHbox = new HBox();
        infoHbox.getChildren().addAll(blockLabel, tagLabel);

        blockLabel.prefWidthProperty().bind(infoHbox.widthProperty().divide(2));
        tagLabel.prefWidthProperty().bind(infoHbox.widthProperty().divide(2));

        AnchorUtils.setAnchor(infoHbox, 0, -1, 0, 0);
        infoHbox.setPrefHeight(30);
        infoHbox.setSpacing(5);

        getChildren().addAll(infoHbox);

        Runnable update = () -> Platform.runLater(() -> {
            blockLabel.setReplacements(new String[]{"{BLOCK}", String.valueOf(table.getBlock())});

            var block = table.getMemory().getCacheBlock(table.getBlock()).orElse(null);
            if (block == null) {
                tagLabel.setReplacements(new String[]{"{TAG}", "-"});
            } else {
                tagLabel.setReplacements(new String[]{"{TAG}", "0x" + StringUtils.addZeros(Integer.toHexString(block.getTag()), 8)});
            }
        });

        table.afterPopulate(update);
        update.run();
    }

}
