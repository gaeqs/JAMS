package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.beans.property.*;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Optional;

public class InstructionEntry {

	private final Simulation<?> simulation;
	private final int address;
	private int code;

	private final StringProperty addressProperty;
	private final StringProperty originalProperty;

	private BooleanProperty breakpointProperty;
	private StringProperty codeProperty;
	private StringProperty instructionProperty;

	public InstructionEntry(Simulation<?> simulation, int address, String original) {
		this.simulation = simulation;
		this.address = address;
		this.addressProperty = new ReadOnlyStringWrapper("0x" + StringUtils.addZeros(Integer.toHexString(address), 8));
		this.originalProperty = new ReadOnlyStringWrapper(original);

		simulation.getMemory().registerListeners(this, true);
	}

	public int getAddress() {
		return address;
	}

	public StringProperty addressProperty() {
		return addressProperty;
	}

	public StringProperty originalProperty() {
		return originalProperty;
	}

	public BooleanProperty breakpointProperty() {
		if (breakpointProperty == null) {
			breakpointProperty = new SimpleBooleanProperty(null, "breakpoint", false);
			breakpointProperty.addListener((obs, old, val) -> {
				if (val) {
					simulation.getBreakpoints().add(address);
				} else {
					simulation.getBreakpoints().remove(address);
				}
			});
		}
		return breakpointProperty;
	}

	public StringProperty codeProperty() {

		if (codeProperty == null) {
			code = simulation.getMemory().getWord(address, false, true);
			codeProperty = new SimpleStringProperty("0x" + StringUtils.addZeros(Integer.toHexString(code), 8));

			codeProperty.addListener((obs, old, val) -> {
				if (old.equals(val)) return;

				try {
					simulation.getMemory().setWord(address, NumericUtils.decodeInteger(val));
				} catch (NumberFormatException ex) {
					codeProperty.setValue(old);
				}
			});
		}

		return codeProperty;
	}

	public StringProperty instructionProperty() {

		if (instructionProperty == null) {
			codeProperty();
			instructionProperty = new SimpleStringProperty(generateInstructionDisplay());
		}

		return instructionProperty;
	}


	@Listener
	private void onMemoryChange(MemoryWordSetEvent.After event) {
		if (event.getAddress() == address) {
			refresh(event.getValue());
		}
	}

	@Listener
	private void onMemoryChange(MemoryByteSetEvent.After event) {
		if (event.getAddress() >> 2 == address >> 2) {
			refresh(event.getMemory().getWord(address));
		}
	}

	private void refresh(int value) {
		code = value;
		if (codeProperty != null) codeProperty.setValue("0x" + StringUtils.addZeros(Integer.toHexString(code), 8));
		if (instructionProperty != null) instructionProperty.setValue(generateInstructionDisplay());
	}

	private String generateInstructionDisplay() {
		Optional<? extends BasicInstruction<?>> optional =
				simulation.getInstructionSet().getInstructionByInstructionCode(code);
		if (optional.isPresent()) {
			BasicInstruction<?> instruction = optional.get();
			AssembledInstruction assembled = instruction.assembleFromCode(code);
			StringBuilder display = new StringBuilder(instruction.getMnemonic());

			String start = String.valueOf(simulation.getRegisters()
					.getValidRegistersStarts().stream().findFirst().orElse('$'));

			display.append(" ").append(assembled.parametersToString(start));
			return display.toString();
		}
		return "nop";
	}
}
