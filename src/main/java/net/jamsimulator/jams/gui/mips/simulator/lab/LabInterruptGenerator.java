package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class LabInterruptGenerator extends VBox {

    public LabInterruptGenerator(MIPSSimulation<?> simulation) {
        setAlignment(Pos.CENTER);
        generateSoftwareInterruptManager(simulation);
        generateHardwareInterruptManager(simulation);
    }


    private void generateSoftwareInterruptManager(MIPSSimulation<?> simulation) {
        var comboBox = new ComboBox<InterruptCause>();
        comboBox.getItems().addAll(InterruptCause.values());
        comboBox.getSelectionModel().selectFirst();
        getChildren().add(comboBox);

        var button = new Button("Interrupt");
        button.setOnAction(event -> simulation.requestSoftwareInterrupt(
                new MIPSInterruptException(comboBox.getSelectionModel().getSelectedItem())));
        getChildren().add(button);
    }


    private void generateHardwareInterruptManager(MIPSSimulation<?> simulation) {
        var slider = new Slider(2, 63, 2);
        getChildren().add(slider);

        var button = new Button("Interrupt");
        button.setOnAction(event -> simulation.requestHardwareInterrupt((int)slider.getValue()));
        getChildren().add(button);
    }

}
