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

package net.jamsimulator.jams.gui.util;


import javafx.scene.control.ScrollPane;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.utils.NumericUtils;

/**
 * This is the skin that makes a {@link PixelScrollPaneSkin} to behave properly.
 *
 * @see PixelScrollPane
 */
public class PixelScrollPaneSkin extends ScrollPaneSkin {

    /**
     * The default increment.
     */
    public static final double DEFAULT_INCREMENT = 30;

    private double increment;

    /**
     * Creates the skin.
     *
     * @param scrollPane the {@link ScrollPane} to handle.
     */
    public PixelScrollPaneSkin(final ScrollPane scrollPane) {
        this(scrollPane, DEFAULT_INCREMENT);
    }

    /**
     * Creates the skin.
     *
     * @param scrollPane the {@link ScrollPane} to handle.
     * @param increment  the amount of pixels to scroll each time.
     */
    public PixelScrollPaneSkin(final ScrollPane scrollPane, double increment) {
        super(scrollPane);
        this.increment = increment;
        filterScrollEvents();
    }

    /**
     * Returns the amount of pixels to scroll each time.
     *
     * @return the amount of pixels.
     */
    public double getIncrement() {
        return increment;
    }

    /**
     * Sets the amount of pixels to scroll each time.
     *
     * @param increment the amount of pixels.
     */
    public void setIncrement(double increment) {
        this.increment = increment;
    }

    /**
     * Returns the increment used to move the vertical scroll bar natively.
     *
     * @return the increment.
     */
    public double getRelativeVerticalIncrement() {
        var pane = (ScrollPane) getNode();
        var region = getRegionToScroll();
        return region == null ? 0 : increment / (region.getHeight() - pane.getViewportBounds().getHeight());
    }

    /**
     * Returns the increment used to move the horizontal scroll bar natively.
     *
     * @return the increment.
     */
    public double getRelativeHorizontalIncrement() {
        var pane = (ScrollPane) getNode();
        var region = getRegionToScroll();
        return region == null ? 0 : increment / (region.getWidth() - pane.getViewportBounds().getWidth());
    }

    private Region getRegionToScroll() {
        var pane = (ScrollPane) getNode();
        var content = pane.getContent();

        Region region = null;
        if (content instanceof Region) {
            region = (Region) content;
        } else if (content instanceof ScalableNode) {
            if (((ScalableNode) content).getNode() instanceof Region) {
                region = (Region) ((ScalableNode) content).getNode();
            }
        }
        return region;
    }

    private void filterScrollEvents() {
        getSkinnable().addEventFilter(ScrollEvent.SCROLL, event -> {

            if (event.getDeltaX() < 0) {
                incrementHorizontal();
            } else if (event.getDeltaX() > 0) {
                decrementHorizontal();
            }

            if (event.getDeltaY() < 0) {
                incrementVertical();
            } else if (event.getDeltaY() > 0) {
                decrementVertical();
            }

            event.consume();
        });
    }

    private void incrementVertical() {
        var v = getVerticalScrollBar();
        v.setValue(NumericUtils.clamp(v.getMin(), v.getValue() + getRelativeVerticalIncrement(), v.getMax()));
    }

    private void decrementVertical() {
        var v = getVerticalScrollBar();
        v.setValue(NumericUtils.clamp(v.getMin(), v.getValue() - getRelativeVerticalIncrement(), v.getMax()));
    }

    private void incrementHorizontal() {
        var h = getHorizontalScrollBar();
        h.setValue(NumericUtils.clamp(h.getMin(), h.getValue() + getRelativeHorizontalIncrement(), h.getMax()));
    }

    private void decrementHorizontal() {
        var h = getHorizontalScrollBar();
        h.setValue(NumericUtils.clamp(h.getMin(), h.getValue() - getRelativeHorizontalIncrement(), h.getMax()));
    }
}
