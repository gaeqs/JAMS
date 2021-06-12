/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.project.mips;

import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.image.icon.IconManager;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.project.ProjectType;

import java.io.File;

public class MIPSProjectType extends ProjectType<MIPSProject> {

    public static final String NAME = "MIPS";
    public static final Image ICON = IconManager.INSTANCE.getOrLoadSafe(Icons.PROJECT_TYPE_MIPS).orElse(null);
    public static final MIPSProjectType INSTANCE = new MIPSProjectType();

    private MIPSProjectType() {
        super(NAME, ICON);
        templateBuilders.add(new MIPSEmptyProjectTemplate.Builder());
    }

    @Override
    public MIPSProject loadProject(File folder) {
        return new MIPSProject(folder);
    }
}
