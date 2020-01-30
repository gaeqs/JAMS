package net.jamsimulator.jams.mips.register;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Registers {

	private Set<Register> registers;
	private Set<Register> coprocessor0Registers;
	private Set<Register> coprocessor1Registers;

	private Register programCounter, highRegister, lowRegister;

	public Registers() {
		registers = new HashSet<>();
		coprocessor0Registers = new HashSet<>();
		coprocessor1Registers = new HashSet<>();

		loadPrincipalRegisters();
		loadCoprocessor0Registers();
		loadCoprocessor1Registers();
		loadEssentialRegisters();
	}

	public Registers(Set<Register> registers, Set<Register> coprocessor0Registers, Set<Register> coprocessor1Registers) {
		this.registers = registers;
		this.coprocessor0Registers = coprocessor0Registers;
		this.coprocessor1Registers = coprocessor1Registers;
		loadEssentialRegisters();
	}


	public Register getProgramCounter() {
		return programCounter;
	}

	public Register getHighRegister() {
		return highRegister;
	}

	public Register getLowRegister() {
		return lowRegister;
	}

	public Optional<Register> getRegister(String name) {
		return registers.stream().filter(target -> target.hasName(name)).findFirst();
	}

	public Optional<Register> getCoprocessor0Register(String name) {
		return coprocessor0Registers.stream().filter(target -> target.hasName(name)).findFirst();
	}

	public Optional<Register> getCoprocessor1Register(String name) {
		return coprocessor1Registers.stream().filter(target -> target.hasName(name)).findFirst();
	}

	private void loadEssentialRegisters() {
		programCounter = new Register(0x00400000, true, "pc");
		highRegister = new Register("hi");
		lowRegister = new Register("lo");
	}


	private void loadPrincipalRegisters() {
		int value = 0;
		registers.add(new Register(0, false, "zero", String.valueOf(value++)));
		registers.add(new Register("at", String.valueOf(value++)));
		registers.add(new Register("v0", String.valueOf(value++)));
		registers.add(new Register("v1", String.valueOf(value++)));
		for (int i = 0; i < 4; i++)
			registers.add(new Register("a" + i, String.valueOf(value++)));
		for (int i = 0; i < 8; i++)
			registers.add(new Register("t" + i, String.valueOf(value++)));
		for (int i = 0; i < 8; i++)
			registers.add(new Register("s" + i, String.valueOf(value++)));
		registers.add(new Register("t8", String.valueOf(value++)));
		registers.add(new Register("t8", String.valueOf(value++)));

		registers.add(new Register("k0", String.valueOf(value++)));
		registers.add(new Register("k1", String.valueOf(value++)));

		registers.add(new Register(0x10008000, true, "gp", String.valueOf(value++)));
		registers.add(new Register(0x7fffeffc, true, "sp", String.valueOf(value++)));
		registers.add(new Register("ra", String.valueOf(value)));
	}

	private void loadCoprocessor0Registers() {
		coprocessor0Registers.add(new Register("8"));
		coprocessor0Registers.add(new Register(0x0000ff11, true, "12"));
		coprocessor0Registers.add(new Register("13"));
		coprocessor0Registers.add(new Register("14"));
	}

	private void loadCoprocessor1Registers() {
		for (int i = 0; i < 32; i++) {
			coprocessor1Registers.add(new Register("f" + i, String.valueOf(i)));
		}


	}
}
