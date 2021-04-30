package net.jamsimulator.jams.gui.mips.simulator.label;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.mips.project.MIPSSimulationPane;

import java.util.Comparator;
import java.util.HashMap;

public class LabelTable extends Explorer {

    public static final String STYLE_CLASS = "label-table";

    private final MIPSSimulationPane simulationPane;

    public LabelTable(ScrollPane scrollPane, MIPSSimulationPane simulationPane) {
        super(scrollPane, false, true);
        getStyleClass().add(STYLE_CLASS);
        this.simulationPane = simulationPane;

        var labels = simulationPane.getSimulation().getData().getLabels();
        var files = new HashMap<String, LabelTableFile>();
        labels.forEach(label -> {
            var section = files.computeIfAbsent(label.getOriginFile(),
                    key -> {
                        var target = new LabelTableFile(this, mainSection, key, 0);
                        mainSection.addElement(target);
                        return target;
                    });
            section.addElement(new LabelTableLabel(this, section, label, 1));
        });
    }

    public MIPSSimulationPane getSimulationPane() {
        return simulationPane;
    }

    @Override
    protected void generateMainSection() {
        mainSection = new ExplorerSection(this, null,
                "main", 0, Comparator.comparing(ExplorerElement::getVisibleName));
        hideMainSectionRepresentation();
        getChildren().add(mainSection);
    }

    //    private void generateContextMenu(ContextMenuEvent event) {
//        ContextMenu main = new ContextMenu();
//
//        var memory = new LanguageMenuItem(Messages.LABELS_CONTEXT_SHOW_IN_MEMORY);
//        var instruction = new LanguageMenuItem(Messages.LABELS_CONTEXT_SHOW_IN_INSTRUCTION);
//
//        memory.setOnAction(e -> selectMemory(getSelectionModel().getSelectedItem().getAddressInt()));
//        instruction.setOnAction(e -> selectInstruction(getSelectionModel().getSelectedItem().getAddressInt()));
//
//        main.getItems().addAll(memory, instruction);
//
//        JamsApplication.openContextMenu(main, this, event.getScreenX(), event.getScreenY());
//    }
//
//    private void selectInstruction(int address) {
//        var instructionGroup = simulationPane.getInstructionTableGroup();
//
//        var match = instructionGroup.getUser().selectAddress(address);
//        if (match) {
//            instructionGroup.selectUser();
//        } else if (instructionGroup.getKernel() != null) {
//            match = instructionGroup.getKernel().selectAddress(address);
//            if (match) {
//                instructionGroup.selectKernel();
//            }
//        }
//    }
//

}
