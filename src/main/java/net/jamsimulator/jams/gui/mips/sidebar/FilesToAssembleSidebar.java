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

package net.jamsimulator.jams.gui.mips.sidebar;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.LanguageExplorerSection;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.project.mips.event.FileAddToAssembleEvent;
import net.jamsimulator.jams.project.mips.event.FileRemoveFromAssembleEvent;

import java.io.File;
import java.util.Comparator;

public class FilesToAssembleSidebar extends Explorer {

	private final Image icon;
	private final MIPSProject project;

	public FilesToAssembleSidebar(MIPSProject project, ScrollPane scrollPane) {
		super(scrollPane, true, true);
		this.project = project;

		project.getData().getFilesToAssemble().registerListeners(this, true);

		icon = Jams.getFileTypeManager().getByExtension("asm").map(FileType::getIcon).orElse(null);

		for (File file : project.getData().getFilesToAssemble().getFiles()) {
			mainSection.addElement(new FilesToAssembleSidebarElement(mainSection, file, this, icon));
		}

		hideMainSectionRepresentation();
	}

	/**
	 * Returns the {@link MIPSProject} handling the files.
	 *
	 * @return the {@link MIPSProject}.
	 */
	public MIPSProject getProject() {
		return project;
	}

	@Override
	protected void generateMainSection() {
		mainSection = new LanguageExplorerSection(this, null, "Files to assemble",
				0, Comparator.comparing(ExplorerElement::getName), Messages.BAR_FILES_TO_ASSEMBLE_NAME);
		getChildren().add(this.mainSection);
	}


	@Listener
	private void onFileAdd(FileAddToAssembleEvent.After event) {
		mainSection.addElement(new FilesToAssembleSidebarElement(mainSection, event.getFile(), this, icon));
	}

	@Listener
	private void onFileRemoved(FileRemoveFromAssembleEvent.After event) {
		mainSection.removeElementIf(element -> element instanceof FilesToAssembleSidebarElement
				&& ((FilesToAssembleSidebarElement) element).getFile().equals(event.getFile()));
	}
}
