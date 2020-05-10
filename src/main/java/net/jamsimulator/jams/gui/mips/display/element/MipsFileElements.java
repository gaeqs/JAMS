/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.display.element;

import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a collection of assembly elements within an {@link net.jamsimulator.jams.mips.assembler.AssemblingFile}.
 */
public class MipsFileElements {

	private final File file;

	private final MipsProject project;
	private final List<MipsLine> lines;
	private final List<String> labels;
	private final List<String> globalLabels;

	/**
	 * Creates an empty element collection.
	 * To populate it use {@link #refreshAll(String, WorkingPane)}.
	 */
	public MipsFileElements(File file, MipsProject project) {
		this.file = file;
		this.project = project;
		this.lines = new ArrayList<>();
		this.labels = new LinkedList<>();
		this.globalLabels = new ArrayList<>();
	}

	/**
	 * Returns the project containing this file, if present.
	 *
	 * @return the project, if present.
	 */
	public MipsProject getProject() {
		return project;
	}

	/**
	 * Returns all lines of the represented file.
	 *
	 * @return the lines.
	 */
	public List<MipsLine> getLines() {
		return lines;
	}

	/**
	 * Returns all labels of the represented file.
	 *
	 * @return the labels.
	 */
	public List<String> getLabels() {
		return labels;
	}

	/**
	 * Returns all global labels of the represented file. These labels may not exist.
	 * To get all existing global labels use {@link List #getExistingGlobalLabels}.
	 *
	 * @return all global labels.
	 */
	public List<String> getGlobalLabels() {
		return globalLabels;
	}

	public List<String> getExistingGlobalLabels() {
		return globalLabels.stream().filter(labels::contains).collect(Collectors.toList());
	}

	/**
	 * Returns the element placed at the given index.
	 *
	 * @param index the index.
	 * @return the element, if found.
	 */
	public Optional<MipsCodeElement> getElementAt(int index) {
		try {
			MipsLine line = lines.get(lineOf(index));
			return line.getElementAt(index);
		} catch (IndexOutOfBoundsException ex) {
			return Optional.empty();
		}
	}

	/**
	 * Returns the index of the file where the given absolute index is located at.
	 *
	 * @param index the absolute index.
	 * @return the line index or -1 if not found.
	 */
	public int lineOf(int index) {
		if (index == -1) return -1;
		MipsLine line;
		for (int i = 0; i < lines.size(); i++) {
			line = lines.get(i);
			if (line.getStart() <= index && line.getStart() + line.getText().length() >= index) return i;
		}
		return -1;
	}

