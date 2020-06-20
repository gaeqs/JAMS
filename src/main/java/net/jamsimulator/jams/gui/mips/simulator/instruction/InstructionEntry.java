package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.memory.event.ByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.WordSetEvent;
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

	private StringProperty codeProperty;
	private StringProperty instructionProperty;

	public InstructionEntry(Simulation<?> simulation, int address, String original) {
		this.simulation = simulation;
		this.address = address;
		this.addressProperty = new ReadOnlyStringWrapper("0x" + Integer.toHexString(address));
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

	public StringProperty codeProperty() {

		if (codeProperty == null) {
			code = simulation.getMemory().getWord(address);
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
	private void onMemoryChange(WordSetEvent.After event) {
		if (event.getAddress() == address) {
			refresh(event.getValue());
		}
	}

	@Listener
	private void onMemoryChange(ByteSetEvent.After event) {
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

			String start = String.valueOf(simulation.getRegisterSet()
					.getValidRegistersStarts().stream().findFirst().orElse('$'));

			display.append(" ").append(assembled.parametersToString(start));
			return display.toString();
		}
		return "nop";
	}
}
