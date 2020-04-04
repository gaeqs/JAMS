package net.jamsimulator.jams.gui.display.mips.element;

import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a collection of assembly elements within an {@link net.jamsimulator.jams.mips.assembler.AssemblingFile}.
 */
public class MipsFileElements {

	private final Set<DisplayComment> comments;
	private final Set<DisplayLabel> labels;
	private final Set<DisplayDirective> directives;
	private final Set<DisplayDirectiveParameter> directivesParameters;
	private final Set<DisplayInstruction> instructions;
	private final Set<DisplayInstructionParameterPart> instructionParameterParts;

	public MipsFileElements() {
		this.comments = new HashSet<>();
		this.labels = new HashSet<>();
		this.directives = new HashSet<>();
		this.directivesParameters = new HashSet<>();
		this.instructions = new HashSet<>();
		this.instructionParameterParts = new HashSet<>();
	}

	/**
	 * Returns all comments.
	 *
	 * @return the comments.
	 */
	public Set<DisplayComment> getComments() {
		return comments;
	}

	/**
	 * Returns all labels.
	 *
	 * @return all labels.
	 */
	public Set<DisplayLabel> getLabels() {
		return labels;
	}

	/**
	 * Returns all directives.
	 *
	 * @return all directives.
	 */
	public Set<DisplayDirective> getDirectives() {
		return directives;
	}

	/**
	 * Returns all directives' parameters.
	 *
	 * @return all directives' parameters.
	 */
	public Set<DisplayDirectiveParameter> getDirectivesParameters() {
		return directivesParameters;
	}

	/**
	 * Returns all instructions.
	 *
	 * @return all instructions.
	 */
	public Set<DisplayInstruction> getInstructions() {
		return instructions;
	}

	/**
	 * Returns all instructions' parameters' parts.
	 *
	 * @return all instruction's parameters' parts.
	 */
	public Set<DisplayInstructionParameterPart> getInstructionParameterParts() {
		return instructionParameterParts;
	}

	/**
	 * Returns all {@link MipsCodeElement}s.
	 *
	 * @return all {@link MipsCodeElement}s.
	 */
	public Set<MipsCodeElement> getElements() {
		Set<MipsCodeElement> elements = new HashSet<>();
		elements.addAll(comments);
		elements.addAll(labels);
		elements.addAll(directives);
		elements.addAll(directivesParameters);
		elements.addAll(instructions);
		elements.addAll(instructionParameterParts);
		return Collections.unmodifiableSet(elements);
	}

	/**
	 * Returns all {@link MipsCodeElement}s, sorted by it's start index.
	 *
	 * @return all {@link MipsCodeElement}s.
	 */
	public SortedSet<MipsCodeElement> getSortedElements() {
		SortedSet<MipsCodeElement> elements = new TreeSet<>(Comparator.comparingInt(o -> o.startIndex));
		elements.addAll(comments);
		elements.addAll(labels);
		elements.addAll(directives);
		elements.addAll(directivesParameters);
		elements.addAll(instructions);
		elements.addAll(instructionParameterParts);
		return Collections.unmodifiableSortedSet(elements);
	}

	/**
	 * Refreshes the file.
	 *
	 * @param lines       the file text.
	 * @param workingPane the working pane.
	 */
	public void refresh(String lines, WorkingPane workingPane) {
		//Clears all sets.
		comments.clear();
		labels.clear();
		directives.clear();
		directivesParameters.clear();
		instructions.clear();
		instructionParameterParts.clear();

		if (lines.isEmpty()) return;

		int start = 0;
		int end = 0;
		StringBuilder builder = new StringBuilder();

		//Checks all lines
		char c;
		while (lines.length() > end) {
			c = lines.charAt(end);
			if (c == '\n' || c == '\r') {

				//Checks line
				parseLine(start, end, builder.toString());

				//Restarts the builder. 
				builder = new StringBuilder();
				start = end + 1;
			} else builder.append(c);
			end++;
		}
		if (end < start) return; //Empty

		//Checks the final line.
		parseLine(start, end, builder.toString());

		//Checks for errors.
		searchErrors(workingPane);
	}

