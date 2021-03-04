package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.application.Platform;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.instruction.type.MIPSMultiCycleAssembledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.instruction.type.MIPSPipelinedAssembledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.instruction.type.MIPSSingleCycleAssembledCodeViewer;
import net.jamsimulator.jams.gui.util.EasyStyleSpansBuilder;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.*;
import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.richtext.CodeArea;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Represents the area that shows a simulation's instructions and their statuses.
 * <p>
 * Architectures must implement a subclass of this viewer.
 */
public abstract class MIPSAssembledCodeViewer extends CodeArea {

    public static Map<Architecture, BiFunction<Simulation<?>, Boolean, MIPSAssembledCodeViewer>> VIEWERS_PER_ARCHITECTURE = new HashMap<>();

    static {
        VIEWERS_PER_ARCHITECTURE.put(SingleCycleArchitecture.INSTANCE, MIPSSingleCycleAssembledCodeViewer::new);
        VIEWERS_PER_ARCHITECTURE.put(MultiCycleArchitecture.INSTANCE, MIPSMultiCycleAssembledCodeViewer::new);
        VIEWERS_PER_ARCHITECTURE.put(PipelinedArchitecture.INSTANCE, MIPSPipelinedAssembledCodeViewer::new);
    }

    public static void registerViewer(Architecture architecture,
                                      BiFunction<Simulation<?>, Boolean, MIPSAssembledCodeViewer> builder) {
        VIEWERS_PER_ARCHITECTURE.put(architecture, builder);
    }

    public static MIPSAssembledCodeViewer createViewer(Architecture architecture, Simulation<?> simulation, boolean kernel) {
        BiFunction<Simulation<?>, Boolean, MIPSAssembledCodeViewer> builder =
                VIEWERS_PER_ARCHITECTURE.get(architecture);
        if (builder == null) return new MIPSSingleCycleAssembledCodeViewer(simulation, kernel);
        return builder.apply(simulation, kernel);
    }

    protected static final Set<String> IMMEDIATE = Collections.singleton("mips-instruction-parameter-immediate");
    protected static final Set<String> REGISTER = Collections.singleton("mips-instruction-parameter-register");
    protected static final Set<String> INSTRUCTION = Collections.singleton("mips-instruction");
    protected static final Set<String> COMMENT = Collections.singleton("mips-comment");
    protected static final Set<String> LABEL = Collections.singleton("mips-label");
    protected static final Set<String> BREAKPOINT = Collections.singleton("instruction-breakpoint");

    private static final String VIEWER_ELEMENTS_ORDER_NODE = "simulation.mips.viewer_elements_order";
    private static final String SHOW_LABELS_NODE = "simulation.mips.show_labels";

    protected final List<MIPSAssembledLine> assembledLines;
    protected final Simulation<?> simulation;
    protected final boolean kernel;
    protected final Register pc;

    protected boolean shouldUpdate;
    protected boolean fullSpeed;

    public Simulation<?> getSimulation() {
        return simulation;
    }

    /**
     * Creates the code viewer.
     *
     * @param simulation the simulation holding the instructions.
     * @param kernel     whether this view should show user or kernel instructions.
     */
    public MIPSAssembledCodeViewer(Simulation<?> simulation, boolean kernel) {
        assembledLines = new ArrayList<>();
        this.simulation = simulation;
        this.kernel = kernel;
        pc = simulation.getRegisters().getProgramCounter();
        shouldUpdate = true;
        fullSpeed = simulation.getCycleDelay() == 0;

        addElements(simulation);

        CompiledViewerNumberFactory factory = new CompiledViewerNumberFactory(this);
        setParagraphGraphicFactory(factory);

        setEditable(false);
        simulation.registerListeners(this, true);
        simulation.getMemory().getBottomMemory().registerListeners(this, true);

        Jams.getMainConfiguration().registerListeners(this, true);
    }

