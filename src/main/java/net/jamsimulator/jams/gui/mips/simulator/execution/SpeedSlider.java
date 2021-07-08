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

package net.jamsimulator.jams.gui.mips.simulator.execution;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * Implementation of the speed slider use to set the execution speed of a simulation.
 * This node consist of a label indicating the milliseconds and a slider the user can
 * move to set the velocity.
 * <p>
 * The velocity is calculated using the next formula:
 * <p>
 * speed = (2^(slider / 5) - 1) * 2000 / 15
 * <p>
 * The slider can take values from 0 to 20 (both inclusive).
 * The speed can take values from 0 to 2000 (both inclusive).
 * <p>
 * If you want to change this formula, override {@link #onValueChange(ObservableValue, Number, Number)}.
 */
public class SpeedSlider extends HBox {

    /**
     * The style class of the HBox containing all elements.
     * Use it to style the HBox, the Label and the Slider.
     */
    public static final String STYLE_CLASS = "speed-slider";

    /**
     * The minimum value the slider can take.
     */
    public static final int MIN = 0;

    /**
     * The maximum value the slider can take.
     */
    public static final int MAX = 20;

    private final Simulation<?> simulation;
    private final Label delayHint;

    /**
     * Creates the node.
     *
     * @param simulation the simulation whose velocity is controller by this node.
     */
    public SpeedSlider(Simulation<?> simulation) {
        this.simulation = simulation;

        getStyleClass().add(STYLE_CLASS);

        var tooltip = new LanguageTooltip(Messages.ACTION_MIPS_SIMULATION_CYCLE_DELAY,
                LanguageTooltip.DEFAULT_DELAY);

        var slider = new Slider(MIN, MAX, MIN);
        slider.setTooltip(tooltip);
        slider.valueProperty().addListener(this::onValueChange);

        delayHint = new Label("0ms");
        delayHint.setTooltip(tooltip);

        getChildren().addAll(delayHint, slider);
    }

    /**
     * Updates the speed of the simulation when the value of the slider changes.
     *
     * @param obs the observable value of the slider.
     * @param old the old value.
     * @param val the new value.
     */
    protected void onValueChange(ObservableValue<? extends Number> obs, Number old, Number val) {
        double normalized = (Math.pow(2, val.doubleValue() / 5) - 1) / 15;
        int delay = (int) (normalized * 2000);
        simulation.setCycleDelay(delay);
        delayHint.setText(delay + "ms");
    }

}
