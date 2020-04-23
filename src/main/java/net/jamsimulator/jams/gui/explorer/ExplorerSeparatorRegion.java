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

package net.jamsimulator.jams.gui.explorer;

import javafx.scene.layout.Region;

/**
 * Small class used to add padding to an {@link Explorer}.
 */
public class ExplorerSeparatorRegion extends Region {

	public static final int HIERARCHY_SEPARATOR_SIZE_FILE = 38;
	public static final int HIERARCHY_SEPARATOR_SIZE_FOLDER = 20;

	public ExplorerSeparatorRegion(boolean folder, int hierarchyLevel) {
		double width;
		if (folder)
			width = HIERARCHY_SEPARATOR_SIZE_FOLDER * hierarchyLevel;
		else
			width = HIERARCHY_SEPARATOR_SIZE_FOLDER * (hierarchyLevel - 1) + HIERARCHY_SEPARATOR_SIZE_FILE;
		setPrefWidth(width);
		setMinWidth(width);
	}

	public ExplorerSeparatorRegion(double width) {
		setPrefWidth(width);
	}

}