    /**
     * Moves the view to the given address.
     *
     * @param address the address.
     * @return whether the operation was successful.
     */
    public boolean selectAddress(int address) {
        var optional = assembledLines.stream()
                .filter(line -> line.getAddress().map(v -> v == address).orElse(false)).findAny();
        if (optional.isEmpty()) return false;

        int line = optional.get().getLine() != 0 ? optional.get().getLine() - 1 : 0;
        moveTo(line, 0);
        showParagraphAtTop(line);
        return true;
    }

    /**
     * Refreshes the styles of all paragraphs.
     * <p>
     * To be implemente by the different architectures.
     */
    protected abstract void refresh();

    /**
     * Clears all styles of all paragraphs.
     * <p>
     * To be implemente by the different architectures.
     */
    protected abstract void clearStyles();

    /**
     * Returns whether the given line is being styled by the architecture.
     * <p>
     * To be implemente by the different architectures.
     *
     * @param line the line.
     * @return whether it's being styled by the architecture.
     */
    protected abstract boolean isLineBeingUsed(int line);

    protected void refreshInstructions() {
        var memory = simulation.getMemory();

        var registerStart = String.valueOf(simulation.getRegisters()
                .getValidRegistersStarts().stream().findFirst().orElse('$'));
        var order = Jams.getMainConfiguration()
                .getAndConvertOrElse(VIEWER_ELEMENTS_ORDER_NODE, MIPSAssembledInstructionViewerOrder.DEFAULT);
        var originals = simulation.getData().getOriginalInstructions();


        for (MIPSAssembledLine line : assembledLines) {
            if (line.getAddress().isEmpty() || line.getCode().isEmpty()) continue;

            int code = memory.getWord(line.getAddress().get(), false, false, false);

            if (line.getCode().get() != code) {
                line.setCode(code);
                Platform.runLater(() -> {

                    var styleBuilder = new EasyStyleSpansBuilder();
                    var stringBuilder = new StringBuilder();

                    generateInstructionDisplay(simulation, code, registerStart,
                            originals.getOrDefault(line.getAddress().get(), ""),
                            order, stringBuilder, styleBuilder);


                    var paragraph = getParagraph(line.getLine());
                    replace(line.getLine(), 0, line.getLine(), paragraph.length(),
                            stringBuilder.toString(), Collections.emptyList());
                    if(!styleBuilder.isEmpty()) {
                        setStyleSpans(line.getLine(), 0, styleBuilder.create());
                    }
                });
            }
        }

    }

    //region events

    @Listener
    private void onSimulationLock(SimulationLockEvent event) {
        shouldUpdate = true;
        refresh();
        refreshInstructions();
    }

    @Listener
    private void onSimulationUnlock(SimulationUnlockEvent event) {
        shouldUpdate = false;
    }

    @Listener
    private void onSimulationStart(SimulationStartEvent event) {
        shouldUpdate = false;
        fullSpeed = simulation.getCycleDelay() == 0;
        if (fullSpeed) {
            clearStyles();
        }
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        shouldUpdate = true;
        refresh();
        refreshInstructions();
    }

    @Listener
    private void onSimulationReset(SimulationResetEvent event) {
        refresh();
        refreshInstructions();
    }

    @Listener
    private void onSimulationUndo(SimulationUndoStepEvent.After event) {
        if (shouldUpdate) {
            refresh();
            refreshInstructions();
        }
    }

    @Listener
    private void onBreakpointAdd(SimulationAddBreakpointEvent event) {
        int address = event.getAddress();
        var optional = assembledLines.stream()
                .filter(line -> line.getAddress().map(v -> v == address).orElse(false)).findAny();

        if (optional.isPresent() && !isLineBeingUsed(optional.get().getLine())) {
            setParagraphStyle(optional.get().getLine(), BREAKPOINT);
        }
    }

