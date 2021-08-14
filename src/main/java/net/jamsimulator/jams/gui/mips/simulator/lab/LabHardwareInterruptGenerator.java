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
