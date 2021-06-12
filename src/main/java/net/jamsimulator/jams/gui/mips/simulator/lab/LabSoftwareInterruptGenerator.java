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
