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

import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;

import java.io.File;

public class FilesToAssembleSidebarElement extends ExplorerBasicElement {

	private final FilesToAssembleSidebar display;
	private final File file;

	/**
	 * Creates an files to assemble display's element.
	 *
	 * @param parent  the {@link ExplorerSection} containing this element.
	 * @param file    the represented file.
	 * @param display the {@link FilesToAssembleSidebar} this elements is being displayed on.
	 * @param icon    the displayed icon.
	 */
	public FilesToAssembleSidebarElement(ExplorerSection parent, File file, FilesToAssembleSidebar display, Image icon) {
		super(parent, display.getProject().getFolder().toPath().relativize(file.toPath()).toString(), 1);
		this.display = display;
		this.file = file;
		this.icon.setImage(icon);
	}

	public FilesToAssembleSidebar getDisplay() {
		return display;
	}

	public File getFile() {
		return file;
	}

	@Override
	public boolean supportsActionRegion(String region) {
		return super.supportsActionRegion(region) || RegionTags.MIPS_FILE_TO_ASSEMBLE.equals(region);
	}
}