    @Listener
    private void onBreakpointRemove(SimulationRemoveBreakpointEvent event) {
        int address = event.getAddress();
        var optional = assembledLines.stream()
                .filter(line -> line.getAddress().map(v -> v == address).orElse(false)).findAny();

        if (optional.isPresent() && !isLineBeingUsed(optional.get().getLine())) {
            setParagraphStyle(optional.get().getLine(), Collections.emptySet());
        }
    }

    @Listener
    private void onConfigurationNodeChange(ConfigurationNodeChangeEvent.After event) {
        if (event.getNode().equals(SHOW_LABELS_NODE) || event.getNode().equals(VIEWER_ELEMENTS_ORDER_NODE)) {
            clear();
            addElements(simulation);
            refresh();

            var breakpoints = simulation.getBreakpoints();
            for (var line : assembledLines) {
                if (line.getAddress().isEmpty()) continue;
                if (breakpoints.contains(line.getAddress().get()) && !isLineBeingUsed(line.getLine())) {
                    try {
                        setParagraphStyle(line.getLine(), BREAKPOINT);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Listener
    private void onMemoryChange(MemoryWordSetEvent.After event) {
        checkMemoryChange(event.getAddress());
    }

    @Listener
    private void onMemoryChange(MemoryByteSetEvent.After event) {
        checkMemoryChange(event.getAddress());
    }

    private void checkMemoryChange(int address) {
        if (shouldUpdate || !simulation.isRunning()) {
            int first = kernel ? MIPS32Memory.EXCEPTION_HANDLER : MIPS32Memory.TEXT;
            int last = kernel ? simulation.getKernelStackBottom() : simulation.getInstructionStackBottom();

            if (address >= first && address <= last) {
                refreshInstructions();
            }
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
        assembledLines.clear();
        var order = Jams.getMainConfiguration()
                .getAndConvertOrElse(VIEWER_ELEMENTS_ORDER_NODE, MIPSAssembledInstructionViewerOrder.DEFAULT);
        boolean showLabels = Jams.getMainConfiguration().getOrElse(SHOW_LABELS_NODE, false);

        var memory = simulation.getMemory();
        var labels = simulation.getData().getLabels();
        var originals = simulation.getData().getOriginalInstructions();

        int current = kernel ? MIPS32Memory.EXCEPTION_HANDLER : MIPS32Memory.TEXT;
        int end = kernel ? simulation.getKernelStackBottom() : simulation.getInstructionStackBottom();

        var registerStart = String.valueOf(simulation.getRegisters()
                .getValidRegistersStarts().stream().findFirst().orElse('$'));

        var styleBuilder = new EasyStyleSpansBuilder();
        var stringBuilder = new StringBuilder();

        //Style each line.
        while (current <= end) {
            int currentCopy = current;

            if (showLabels) {
                var optional = labels.entrySet().stream().filter(e -> e.getValue() == currentCopy).map(Map.Entry::getKey).findAny();
                if (optional.isPresent()) {
                    assembledLines.add(new MIPSAssembledLine(assembledLines.size()));
                    addAndStyle(optional.get() + ":\n", LABEL, stringBuilder, styleBuilder);
                }
            }

            var code = memory.getWord(current, false, true, true);
            assembledLines.add(new MIPSAssembledLine(assembledLines.size(), current, code));

            var original = originals.getOrDefault(current, "");

            generateInstructionDisplay(simulation, code, registerStart, original, order, stringBuilder, styleBuilder);
            if (current != end) stringBuilder.append("\n");
            current += 4;
        }

        appendText(stringBuilder.toString());
        if (!styleBuilder.isEmpty()) {
            setStyleSpans(0, styleBuilder.create());
        }
    }

    /**
     * Adds and styles the given instruction.
     *
     * @param simulation    the {@link Simulation}
     * @param code          the code of the instruction.
     * @param registerStart the prefix of the registers.
     * @param stringBuilder the string builder.
     * @param styleBuilder  the style builder.
     */
    private void generateInstructionDisplay(Simulation<?> simulation, int code,
                                            String registerStart, String original,
                                            MIPSAssembledInstructionViewerOrder order,
                                            StringBuilder stringBuilder, EasyStyleSpansBuilder styleBuilder) {

        var optional = simulation.getInstructionSet().getInstructionByInstructionCode(code);
        if (optional.isPresent()) {
            var elements = order.getElements();
            var instruction = optional.get();

            for (int i = 0, elementsSize = elements.size(); i < elementsSize; i++) {
                MIPSAssembledInstructionViewerElement element = elements.get(i);
                int size = switch (element) {
                    case HEX_CODE -> styleHexadecimalCode(code, stringBuilder, styleBuilder);
                    case DISASSEMBLED -> styleDisassembled(instruction, code, registerStart, i == 0, stringBuilder, styleBuilder);
                    case ORIGINAL -> styleOriginal(original, stringBuilder, styleBuilder);
                };

                if (i != elementsSize - 1) {
                    int add = Math.max(4, element.getMaximumLength() - size);
                    stringBuilder.append(" ".repeat(add));
                }
            }
        }
    }

    /**
     * Styles the hexadeximal representation of the instruction.
     *
     * @param code          the code of the instruction
     * @param stringBuilder the string builder.
     * @param styleBuilder  the style builder.
     * @return the length of the styled text.
     */
    private int styleHexadecimalCode(int code, StringBuilder stringBuilder, EasyStyleSpansBuilder styleBuilder) {
        addAndStyle("0x" + StringUtils.addZeros(Integer.toHexString(code), 8), COMMENT, stringBuilder, styleBuilder);
        return 10;
    }

    /**
     * Styles the disassembled representation of the instruction.
     *
     * @param instruction   the basic instruction.
     * @param code          the code of the instruction
     * @param registerStart the prefix of the registers.
     * @param stringBuilder the string builder.
     * @param styleBuilder  the style builder.
     * @return the length of the styled text.
     */
    private int styleDisassembled(BasicInstruction<?> instruction, int code, String registerStart, boolean first,
                                  StringBuilder stringBuilder, EasyStyleSpansBuilder styleBuilder) {
        addAndStyle((first ? "\t" : "") + instruction.getMnemonic(), INSTRUCTION, stringBuilder, styleBuilder);

        var assembled = instruction.assembleFromCode(code);
        var parameters = assembled.parametersToString(registerStart);
        var split = parameters.split(",");
        for (int i = 0, splitLength = split.length; i < splitLength; i++) {
            String parameter = split[i];
            parameter = parameter.trim();

            addAndStyle(i == 0 ? " " : ", ", Collections.emptySet(), stringBuilder, styleBuilder);
            styleParameter(parameter, registerStart, stringBuilder, styleBuilder);
        }
        var total = instruction.getMnemonic() + " " + parameters;
        return total.length();
    }

    /**
     * Styles the original representation of the instruction.
     *
     * @param original      the original text.
     * @param stringBuilder the string builder.
     * @param styleBuilder  the style builder.
     * @return the length of the styled text.
     */
    private int styleOriginal(String original, StringBuilder stringBuilder, EasyStyleSpansBuilder styleBuilder) {
        original = original.replace("\t", "  ");
        addAndStyle(original, COMMENT, stringBuilder, styleBuilder);
        return original.length();
    }

    /**
     * Adds and styles the given parameter.
     *
     * @param parameter     the parameter.
     * @param registerStart the prefix of the registers.
     * @param stringBuilder the string builder.
     * @param styleBuilder  the style builder.
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
     * @param styleBuilder  the style builder.
     */
    private void addAndStyle(String element, Collection<String> style,
                             StringBuilder stringBuilder, EasyStyleSpansBuilder styleBuilder) {
        int size = stringBuilder.length();
        styleBuilder.add(size, element, style);
        stringBuilder.append(element);
    }

    //endregion
}
