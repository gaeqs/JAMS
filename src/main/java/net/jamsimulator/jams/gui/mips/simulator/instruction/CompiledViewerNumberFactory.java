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

package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.utils.StringUtils;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import java.util.function.IntFunction;

public class CompiledViewerNumberFactory implements IntFunction<Node> {

    private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);
    private static final IconData BREAKPOINT_IMAGE = Icons.SIMULATION_BREAKPOINT;


    private final Val<Integer> nParagraphs;
    private final Rectangle background;

    private final MIPSAssembledCodeViewer viewer;

    public CompiledViewerNumberFactory(MIPSAssembledCodeViewer viewer) {
        this.viewer = viewer;
        nParagraphs = LiveList.sizeOf(viewer.getParagraphs());
        background = new Rectangle(0, 100000);
        background.getStyleClass().add("left-bar-background");
    }

    @Override
    public Node apply(int idx) {
        if (viewer.assembledLines.isEmpty()) return new HBox();
        var formatted = nParagraphs.map(n -> format(idx));

        var lineNo = new Label();
        lineNo.setPadding(DEFAULT_INSETS);
        lineNo.setAlignment(Pos.BOTTOM_RIGHT);
        lineNo.getStyleClass().add("line-number");

        // bind label's text to a Val that stops observing area's paragraphs
        // when lineNo is removed from scene
        lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));

        boolean breakpoint = viewer.assembledLines.get(idx).getAddress().map(address ->
                viewer.getSimulation().hasBreakpoint(address)).orElse(false);

        var image = new QualityImageView(breakpoint ? BREAKPOINT_IMAGE : null, 16, 16);
        var hBox = new HBox(lineNo, image);
        hBox.getStyleClass().add("left-bar");

        if (idx >= 0 && idx < 9) {
            background.widthProperty().bind(hBox.widthProperty());
        }

        hBox.setOnMousePressed(event -> {
            var optional = viewer.assembledLines.get(idx).getAddress();
            if (optional.isEmpty()) return;
            viewer.simulation.toggleBreakpoint(optional.get());

            //The table can't refresh the line if it is being used by another color.
            if (viewer.isLineBeingUsed(idx)) {
                image.setIcon(viewer.getSimulation().hasBreakpoint(optional.get()) ? BREAKPOINT_IMAGE : null);
            }
        });

        return hBox;
    }

    public Rectangle getBackground() {
        return background;
    }

    private String format(int x) {
        if (viewer.assembledLines.size() <= x) return " - ";
        return viewer.assembledLines.get(x).getAddress()
                .map(target -> " 0x" + StringUtils.addZeros(Integer.toHexString(target), 8))
                .orElse("           ");
    }
}
