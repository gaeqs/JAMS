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

package net.jamsimulator.jams.project;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import net.jamsimulator.jams.project.exception.MIPSTemplateBuildException;

/**
 * Represents the template of a {@link Project}. A project template is used to create new projects
 * modifying this template params.
 * <p>
 * This template also manages the {@link Node JavaFX representation}. This alows users to create
 * new projects easily.
 *
 * @param <E> the project to create.
 */
public abstract class ProjectTemplate<E extends Project> {

    protected final ProjectType<E> type;

    /**
     * Creates the project template.
     *
     * @param type the {@link ProjectType} representing the {@link Project} to create.
     */
    public ProjectTemplate(ProjectType<E> type) {
        this.type = type;
    }

    /**
     * Returns the {@link ProjectType} of the {@link Project} to create.
     *
     * @return the {@link ProjectType}.
     */
    public ProjectType<E> getType() {
        return type;
    }

    /**
     * Returns the {@link Node JavaFX representation} of this template. This allows users
     * to modify this template easily.
     *
     * @return the {@link Node JavaFX representation}.
     */
    public abstract Node getBuilderNode();

    /**
     * Returns a property representing whether this template has valid data and can create a new project.
     *
     * @return whether this template can create a new project with its current data.
     */
    public abstract BooleanProperty validProperty();

    /**
     * Creates a new {@link Project} with the data of this template.
     * <p>
     * If any error occurs during creation, this method throws a {@link MIPSTemplateBuildException}.
     *
     * @return the new {@link Project}.
     * @throws MIPSTemplateBuildException when any error occurs during creation.
     */
    public abstract E build() throws MIPSTemplateBuildException;

}
