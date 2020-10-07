package net.jamsimulator.jams.mips.syscall.bundle.defaults;

import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.defaults.*;

public class MARSSyscallExecutionBuilderBundle extends SyscallExecutionBuilderBundle {

	public static final String NAME = "MARS";

	public MARSSyscallExecutionBuilderBundle() {
		super(NAME);
		addBuilder(1, SyscallExecutionPrintInteger.NAME);
		addBuilder(2, SyscallExecutionPrintFloat.NAME);
		addBuilder(3, SyscallExecutionPrintDouble.NAME);
		addBuilder(4, SyscallExecutionPrintString.NAME);
		addBuilder(5, SyscallExecutionReadInteger.NAME);
		addBuilder(6, SyscallExecutionReadFloat.NAME);
		addBuilder(7, SyscallExecutionReadDouble.NAME);
		addBuilder(8, SyscallExecutionReadString.NAME);
		addBuilder(9, SyscallExecutionAllocateMemory.NAME);
		addBuilder(10, SyscallExecutionExit.NAME);
		addBuilder(11, SyscallExecutionPrintCharacter.NAME);
		addBuilder(12, SyscallExecutionReadCharacter.NAME);
		addBuilder(13, SyscallExecutionOpenFile.NAME);
		addBuilder(14, SyscallExecutionReadFile.NAME);
		addBuilder(15, SyscallExecutionWriteFile.NAME);
		addBuilder(16, SyscallExecutionCloseFile.NAME);
		addBuilder(17, SyscallExecutionExitWithValue.NAME);

		addBuilder(30, SyscallExecutionSystemTime.NAME);
		addBuilder(32, SyscallExecutionSleep.NAME);
		addBuilder(34, SyscallExecutionPrintHexadecimalInteger.NAME);
		addBuilder(35, SyscallExecutionPrintBinaryInteger.NAME);
		addBuilder(36, SyscallExecutionPrintUnsignedInteger.NAME);
		addBuilder(40, SyscallExecutionSetSeed.NAME);
		addBuilder(41, SyscallExecutionRandomInteger.NAME);
		addBuilder(42, SyscallExecutionRandomRangedInteger.NAME);
		addBuilder(43, SyscallExecutionRandomFloat.NAME);
		addBuilder(44, SyscallExecutionRandomDouble.NAME);
	}
}
