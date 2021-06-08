package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.scene.layout.VBox;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class LabPane extends VBox {

    public static final String STYLE_CLASS = "lab";

    public LabPane(MIPSSimulation<?> simulation) {
        getStyleClass().add(STYLE_CLASS);
        getChildren().add(new LabSegments(simulation.getMemory()));
        getChildren().add(new LabHexadecimalKeyboard(simulation));
        getChildren().add(new LabSoftwareInterruptGenerator(simulation));
        getChildren().add(new LabHardwareInterruptGenerator(simulation));
        getChildren().add(new LabCounter(simulation));
    }
}
