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
        button.setOnAction(event -> simulation.requestHardwareInterrupt((int) slider.getValue()));
        getChildren().add(button);
    }

}
