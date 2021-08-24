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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents the type of {@link Project}. This type allows projects to be managed by
 * their respective {@link Project} class.
 *
 * @param <T> the project to load or create.
 */
public abstract class ProjectType<T extends Project> implements ManagerResource {

    protected final ResourceProvider provider;
    protected final String name;
    protected final IconData icon;
    protected final ObservableList<ProjectTemplateBuilder<?>> templateBuilders;

    /**
     * Creates the project type.
     *
     * @param provider the provider of the project type.
     * @param name     the name of the project type.
     * @param icon     the icon representing this type. It may be null.
     */
    public ProjectType(ResourceProvider provider, String name, IconData icon) {
        Validate.notNull(provider, "Provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        this.provider = provider;
        this.name = name;
        this.icon = icon;
        this.templateBuilders = FXCollections.observableList(new ArrayList<>());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return provider;
    }

    /**
     * Returns the {@link IconData icon} representing this type, if present.
     *
     * @return the {@link IconData icon}.
     */
    public Optional<IconData> getIcon() {
        return Optional.ofNullable(icon);
    }

    /**
     * Returns a modifiable list with all {@link ProjectTemplateBuilder} registered in this project type.
     * <p>
     * {@link ProjectTemplateBuilder}s are used to create {@link Project}s with custom parameters.
     * These builders also provide graphical nodes for the project creation window.
     * <p>
     * This list is observable. You can listen modifications of this list.
     *
     * @return the {@link ObservableList}.
     */
    public ObservableList<ProjectTemplateBuilder<?>> getTemplateBuilders() {
        return templateBuilders;
    }

    /**
     * Loads a project. This method loads all the data from the given folder and threat it as a project
     * of the represented type.
     * <p>
     * All missing files will be created with default data.
     *
     * @param folder the folder taht will contain the folder. This folder must already exist!
     * @return the new project.
     */
    public abstract T loadProject(File folder);
}
