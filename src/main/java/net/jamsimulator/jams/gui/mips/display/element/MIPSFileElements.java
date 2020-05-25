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

import net.jamsimulator.jams.collection.Bag;
import net.jamsimulator.jams.project.mips.MIPSFilesToAssemble;
import net.jamsimulator.jams.project.mips.MipsProject;
import org.fxmisc.richtext.CodeArea;

import java.util.*;

/**
 * Represents a collection of assembly elements within an {@link net.jamsimulator.jams.mips.assembler.AssemblingFile}.
 */
public class MIPSFileElements {

	private final MipsProject project;
	private MIPSFilesToAssemble filesToAssemble;

	private final List<MIPSLine> lines;

	private final Bag<String> labels;
	private final Bag<String> setAsGlobalLabel;

	private final Set<Integer> requiresUpdate;

	public MIPSFileElements(MipsProject project) {
		this.project = project;

		this.lines = new ArrayList<>();
		this.labels = new Bag<>();
		this.setAsGlobalLabel = new Bag<>();

		this.requiresUpdate = new HashSet<>();
		this.filesToAssemble = null;
	}

	/**
	 * Returns the project of this file, if present.
	 *
	 * @return the project of this file, if present.
	 */
	public Optional<MipsProject> getProject() {
		return Optional.ofNullable(project);
	}

	/**
	 * Returns the {@link MIPSFilesToAssemble} this file is inside of, if present.
	 *
	 * @return the {@link MIPSFilesToAssemble}, if present.
	 */
	public Optional<MIPSFilesToAssemble> getFilesToAssemble() {
		return Optional.ofNullable(filesToAssemble);
	}

	/**
	 * Sets the {@link MIPSFilesToAssemble} this file is inside of.
	 * <p>
	 * This method should be used only by a {@link MIPSFilesToAssemble}.
	 *
	 * @param filesToAssemble the {@link MIPSFilesToAssemble}.
	 */
	public void setFilesToAssemble(MIPSFilesToAssemble filesToAssemble) {
		this.filesToAssemble = filesToAssemble;
	}

	/**
	 * Returns all lines of the represented file.
	 *
	 * @return the lines.
	 */
	public List<MIPSLine> getLines() {
		return lines;
	}

	/**
	 * Returns all labels registered on this file.
	 *
	 * @return the labels.
	 */
	public Bag<String> getLabels() {
		return labels;
	}

	/**
	 * Returns the labels that should be set as global labels.
	 *
	 * @return the labels.
	 */
	public Bag<String> getSetAsGlobalLabel() {
		return setAsGlobalLabel;
	}

	/**
	 * Returns all global labels that are defined by this file.
	 *
	 * @return the global labels.
	 */
	public Set<String> getExistingGlobalLabels() {
		Set<String> existingLabels = new HashSet<>();
		for (String label : setAsGlobalLabel) {
			if (labels.contains(label)) existingLabels.add(label);
		}
		return existingLabels;
	}