	private void parseLine(int start, int end, String line) {
		//COMMENT
		int commentIndex = StringUtils.getCommentIndex(line);
		if (commentIndex != -1) {
			comments.add(new DisplayComment(start + commentIndex, end, line.substring(commentIndex)));
			end = start + commentIndex;
			line = line.substring(0, commentIndex);
		}

		//LABEL
		int labelIndex = line.indexOf(":");
		if (labelIndex != -1) {
			labels.add(new DisplayLabel(start, start + labelIndex, line.substring(0, labelIndex + 1)));
			start = start + labelIndex + 1;
			line = line.substring(labelIndex + 1);
		}

		//DIRECTIVE OR INSTRUCTION
		if (line.trim().startsWith(".")) parseDirective(start, line);
		else parseInstruction(start, line);
	}

	private void parseDirective(int start, String line) {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(line, false, " ", ",", "\t");
		if (parts.isEmpty()) return;

		//Sorts all entries by their indices.
		List<Map.Entry<Integer, String>> stringParameters = parts.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

		//The first entry is the directive itself.
		Map.Entry<Integer, String> first = stringParameters.get(0);
		DisplayDirective directive = new DisplayDirective(start + first.getKey(), start + first.getKey()
				+ first.getValue().length(), first.getValue());
		directives.add(directive);
		stringParameters.remove(0);

		//Adds all parameters.
		int parameterIndex = 0;
		DisplayDirectiveParameter parameter;
		for (Map.Entry<Integer, String> entry : stringParameters) {
			parameter = new DisplayDirectiveParameter(
					directive, parameterIndex,
					start + entry.getKey(),
					start + entry.getKey() + entry.getValue().length(), entry.getValue(),
					StringUtils.isStringOrChar(entry.getValue()));
			directivesParameters.add(parameter);
			directive.addParameter(parameter);
			parameterIndex++;
		}
	}

	private void parseInstruction(int start, String line) {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(line, false, " ", ",", "\t");
		if (parts.isEmpty()) return;

		//Sorts all entries by their indices.
		List<Map.Entry<Integer, String>> stringParameters = parts.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

		//The first entry is the instruction itself.
		Map.Entry<Integer, String> first = stringParameters.get(0);
		DisplayInstruction instruction = new DisplayInstruction(start + first.getKey(), start + first.getKey()
				+ first.getValue().length(), first.getValue());
		instructions.add(instruction);
		stringParameters.remove(0);

		//Adds all parameters.
		int parameterIndex = 0;
		InstructionParameter parameter;
		for (Map.Entry<Integer, String> entry : stringParameters) {

			//Parses the parameter.
			parameter = parseParameter(start + entry.getKey(), entry.getValue(), instruction, parameterIndex);
			instruction.addParameter(parameter);
			parameterIndex++;
		}
	}

	private InstructionParameter parseParameter(int start, String line, DisplayInstruction instruction, int parameterIndex) {
		//Splits all parts.
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(line, false, "+", "(", ")");

		//The parameter instance.
		InstructionParameter parameter = new InstructionParameter(line, instruction);

		//Adds all parts.
		parts.forEach((index, string) -> {
			DisplayInstructionParameterPart part = new DisplayInstructionParameterPart(instruction, parameterIndex,
					start + index,
					start + index + string.length(), string,
					DisplayInstructionParameterPart.InstructionParameterPartType.getByString(string));
			instructionParameterParts.add(part);
			parameter.addPart(part);
		});
		return parameter;
	}

	private void searchErrors(WorkingPane pane) {
		labels.forEach(target -> target.searchErrors(pane, this));
		directives.forEach(target -> target.searchErrors(pane, this));
		directivesParameters.forEach(target -> target.searchErrors(pane, this));
		instructions.forEach(target -> target.searchErrors(pane, this));
		instructionParameterParts.forEach(target -> target.searchErrors(pane, this));
	}

}
