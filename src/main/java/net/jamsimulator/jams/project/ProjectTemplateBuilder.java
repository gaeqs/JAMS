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

import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

/**
 * This class creates {@link ProjectTemplate}s of a certain type.
 * <p>
 * This class also contains the common data for these templates, such the name, the language node or the icon.
 *
 * @param <T> the project the {@link ProjectTemplate}s create.
 */
public abstract class ProjectTemplateBuilder<T extends Project> {

    protected final String name;
    protected final String languageNode;
    protected final IconData icon;

    /**
     * Creates the new template builder.
     *
     * @param name         the name of the template.
     * @param languageNode the language node or null.
     * @param icon         the icon or null.
     */
    public ProjectTemplateBuilder(String name, String languageNode, IconData icon) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
        this.languageNode = languageNode;
        this.icon = icon;
    }

    /**
     * Returns the name representing the {@link ProjectTemplate}s.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the language node representing the {@link ProjectTemplate}s if present.
     *
     * @return the language node if present.
     */
    public Optional<String> getLanguageNode() {
        return Optional.ofNullable(languageNode);
    }

    /**
     * Returns the {@link Image icon} representing the {@link ProjectTemplate}s if present.
     *
     * @return the {@link Image icon} if present.
     */
    public Optional<IconData> getIcon() {
        return Optional.ofNullable(icon);
    }

    /**
     * Creates a {@link ProjectTemplate} of the represented type of this builder.
     *
     * @return the {@link ProjectTemplate}.
     */
    public abstract ProjectTemplate<T> createBuilder();
}
