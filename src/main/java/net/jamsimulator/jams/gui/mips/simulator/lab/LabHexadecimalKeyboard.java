package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Locale;

public class LabHexadecimalKeyboard extends GridPane {

    private static final int BASE_ADDRESS = 0xFFFF0013;
    private final HexButton[] buttons = new HexButton[16];

    private final Simulation<?> simulation;

    public LabHexadecimalKeyboard(Simulation<?> simulation) {
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
            simulation.getMemory().setByte(BASE_ADDRESS + (upperByte ? 1 : 0), generateByte(upperByte));
            simulation.requestHardwareInterrupt(2);
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
