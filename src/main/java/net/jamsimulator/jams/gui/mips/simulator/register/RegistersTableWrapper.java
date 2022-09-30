/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.gui.mips.simulator.register;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.LanguageComboBox;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.NumberRepresentation;

import java.util.Comparator;
import java.util.Set;

public class RegistersTableWrapper extends VBox {

    public static final String STYLE_CLASS = "register-table-wrapper";

    private final ComboBox<NumberRepresentation> representationBox;
    private final NumberRepresentation defaultRepresentation;

    public RegistersTableWrapper(Simulation<?> simulation, Set<Register> registers,
                                 NumberRepresentation representation, boolean showSelColumn) {
        getStyleClass().add(STYLE_CLASS);

        representationBox = new LanguageComboBox<>(NumberRepresentation::getLanguageNode);
        representationBox.setPrefWidth(Double.MAX_VALUE);
        defaultRepresentation = representation;
        var table = new RegistersTable(simulation, registers, representation, showSelColumn);

        refreshRepresentationsComboBox();

        var manager = Manager.of(NumberRepresentation.class);
        manager.registerListeners(this, true);

        VBox.setVgrow(table, Priority.ALWAYS);
        getChildren().addAll(representationBox, table);

        representationBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, val) -> table.setRepresentation(val));
    }

    @Listener
    private void onRepresentationRegister(ManagerElementRegisterEvent.After<NumberRepresentation> event) {
        refreshRepresentationsComboBox();
    }

    @Listener
    private void onRepresentationUnregister(ManagerElementUnregisterEvent.After<NumberRepresentation> event) {
        refreshRepresentationsComboBox();
    }

    private void refreshRepresentationsComboBox() {
        var manager = Manager.of(NumberRepresentation.class);
        var representations = manager.stream()
                .sorted(Comparator.comparing(NumberRepresentation::getName))
                .toList();
        representationBox.getItems().setAll(representations);
        representationBox.getSelectionModel().select(defaultRepresentation);
    }

}