	/**
	 * Returns the element placed at the given index.
	 *
	 * @param index the index.
	 * @return the element, if found.
	 */
	public Optional<MIPSCodeElement> getElementAt(int index) {
		try {
			MIPSLine line = lines.get(lineOf(index));
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
		MIPSLine line;
		for (int i = 0; i < lines.size(); i++) {
			line = lines.get(i);
			if (line.getStart() <= index && line.getStart() + line.getText().length() >= index) return i;
		}
		return -1;
	}

	public boolean removeLine(int index) {
		if (index < 0 || index >= lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
		MIPSLine line = lines.remove(index);

		line.getLabel().ifPresent(label -> labels.remove(label.getLabel()));

		int length = line.getText().length() + 1;
		for (int i = index; i < lines.size(); i++)
			lines.get(i).move(-length);

		requiresUpdate.remove(lines.size());

		return checkLabels(line, false);
	}

	public boolean addLine(int index, String text) {
		if (index < 0 || index > lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
		if (text.contains("\n") || text.contains("\r")) throw new IllegalArgumentException("Invalid line!");

		int start = 0;
		if (index != 0) {
			MIPSLine previous = lines.get(index - 1);
			start = previous.getStart() + previous.getText().length() + 1;
		}

		MIPSLine line = new MIPSLine(this, start, text);
		lines.add(index, line);
		line.getLabel().ifPresent(label -> labels.add(label.getLabel()));

		int length = text.length() + 1;
		for (int i = index + 1; i < lines.size(); i++)
			lines.get(i).move(length);

		requiresUpdate.add(index);
		return checkLabels(line, true);
	}

	public boolean editLine(int index, String text) {
		if (index < 0 || index >= lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
		if (text.contains("\n") || text.contains("\r")) throw new IllegalArgumentException("Invalid line!");

		MIPSLine old = lines.get(index);
		int difference = text.length() - old.getText().length();

		MIPSLine line = new MIPSLine(this, old.getStart(), text);
		lines.set(index, line);

		for (int i = index + 1; i < lines.size(); i++)
			lines.get(i).move(difference);


		requiresUpdate.add(index);
		boolean a = checkLabels(old, false);
		boolean b = checkLabels(line, true);
		return a || b;
	}

	/**
	 * Refreshes all lines.
	 *
	 * @param raw the raw file string.
	 */
	public void refreshAll(String raw) {
		lines.clear();
		labels.clear();
		if (raw.isEmpty()) return;

		int start = 0;
		int end = 0;
		StringBuilder builder = new StringBuilder();

		//Checks all lines
		char c;
		MIPSLine line;
		while (raw.length() > end) {
			c = raw.charAt(end);
			if (c == '\n' || c == '\r') {
				line = new MIPSLine(this, start, builder.toString());
				line.getLabel().ifPresent(label -> labels.add(label.getLabel()));
				this.lines.add(line);
				//Restarts the builder.
				builder = new StringBuilder();
				start = end + 1;
			} else builder.append(c);
			end++;
		}

		//Final line
		if (end >= start) {
			line = new MIPSLine(this, start, builder.toString());
			line.getLabel().ifPresent(label -> labels.add(label.getLabel()));
			this.lines.add(line);
		}
	}

	/**
	 * Styles all lines.
	 *
	 * @param area the area to style.
	 */
	public void updateAndStyleAll(CodeArea area) {
		int i = 0;
		for (MIPSLine line : lines) {
			line.refreshMetadata(this);
			line.styleLine(area, i);
			i++;
		}
		requiresUpdate.clear();
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

	/**
	 * Applies all pending updates to the given area.
	 *
	 * @param area the {@link CodeArea}.
	 */
	public void update(CodeArea area) {
		MIPSLine line;
		for (Integer i : requiresUpdate) {
			if (i < 0 || i >= lines.size()) continue;
			line = lines.get(i);
			line.refreshMetadata(this);
			line.styleLine(area, i);
		}
		requiresUpdate.clear();
	}

	/**
	 * Adds to the update queue all lines containing any of the given labels.
	 *
	 * @param labelsToCheck the labels.
	 */
	public void searchForUpdates(List<String> labelsToCheck) {
		int i = 0;
		Set<String> used;
		for (MIPSLine mipsLine : lines) {
			if (mipsLine.getInstruction().isPresent()) {
				used = mipsLine.getInstruction().get().getUsedLabels();
				for (String label : labelsToCheck) {
					if (used.contains(label)) {
						requiresUpdate.add(i);
						break;
					}
				}
			}
			i++;
		}
	}


	private boolean checkLabels(MIPSLine line, boolean add) {
		List<String> labelsToCheck = new ArrayList<>();
		boolean globalLabelUpdated = false;

		//LABEL
		if (line.getLabel().isPresent()) {
			String label = line.getLabel().get().getLabel();
			if (add) labels.add(label);
			else labels.remove(label);
			labelsToCheck.add(label);

			globalLabelUpdated = setAsGlobalLabel.contains(label);
		}

		//DIRECTIVE
		if (line.getDirective().isPresent() && line.getDirective().get().isGlobl()) {
			line.getDirective().get().getParameters().forEach(target -> {
				if (add) setAsGlobalLabel.add(target.text);
				else setAsGlobalLabel.remove(target.text);
				labelsToCheck.add(target.text);
			});
			globalLabelUpdated |= !line.getDirective().get().getParameters().isEmpty();
		}

		searchForUpdates(labelsToCheck);
		return globalLabelUpdated;
	}

}
