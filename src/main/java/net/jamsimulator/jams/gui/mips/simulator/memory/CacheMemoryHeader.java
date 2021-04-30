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
