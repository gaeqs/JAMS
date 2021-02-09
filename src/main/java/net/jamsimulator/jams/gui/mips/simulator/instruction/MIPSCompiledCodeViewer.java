package net.jamsimulator.jams.gui.mips.simulator.instruction;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.editor.CustomLineNumberFactory;
import net.jamsimulator.jams.gui.util.EasyStyleSpansBuilder;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.event.MemoryEndiannessChange;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.*;
import net.jamsimulator.jams.mips.simulation.pipelined.event.PipelineShiftEvent;
import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.richtext.CodeArea;

import java.util.*;

public abstract class MIPSCompiledCodeViewer extends CodeArea {

    protected static final Set<String> IMMEDIATE = Collections.singleton("mips-instruction-parameter-immediate");
    protected static final Set<String> REGISTER = Collections.singleton("mips-instruction-parameter-register");
    protected static final Set<String> COMMENT = Collections.singleton("mips-comment");
    protected static final Set<String> LABEL = Collections.singleton("mips-label");

    protected final List<MIPSCompiledLine> compiledLines;
    protected final Simulation<?> simulation;
    protected final Register pc;

    protected boolean shouldUpdate;

    public Simulation<?> getSimulation() {
        return simulation;
    }

    public MIPSCompiledCodeViewer(Simulation<?> simulation) {
        compiledLines = new ArrayList<>();
        this.simulation = simulation;
        pc = simulation.getRegisters().getProgramCounter();
        shouldUpdate = true;

        addElements(simulation);

        CompiledViewerNumberFactory factory = new CompiledViewerNumberFactory(this);
        setParagraphGraphicFactory(factory);

        setEditable(false);
        simulation.registerListeners(this, true);
    }

    protected abstract void refresh();

    protected abstract boolean isLineBeingUsed (int line);

    //region events

    @Listener
    private void onPipelineShift(PipelineShiftEvent.After event) {
        if (shouldUpdate) {
            refresh();
        }
    }

    @Listener
    private void onSimulationLock(SimulationLockEvent event) {
        shouldUpdate = true;
        refresh();
    }

    @Listener
    private void onSimulationUnlock(SimulationUnlockEvent event) {
        shouldUpdate = false;
    }

    @Listener
    private void onSimulationStart(SimulationStartEvent event) {
        shouldUpdate = false;
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        shouldUpdate = true;
        refresh();
    }

    @Listener
    private void onSimulationReset(SimulationResetEvent event) {
        if (shouldUpdate) {
            refresh();
        }
    }

    @Listener
    private void onMemoryEndianness(MemoryEndiannessChange.After event) {
        if (shouldUpdate) {
            refresh();
        }
    }

    @Listener
    private void onSimulationUndo(SimulationUndoStepEvent.After event) {
        if (shouldUpdate) {
            refresh();
        }
    }

    //endregion

    //region add and style

    /**
     * Adds all instructions of the {@link Simulation} to this viewer.
     *
     * @param simulation the {@link Simulation}.
     */
    private void addElements(Simulation<?> simulation) {
        var memory = simulation.getMemory();
        var labels = simulation.getData().getLabels();
        int current = MIPS32Memory.TEXT;
        int end = simulation.getInstructionStackBottom();

        var registerStart = String.valueOf(simulation.getRegisters()
                .getValidRegistersStarts().stream().findFirst().orElse('$'));

        var styleBuilder = new EasyStyleSpansBuilder();
        var stringBuilder = new StringBuilder();

        //Style each line.
        while (current <= end) {
            int currentCopy = current;
            var optional = labels.entrySet().stream().filter(e -> e.getValue() == currentCopy).map(Map.Entry::getKey).findAny();
            if (optional.isPresent()) {
                compiledLines.add(new MIPSCompiledLine(compiledLines.size()));
                addAndStyle(optional.get() + ":\n", LABEL, stringBuilder, styleBuilder);
            }

            var code = memory.getWord(current);
            compiledLines.add(new MIPSCompiledLine(compiledLines.size(), current, code));

            stringBuilder.append("\t");
            generateInstructionDisplay(simulation, code, registerStart, stringBuilder, styleBuilder);
            if (current != end) stringBuilder.append("\n");
            current += 4;
        }

        appendText(stringBuilder.toString());
        setStyleSpans(0, styleBuilder.create());
    }

    /**
     * Adds and styles the given instruction.
     *
     * @param simulation    the {@link Simulation}
     * @param code          the code of the instruction.
     * @param registerStart the prefix of the registers.
     * @param stringBuilder the string builder.
     * @param styleBuilder  the style buil.der
     */
    private void generateInstructionDisplay(Simulation<?> simulation, int code, String registerStart,
                                            StringBuilder stringBuilder, EasyStyleSpansBuilder styleBuilder) {
        var optional = simulation.getInstructionSet().getInstructionByInstructionCode(code);
        if (optional.isPresent()) {
            //Add instruction
            var instruction = optional.get();
            var assembled = instruction.assembleFromCode(code);
            addAndStyle(instruction.getMnemonic(), Set.of("mips-instruction"), stringBuilder, styleBuilder);

            //Add parameters
            var parameters = assembled.parametersToString(registerStart);
            var split = parameters.split(",");
            for (int i = 0, splitLength = split.length; i < splitLength; i++) {
                String parameter = split[i];
                parameter = parameter.trim();

                addAndStyle(i == 0 ? " " : ", ", Collections.emptySet(), stringBuilder, styleBuilder);
                styleParameter(parameter, registerStart, stringBuilder, styleBuilder);
            }

            //Add code
            var total = instruction.getMnemonic() + " " + parameters;
            stringBuilder.append(StringUtils.addSpaces("", Math.max(0, 30 - total.length()), true));
            addAndStyle("0x" + StringUtils.addZeros(Integer.toHexString(code), 8), COMMENT, stringBuilder, styleBuilder);
        }
    }

    /**
     * Adds and styles the given parameter.
     *
     * @param parameter     the parameter.
     * @param registerStart the prefix of the registers.
     * @param stringBuilder the string builder.
     * @param styleBuilder  the style buil.der
     */
    private void styleParameter(String parameter, String registerStart,
                                StringBuilder stringBuilder, EasyStyleSpansBuilder styleBuilder) {
        if (parameter.startsWith(registerStart)) {
            //REGISTERS
            addAndStyle(parameter, REGISTER, stringBuilder, styleBuilder);
        } else if (!parameter.contains(registerStart)) {
            //IMMEDIATES
            addAndStyle(parameter, IMMEDIATE, stringBuilder, styleBuilder);
        } else {
            //IMMEDIATE SHIFT REGISTER
            int firstIndex = parameter.indexOf('(');
            int secondIndex = parameter.indexOf(')');
            if (firstIndex != -1 && secondIndex != -1) {

                addAndStyle(parameter.substring(0, firstIndex), IMMEDIATE, stringBuilder, styleBuilder);
                stringBuilder.append("(");
                addAndStyle(parameter.substring(firstIndex + 1, secondIndex), REGISTER, stringBuilder, styleBuilder);
                stringBuilder.append(")");

            } else {
                stringBuilder.append(parameter);
            }
        }
    }

    /**
     * Adds and styles the given element.
     *
     * @param element       the element.
     * @param style         the styles.
     * @param stringBuilder the string builder.
     * @param styleBuilder  the style buil.der
     */
    private void addAndStyle(String element, Collection<String> style,
                             StringBuilder stringBuilder, EasyStyleSpansBuilder styleBuilder) {
        int size = stringBuilder.length();
        styleBuilder.add(size, element, style);
        stringBuilder.append(element);
    }

    //endregion
}
