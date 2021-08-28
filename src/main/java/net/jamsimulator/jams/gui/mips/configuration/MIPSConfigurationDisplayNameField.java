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

package net.jamsimulator.jams.gui.mips.configuration;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.event.MIPSSimulationConfigurationRefreshEvent;

public class MIPSConfigurationDisplayNameField extends HBox {

    public static final String STYLE_CLASS = "name-field";

    public MIPSConfigurationDisplayNameField(MIPSConfigurationWindow window, MIPSSimulationConfiguration configuration) {
        getStyleClass().add(STYLE_CLASS);
        var data = window.getProjectData();
        var label = new LanguageLabel(Messages.SIMULATION_CONFIGURATION_NAME);
        var field = new TextField(configuration.getName());

        EventHandler<ActionEvent> handler = event -> {
            if (data.getConfigurations().stream().anyMatch(target -> target.getName().equals(field.getText()))) {
                field.setText(configuration.getName());
                return;
            }
            configuration.setName(field.getText());
            window.getList().getContents().refreshName(configuration);
            window.getProjectData().callEvent(new MIPSSimulationConfigurationRefreshEvent());
        };

        field.setOnAction(handler);
        field.focusedProperty().addListener((obs, old, val) -> {
            if (!val) handler.handle(null);
        });

        field.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty()).subtract(30));

        getChildren().addAll(label, field);
    }

}
