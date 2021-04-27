package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.label.LabelReference;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.utils.InstructionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class InstructionSnapshot {

    private final int line, address;
    private final String raw, original;
    private final String labelSufix;


    private List<String> parameters;
    private Instruction instruction;

    public InstructionSnapshot(int line, int address, String raw, String original, String labelSufix) {
        this.address = address;
        this.line = line;
        this.raw = raw;
        this.original = original;
        this.labelSufix = labelSufix;
    }

    public int scan(MIPS32Assembler assembler) {
        assembler.addOriginalInstruction(line, address, original);
        return decode(assembler);
    }

    public void assemble(MIPS32AssemblingFile file) {
        ParameterParseResult[] parameters = assembleParameters(file);

        try {
            Assembler assembler = file.getAssembler();
            AssembledInstruction[] assembledInstructions =
                    instruction.assemble(assembler.getInstructionSet(), address, parameters);

            //Add instructions to memory
            int relativeAddress = address;
            for (AssembledInstruction assembledInstruction : assembledInstructions) {
                assembler.getMemory().setWord(relativeAddress, assembledInstruction.getCode(), false, true, true);
                relativeAddress += 4;
            }
        } catch (AssemblerException ex) {
            throw new AssemblerException(line, "Error while assembling instruction.", ex);
        }
    }

    private int decode(MIPS32Assembler assembler) {
        int mnemonicIndex = raw.indexOf(' ');
        int tabIndex = raw.indexOf("\t");
        if (mnemonicIndex == -1) mnemonicIndex = tabIndex;
        else if (tabIndex != -1) mnemonicIndex = Math.min(mnemonicIndex, tabIndex);

        String mnemonic;
        String parameters;
        if (mnemonicIndex == -1) {
            mnemonic = raw;
            parameters = "";
        } else {
            mnemonic = raw.substring(0, mnemonicIndex);
            parameters = this.raw.substring(mnemonicIndex + 1).trim();
        }

        var instructions = assembler.getInstructionSet().getInstructionByMnemonic(mnemonic);
        return scanInstruction(assembler.getRegisters(), instructions, parameters, mnemonic);
    }

    private int scanInstruction(Registers registers, Set<Instruction> instructions, String rawParameters, String mnemonic) {
        parameters = new LinkedList<>();

        var parametersReference = new AtomicReference<List<String>>();
        instruction = InstructionUtils.getBestInstruction(instructions, parametersReference, registers, rawParameters).orElse(null);
        parameters = parametersReference.get();

        if (instruction == null) {
            throw new AssemblerException(line, "Instruction " + mnemonic + " with the given parameters not found.\n" + rawParameters);
        }

        return instruction instanceof PseudoInstruction
                ? ((PseudoInstruction) instruction).getInstructionAmount(parameters) << 2
                : 4;
    }

    private ParameterParseResult[] assembleParameters(MIPS32AssemblingFile file) {
        ParameterParseResult[] assembledParameters = new ParameterParseResult[parameters.size()];

        int index = 0;
        ParameterParseResult result;
        for (ParameterType parameter : instruction.getParameters()) {
            result = parameter.parse(parameters.get(index), file.getAssembler().getRegisters());

            //Parse label
            if (result.isHasLabel()) {
                var optional = file.getLabel(result.getLabel());

                if (optional.isEmpty()) {
                    if (!labelSufix.isEmpty()) {
                        optional = file.getLabel(result.getLabel() + labelSufix);
                        if (optional.isEmpty()) {
                            throw new AssemblerException(line, "Label " + result.getLabel() + labelSufix + " not found.");
                        }
                    } else {
                        throw new AssemblerException(line, "Label " + result.getLabel() + " not found.");
                    }
                }

                optional.get().addReference(new LabelReference(address, file.getName(), line));
                result.setLabelValue(optional.get().getAddress());
            }
            assembledParameters[index++] = result;
        }

        return assembledParameters;
    }

}
