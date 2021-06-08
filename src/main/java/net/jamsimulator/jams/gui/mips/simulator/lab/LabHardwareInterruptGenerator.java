package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.configuration.ConfigurationRegionDisplay;
import net.jamsimulator.jams.gui.util.value.RangedIntegerValueEditor;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class LabHardwareInterruptGenerator extends VBox {

    public LabHardwareInterruptGenerator(MIPSSimulation<?> simulation) {
        getChildren().add(new ConfigurationRegionDisplay(Messages.LAB_HARDWARE_INTERRUPTS));
        setSpacing(5);

        setFillWidth(true);

        var hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        var numberEditor = new RangedIntegerValueEditor();
        numberEditor.setMin(2);
        numberEditor.setMax(63);

        var button = new LanguageButton(Messages.LAB_INTERRUPT);
        button.setOnAction(event -> simulation.requestHardwareInterrupt(numberEditor.getCurrentValue()));
        hbox.getChildren().addAll(numberEditor, button);

        hbox.setSpacing(5);
        numberEditor.prefWidthProperty().bind(hbox.widthProperty().multiply(0.65));
        button.prefWidthProperty().bind(hbox.widthProperty().multiply(0.35));


        getChildren().add(hbox);
    }

}
