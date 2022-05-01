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

package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

import java.util.Locale;

public class LabHexadecimalKeyboard extends GridPane {

    public static final String CONFIG_NODE_1 = "simulation.mips.lab_hex_kerboard_1_address";
    public static final String CONFIG_NODE_2 = "simulation.mips.lab_hex_kerboard_2_address";

    private final HexButton[] buttons = new HexButton[16];

    private final MIPSSimulation<?> simulation;

    public LabHexadecimalKeyboard(MIPSSimulation<?> simulation) {
        this.simulation = simulation;

        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                int value = x + (y << 2);
                buttons[value] = new HexButton(value);
                add(buttons[value], x, y);
            }
        }
    }

    public void refresh(boolean upperByte) {
        simulation.runSynchronized(() -> {
            var node = upperByte ? CONFIG_NODE_2 : CONFIG_NODE_1;
            var number = Jams.getMainConfiguration().data().getNumber(node).orElse(0);
            simulation.getMemory().setByte(number.intValue(), generateByte(upperByte));
            simulation.requestHardwareInterrupt(3);
        });
    }

    private byte generateByte(boolean upperByte) {
        byte b = 0;
        for (int i = 7; i >= 0; i--) {
            b <<= 1;
            b += buttons[i + (upperByte ? 8 : 0)].isSelected() ? 1 : 0;
        }
        return b;
    }

    private class HexButton extends ToggleButton {

        public HexButton(int value) {
            super(Integer.toHexString(value).toUpperCase(Locale.ROOT));
            setPrefSize(50, 50);

            selectedProperty().addListener((obs, old, val) -> refresh(value > 7));
        }

    }

}
