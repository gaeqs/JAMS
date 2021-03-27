package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class LabPane extends TabPane {

    public LabPane(Simulation<?> simulation) {
        var segmentsTab = new Tab("Segments");

        var vbox = new VBox();
        vbox.getChildren().addAll(new LabSegments(simulation.getMemory()), new LabHexadecimalKeyboard(simulation));
        vbox.setFillWidth(true);
        var scroll = new PixelScrollPane(vbox);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);

        segmentsTab.setContent(scroll);
        getTabs().add(segmentsTab);

        var interruptTab = new Tab("Interrupts");
        interruptTab.setContent(new LabInterruptGenerator(simulation));
        getTabs().add(interruptTab);
    }
}
