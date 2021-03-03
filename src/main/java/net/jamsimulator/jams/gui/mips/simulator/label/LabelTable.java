package net.jamsimulator.jams.gui.mips.simulator.label;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.mips.project.MIPSSimulationPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;
import net.jamsimulator.jams.language.wrapper.LanguageTableColumn;
import net.jamsimulator.jams.utils.StringUtils;

public class LabelTable extends TableView<LabelEntry> {

    private final MIPSSimulationPane simulationPane;

    public LabelTable(MIPSSimulationPane simulationPane) {
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        getStyleClass().add("table-view-horizontal-fit");
        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> JamsApplication.hideContextMenu());

        this.simulationPane = simulationPane;

        var nameColumn = new LanguageTableColumn<LabelEntry, String>(Messages.LABELS_NAME);
        var addressColumn = new LanguageTableColumn<LabelEntry, String>(Messages.LABELS_ADDRESS);

        nameColumn.setEditable(false);
        addressColumn.setEditable(false);

        nameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
        addressColumn.setCellValueFactory(p -> p.getValue().addressProperty());


        getColumns().addAll(nameColumn, addressColumn);

        setOnContextMenuRequested(this::generateContextMenu);

        simulationPane.getSimulation().getData().getLabels().forEach((label, address) ->
                getItems().add(new LabelEntry(label, "0x" +
                        StringUtils.addZeros(Integer.toHexString(address), 8), address)));
    }

    private void generateContextMenu(ContextMenuEvent event) {
        ContextMenu main = new ContextMenu();

        var memory = new LanguageMenuItem(Messages.LABELS_CONTEXT_SHOW_IN_MEMORY);
        var instruction = new LanguageMenuItem(Messages.LABELS_CONTEXT_SHOW_IN_INSTRUCTION);

        memory.setOnAction(e -> selectMemory(getSelectionModel().getSelectedItem().getAddressInt()));
        instruction.setOnAction(e -> selectInstruction(getSelectionModel().getSelectedItem().getAddressInt()));

        main.getItems().addAll(memory, instruction);

        JamsApplication.openContextMenu(main, this, event.getScreenX(), event.getScreenY());
    }

    private void selectInstruction(int address) {
        var instructionGroup = simulationPane.getInstructionTableGroup();

        var match = instructionGroup.getUser().selectAddress(address);
        if (match) {
            instructionGroup.selectUser();
        } else if (instructionGroup.getKernel() != null) {
            match = instructionGroup.getKernel().selectAddress(address);
            if (match) {
                instructionGroup.selectKernel();
            }
        }
    }

    private void selectMemory(int address) {
        simulationPane.getBarMap().searchButton("memory").ifPresent(BarButton::show);
        var memoryPane = simulationPane.getMemoryPane();
        memoryPane.selectAddress(address);
    }

}
