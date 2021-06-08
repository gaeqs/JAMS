package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.configuration.ConfigurationRegionDisplay;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class LabSoftwareInterruptGenerator extends VBox {

    public LabSoftwareInterruptGenerator(MIPSSimulation<?> simulation) {
        getChildren().add(new ConfigurationRegionDisplay(Messages.LAB_SOFTWARE_INTERRUPTS));
        setSpacing(5);

        setFillWidth(true);

        var hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        var comboBox = new ComboBox<InterruptCause>();
        comboBox.getItems().addAll(InterruptCause.values());
        comboBox.getSelectionModel().selectFirst();

        var button = new LanguageButton(Messages.LAB_INTERRUPT);
        button.setOnAction(event -> simulation.requestSoftwareInterrupt(
                new MIPSInterruptException(comboBox.getSelectionModel().getSelectedItem())));
        hbox.getChildren().addAll(comboBox, button);

        hbox.setSpacing(5);
        comboBox.prefWidthProperty().bind(hbox.widthProperty().multiply(0.65));
        button.prefWidthProperty().bind(hbox.widthProperty().multiply(0.35));


        getChildren().add(hbox);
    }

}
