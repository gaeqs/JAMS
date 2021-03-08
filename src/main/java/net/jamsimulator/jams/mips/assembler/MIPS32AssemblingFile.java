package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.StringUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Represents a MIPS32 assembly file being assembled.
 */
public class MIPS32AssemblingFile {

    private final String name;

    private final List<String> lines;
    private final MIPS32Assembler assembler;

    private final List<InstructionSnapshot> instructions;
    private final List<DirectiveSnapshot> directives;
    private final Map<String, String> equivalents;

    private final Map<String, Integer> labels;
    private final Set<String> convertToGlobalLabel;

    private final Queue<String> labelsToAdd;

    private final Map<String, MIPS32Macro> macros;
    private MIPS32Macro currentMacro;
    private int callsToMacros;


    public MIPS32AssemblingFile(String name, String rawData, MIPS32Assembler assembler) {
        this(name, StringUtils.multiSplit(rawData, "\n", "\r"), assembler);
    }

    public MIPS32AssemblingFile(String name, List<String> lines, MIPS32Assembler assembler) {
        this.name = name;

        this.lines = lines;
        this.assembler = assembler;

        this.instructions = new LinkedList<>();
        this.directives = new LinkedList<>();
        this.equivalents = new HashMap<>();

        this.labels = new HashMap<>();
        this.convertToGlobalLabel = new HashSet<>();
        this.labelsToAdd = new LinkedList<>();

        this.macros = new HashMap<>();
        this.currentMacro = null;
        this.callsToMacros = 0;
    }

    /**
     * Returns the name of this file.
     *
     * @return the name of this file.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link MIPS32Assembler} assembling this file.
     *
     * @return the {@link MIPS32Assembler}.
     */
    public MIPS32Assembler getAssembler() {
        return assembler;
    }

    /**
     * Returns the raw code of this file. This list is modifiable,
     * so directives can modify, add or remove code if this hasn't been scanned.
     *
     * @return the raw code, as a modifiable list.
     */
    public List<String> getRawCode() {
        return lines;
    }

    /**
     * Adds an equivalent. Equivalents are replaced on the code to its value.
     *
     * @param key   the key to replace.
     * @param value the replacement.
     */
    public void addEquivalent(String key, String value) {
        equivalents.put(key, value);
    }

    /**
     * Sets the given label as a global label.
     *
     * @param executingLine the line executing this command.
     * @param label         the label.
     */
    public void setAsGlobalLabel(int executingLine, String label) {
        convertToGlobalLabel.add(label);
        if (labels.containsKey(label)) {
            assembler.addGlobalLabel(executingLine, label, labels.remove(label));
        }
    }

    /**
     * Returns an unmodifiable map with all labels inside this file.
     *
     * @return the map.
     */
    public Map<String, Integer> getLabels() {
        return Collections.unmodifiableMap(labels);
    }

    /**
     * Returns the address that matches the given local label, if present.
     *
     * @param label the label.
     * @return the address, if present.
     */
    public OptionalInt getLocalLabelAddress(String label) {
        return labels.containsKey(label) ? OptionalInt.of(labels.get(label)) : OptionalInt.empty();
    }

    /**
     * Executes the method {@link #getLocalLabelAddress(String)}. If the address is not found,
     * the address is searched on the global labels.
     *
     * @param label the label.
     * @return the address, if present.
     */
    public OptionalInt getLabelAddress(String label) {
        OptionalInt optional = getLocalLabelAddress(label);
        if (!optional.isPresent()) optional = assembler.getGlobalLabelAddress(label);
        return optional;
    }

    /**
     * Executes the first step of the assembly process: the scanning.
     * In this step some directives are executed and labels are registered.
     */
    public void scan() {
        int index = 0;
        for (String line : lines) {
            scanLine(index, line, "");
            index++;
        }
        int current = assembler.getAssemblerData().getCurrent();
        while (!labelsToAdd.isEmpty()) checkLabel(lines.size(), labelsToAdd.poll(), current);
    }

    /**
     * Executes the second step of the assembly process: the instruction assembly.
     * In this step instructions are assembled into machine code.
     */
    public void assembleInstructions() {
        instructions.forEach(target -> target.assemble(this));
    }

    /**
     * Executes the third step of the assembly process: the label-required directive execution.
     * In this step executes directives that requires labels.
     */
    public void executeLabelRequiredDirectives() {
        directives.forEach(target -> target.executeLabelRequiredSteps(this));
    }