	/**
	 * Removes the line located at the given index.
	 * <p>
	 * This method WONT remove the line from the file. This just removes it's style and internal information.
	 * If the line is not removed from the editor too the execution of this method will result on a buggy editor!
	 *
	 * @param lineIndex the absolute index.
	 * @return whether a global labels refresh is required.
	 */
	public boolean removeLine(int lineIndex) {
		if (lineIndex < 0 || lineIndex >= lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
		MipsLine line = lines.remove(lineIndex);
		line.getLabel().ifPresent(label -> labels.remove(label.getLabel()));
		line.getDirective().filter(DisplayDirective::isGlobalLabelsParameter).ifPresent(directive ->
				directive.getParameters().forEach(label -> globalLabels.remove(label.text)));
		int length = line.getText().length() + 1;

		MipsLine current;
		for (int i = lineIndex; i < lines.size(); i++) {
			current = lines.get(i);
			current.setStart(line.getStart() - length);
		}

		//Refresh if any global label has changed.
		return line.getDirective().map(DisplayDirective::isGlobalLabelsParameter).orElse(false) ||
				line.getLabel().map(DisplayLabel::isGlobal).orElse(false);
	}

	/**
	 * Adds a line at the given index.
	 * <p>
	 * This method WONT add the line to the file. This just adds it's style and internal information.
	 * If the line is not added to the editor too the execution of this method will result on a buggy editor!
	 *
	 * @param lineIndex the absolute index.
	 * @param line      the line to remove.
	 * @return whether a global labels refresh is required.
	 */
	public boolean addLine(int lineIndex, String line) {
		if (lineIndex < 0 || lineIndex > lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
		if (line.contains("\n") || line.contains("\r")) throw new IllegalArgumentException("Invalid line!");
		int start = 0;
		if (lineIndex > 0) {
			MipsLine previous = lines.get(lineIndex - 1);
			start = previous.getStart() + previous.getText().length() + 1;
		}
		MipsLine mipsLine = new MipsLine(start, line);
		parseLine(start, start + line.length(), line, mipsLine);
		lines.add(lineIndex, mipsLine);
		mipsLine.getLabel().ifPresent(label -> labels.add(label.getLabel()));
		mipsLine.getDirective().filter(DisplayDirective::isGlobalLabelsParameter)
				.ifPresent(directive -> directive.getParameters().forEach(label -> globalLabels.add(label.text)));

		int length = line.length() + 1;
		MipsLine current;
		for (int i = lineIndex + 1; i < lines.size(); i++) {
			current = lines.get(i);
			current.setStart(mipsLine.getStart() + length);
		}

		//Check if new label is a global parameter.
		mipsLine.getLabel().ifPresent(target -> target.checkGlobalLabelsChanges(globalLabels));

		//Refresh if any global label has changed.
		return mipsLine.getDirective().map(DisplayDirective::isGlobalLabelsParameter).orElse(false) ||
				mipsLine.getLabel().map(DisplayLabel::isGlobal).orElse(false);
	}

	/**
	 * Replaces the line located at the given index with the new given line.
	 * <p>
	 * This method WONT edit the line of the file. This just edits it's style and internal information.
	 * If the line is not edited on the editor too the execution of this method will result on a buggy editor!
	 *
	 * @param lineIndex the absolute index.
	 * @param line      the line to edit.
	 * @return whether a global labels refresh is required.
	 */
	public boolean editLine(int lineIndex, String line) {
		if (lineIndex < 0 || lineIndex >= lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
		if (line.contains("\n") || line.contains("\r")) throw new IllegalArgumentException("Invalid line!");
		MipsLine old = lines.get(lineIndex);
		old.getLabel().ifPresent(label -> labels.remove(label.getLabel()));
		old.getDirective().filter(DisplayDirective::isGlobalLabelsParameter).ifPresent(directive ->
				directive.getParameters().forEach(label -> globalLabels.remove(label.text)));
		int difference = line.length() - old.getText().length();
		MipsLine mipsLine = new MipsLine(old.getStart(), line);
		parseLine(old.getStart(), old.getStart() + line.length(), line, mipsLine);
		lines.set(lineIndex, mipsLine);
		mipsLine.getLabel().ifPresent(label -> labels.add(label.getLabel()));
		mipsLine.getDirective().filter(DisplayDirective::isGlobalLabelsParameter)
				.ifPresent(directive -> directive.getParameters().forEach(label -> globalLabels.add(label.text)));

		MipsLine current;
		for (int i = lineIndex + 1; i < lines.size(); i++) {
			current = lines.get(i);
			current.setStart(mipsLine.getStart() + difference);
		}


		//Check if new label is a global parameter.
		mipsLine.getLabel().ifPresent(target -> target.checkGlobalLabelsChanges(globalLabels));

		//Refresh if any global label has changed.
		return old.getDirective().map(DisplayDirective::isGlobalLabelsParameter).orElse(false) ||
				mipsLine.getDirective().map(DisplayDirective::isGlobalLabelsParameter).orElse(false) ||
				old.getLabel().map(DisplayLabel::isGlobal).orElse(false) ||
				mipsLine.getLabel().map(DisplayLabel::isGlobal).orElse(false);
	}

	/**
	 * Returns whether the file has the given label declared.
	 *
	 * @param label the label
	 * @return whether the file has the given label declared.
	 */
	public boolean hasLabel(String label) {
		return labels.contains(label);
	}

	/**
	 * Returns the amount of times a label is declared on the file.
	 *
	 * @param label the label.
	 * @return the amount of times.
	 */
	public int labelCount(String label) {
		return (int) labels.stream().filter(target -> target.equals(label)).count();
	}

	/**
	 * Refreshes the file.
	 * This checks for errors automatically.
	 *
	 * @param lines       the file text.
	 * @param workingPane the working pane.
	 */
	public void refreshAll(String lines, WorkingPane workingPane) {
		this.lines.clear();

		if (lines.isEmpty()) return;

		int start = 0;
		int end = 0;
		StringBuilder builder = new StringBuilder();

		//Checks all lines
		char c;
		MipsLine mipsLine;
		while (lines.length() > end) {
			c = lines.charAt(end);
			if (c == '\n' || c == '\r') {
				mipsLine = new MipsLine(start, builder.toString());
				this.lines.add(mipsLine);
				//Checks line
				parseLine(start, end, builder.toString(), mipsLine);

				//Restarts the builder. 
				builder = new StringBuilder();
				start = end + 1;
			} else builder.append(c);
			end++;
		}
		if (end >= start) {
			mipsLine = new MipsLine(start, builder.toString());
			this.lines.add(mipsLine);
			//Checks the final line.
			parseLine(start, end, builder.toString(), mipsLine);
		}

		//Checks for errors.
		refreshLabels();
		searchLabelErrors();
		refreshGlobalLabelsChanges();

		if (workingPane != null) {
			searchGeneralErrors(workingPane);
		}
	}

	/**
	 * Search for errors in all lines.
	 *
	 * @param workingPane the {@link WorkingPane} where the file is displayed.
	 */
	public void searchGeneralErrors(WorkingPane workingPane) {
		lines.forEach(line -> line.searchAllErrors(workingPane, this));
	}

	/**
	 * Search for errors in the selected lines.
	 *
	 * @param workingPane the {@link WorkingPane} where the file is displayed.
	 * @param from        the first line to check.
	 * @param amount      the amount of lines to check.
	 */
	public void searchGeneralErrors(WorkingPane workingPane, int from, int amount) {
		if (from < 0 || from + amount > lines.size())
			throw new IndexOutOfBoundsException("Index out of bounds. [" + from + ", " + (from + amount) + ")");
		for (int i = 0; i < amount; i++) {
			lines.get(from + i).searchAllErrors(workingPane, this);
		}
	}

	/**
	 * Search for label errors in all lines.
	 */
	public List<Integer> searchLabelErrors() {
		List<Integer> updated = new ArrayList<>();

		List<String> gLabels = project == null || !project.getData().getFilesToAssemble().getFiles().contains(file)
				? globalLabels : project.getData().getFilesToAssemble().getGlobalLabels();

		Iterator<MipsLine> iterator = lines.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			if (iterator.next().searchLabelErrors(labels, gLabels)) updated.add(i);
			i++;
		}
		return updated;
	}

	/**
	 * Refresh the labels' list.
	 */
	public void refreshLabels() {
		labels.clear();
		globalLabels.clear();
		lines.forEach(line -> line.getDirective()
				.filter(DisplayDirective::isGlobalLabelsParameter)
				.ifPresent(directive -> directive.getParameters().forEach(label -> globalLabels.add(label.text))));
		lines.forEach(line -> line.getLabel().ifPresent(label -> labels.add(label.getLabel())));
	}


	public List<Integer> refreshGlobalLabelsChanges() {
		List<String> globalLabels;
		if (project == null || !project.getData().getFilesToAssemble().getFiles().contains(file)) {
			globalLabels = getExistingGlobalLabels();
		} else {
			globalLabels = project.getData().getFilesToAssemble().getGlobalLabels();
		}

		List<Integer> updated = new ArrayList<>();

		Iterator<MipsLine> iterator = lines.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			if (iterator.next().checkGlobalLabelsChanges(labels, this.globalLabels, globalLabels)) updated.add(i);
			i++;
		}
		return updated;
	}

	/**
	 * Styles the selected lines.
	 *
	 * @param area   the area to style.
	 * @param from   the first line index.
	 * @param amount the amount of lines to style.
	 */
	public void styleLines(CodeArea area, int from, int amount) {
		if (from < 0 || from + amount > lines.size())
			throw new IndexOutOfBoundsException("Index out of bounds. [" + from + ", " + (from + amount) + ")");
		for (int i = 0; i < amount; i++) {
			lines.get(from + i).styleLine(area, from + i);
		}
	}

	/**
	 * Styles the selected lines.
	 *
	 * @param area  the area to style.
	 * @param lines the lines to update.
	 */
	public void styleLines(CodeArea area, Collection<Integer> lines) {
		lines.forEach(target -> this.lines.get(target).styleLine(area, target));
	}

	public String getReformattedCode() {
		StringBuilder builder = new StringBuilder();
		boolean lastEmpty = false;
		String trim;
		for (MipsLine line : lines) {
			trim = line.getText().trim();
			if (trim.isEmpty()) {
				if (!lastEmpty) {
					lastEmpty = true;
					builder.append('\n');
				}
			} else {
				line.appendReformattedLine(builder);
				builder.append('\n');
				lastEmpty = false;
			}
		}
		String result = builder.toString();
		return result.isEmpty() ? result : result.substring(0, result.length() - 1);
	}

	private void parseLine(int start, int end, String line, MipsLine mipsLine) {

		//COMMENT
		int commentIndex = StringUtils.getCommentIndex(line);
		if (commentIndex != -1) {
			mipsLine.setComment(new DisplayComment(start + commentIndex, end, line.substring(commentIndex)));
			end = start + commentIndex;
			line = line.substring(0, commentIndex);
		}

		//LABEL
		int labelIndex = line.indexOf(":");
		if (labelIndex != -1) {
			mipsLine.setLabel(new DisplayLabel(start, start + labelIndex, line.substring(0, labelIndex + 1)));
			start = start + labelIndex + 1;
			line = line.substring(labelIndex + 1);
		}

		String trim = line.trim();
		if (trim.isEmpty()) return;
		//DIRECTIVE OR INSTRUCTION
		if (trim.startsWith(".")) parseDirective(start, line, mipsLine);
		else parseInstruction(start, line, mipsLine);
	}

	private void parseDirective(int start, String line, MipsLine mipsLine) {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(line, false, " ", ",", "\t");
		if (parts.isEmpty()) return;

		//Sorts all entries by their indices.
		List<Map.Entry<Integer, String>> stringParameters = parts.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

		//The first entry is the directive itself.
		Map.Entry<Integer, String> first = stringParameters.get(0);
		DisplayDirective directive = new DisplayDirective(start + first.getKey(), start + first.getKey()
				+ first.getValue().length(), first.getValue());

		mipsLine.setDirective(directive);
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
			directive.addParameter(parameter);
			parameterIndex++;
		}
	}

	private void parseInstruction(int start, String line, MipsLine mipsLine) {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(line, false, " ", ",", "\t");
		if (parts.isEmpty()) return;

		//Sorts all entries by their indices.
		List<Map.Entry<Integer, String>> stringParameters = parts.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

		//The first entry is the instruction itself.
		Map.Entry<Integer, String> first = stringParameters.get(0);
		DisplayInstruction instruction = new DisplayInstruction(start + first.getKey(), start + first.getKey()
				+ first.getValue().length(), first.getValue());
		mipsLine.setInstruction(instruction);
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
					DisplayInstructionParameterPart.InstructionParameterPartType.getByString(string, project));
			parameter.addPart(part);
		});
		return parameter;
	}

}