    /**
     * Puts the given label on the label queue.
     * All labels inside the queue will be used when an instruction or directive that has a memory address is found.
     *
     * @param label the label.
     */
    public void addLabelToQueue(String label) {
        Validate.notNull(label, "Label cannot be null!");
        labelsToAdd.add(label);
    }

    public void startMacro(MIPS32Macro macro) {
        Validate.notNull(macro, "Macro cannot be null!");
        if (currentMacro != null) {
            throw new AssemblerException("Cannot define a macro inside another macro!");
        }

        currentMacro = macro;
    }

    public void finishMacro() {
        if (currentMacro == null) {
            throw new AssemblerException("No macro defined!");
        }

        macros.put(currentMacro.getName(), currentMacro);
        currentMacro = null;
    }

    protected void scanLine(int index, String line, String labelSufix) {
        String original = line;
        line = sanityLine(line);

        if (checkMacro(line)) return;

        MIPS32AssemblerData data = assembler.getAssemblerData();
        int labelIndex = LabelUtils.getLabelFinishIndex(line);

        String label = null;
        int labelAddress = data.getCurrent();
        if (labelIndex != -1) {
            label = line.substring(0, labelIndex);
            line = line.substring(labelIndex + 1).trim();
        }

        if (line.isEmpty()) {
            if (label != null) {
                labelsToAdd.add(label + labelSufix);
            }
            return;
        }

        if (line.startsWith(".")) {
            // DIRECTIVE
            DirectiveSnapshot snapshot = new DirectiveSnapshot(index, labelAddress, line, labelSufix);
            snapshot.scan(assembler);
            labelAddress = snapshot.executeNonLabelRequiredSteps(this, labelAddress);
            directives.add(snapshot);
        } else {
            var spaceIndex = line.indexOf(" ");

            if (spaceIndex != -1 && line.substring(spaceIndex).trim().startsWith("(")) {
                // MACRO
                executeMacro(line.substring(0, spaceIndex), line.substring(spaceIndex + 1).trim(), index);
            } else {
                // INSTRUCTION
                data.align(2);
                labelAddress = data.getCurrent();
                InstructionSnapshot snapshot = new InstructionSnapshot(index, labelAddress, line, original, labelSufix);
                instructions.add(snapshot);
                data.addCurrent(snapshot.scan(assembler));
            }
        }

        if (labelAddress == -1) {
            if (label != null) {
                labelsToAdd.add(label + labelSufix);
            }
            return;
        }

        checkLabel(index, label, labelAddress);
        while (!labelsToAdd.isEmpty()) checkLabel(index, labelsToAdd.poll(), labelAddress);
    }

    public void checkLabel(int index, String label, int address) {
        if (label == null) return;

        if (!LabelUtils.isLabelLegal(label)) {
            throw new AssemblerException(index, "Label " + label + " contains illegal characters.");
        }

        if (convertToGlobalLabel.contains(label)) {
            assembler.addGlobalLabel(index, label, address);
        } else {
            if (assembler.getGlobalLabelAddress(label).isPresent()) {
                throw new AssemblerException(index, "Label " + label + " is already defined as a global label.");
            }
            if (labels.containsKey(label)) {
                throw new AssemblerException(index, "Label " + label + " already defined.");
            }
            labels.put(label, address);
        }
    }

    private String sanityLine(String line) {
        line = StringUtils.removeComments(line).trim();
        for (Map.Entry<String, String> entry : equivalents.entrySet()) {
            line = line.replace(entry.getKey(), entry.getValue());
        }
        return line;
    }

    private boolean checkMacro(String line) {
        if (currentMacro == null) return false;

        if (line.equals(".endmacro")) {
            System.out.println("Macro finished!");
            finishMacro();
        } else {
            System.out.println("Adding line " + line);
            if (!line.isEmpty()) {
                currentMacro.addLine(line);
            }
        }

        return true;
    }

    private void executeMacro(String name, String parameters, int lineNumber) {
        var macro = macros.get(name);
        if (macro == null)
            throw new AssemblerException("Cannot find macro " + name + "! Macros should be declared before using them!");

        if (!parameters.endsWith(")"))
            throw new AssemblerException("Invalid macro parameters");

        var parsedParameters =
                StringUtils.multiSplitIgnoreInsideString(parameters.substring(1, parameters.length() - 1), false, " ", ",", "\t");

        macro.executeMacro(parsedParameters.toArray(new String[0]), this, lineNumber, callsToMacros);
        callsToMacros++;
    }
}
