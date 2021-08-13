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

package net.jamsimulator.jams.gui.action.context;

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

/**
 * Represents an action that can be shown in a {@link javafx.scene.control.ContextMenu}.
 */
public abstract class ContextAction extends Action implements ContextRegionable {

    private final IconData icon;
    private final ContextRegion contextRegion;
    private final MainMenuRegion mainMenuRegion;

    /**
     * Creates the context action.
     *
     * @param name               the name of the action. This name must be unique.
     * @param regionTag          the region tag of this action. This action will only interact on regions that support this tag.
     * @param languageNode       the language node of this action.
     * @param defaultCombination the default combination of keys that a user needs to press to execute this action.
     * @param contextRegion      the context region this action will be shown on.
     * @param mainMenuRegion     the main menu region this action will be shown on. May be null.
     * @param icon               the icon this action will show on the context menu or null.
     */
    public ContextAction(String name, String regionTag, String languageNode,
                         KeyCombination defaultCombination, ContextRegion contextRegion, MainMenuRegion mainMenuRegion, IconData icon) {
        super(name, regionTag, languageNode, defaultCombination);
        Validate.notNull(contextRegion, "Context region cannot be null!");
        this.contextRegion = contextRegion;
        this.mainMenuRegion = mainMenuRegion;
        this.icon = icon;
    }

    /**
     * Executes this action, being invoked from the main menu.
     */
    public abstract void runFromMenu();

    /**
     * Returns whether this action can be shown in the given {@link Explorer}.
     *
     * @param explorer the {@link Explorer}.
     * @return whether it's supported.
     */
    public abstract boolean supportsExplorerState(Explorer explorer);

    /**
     * Returns whether this action can be shown in the given {@link CodeFileEditor}.
     *
     * @param editor the {@link CodeFileEditor}.
     * @return whether it's supported.
     */
    public abstract boolean supportsTextEditorState(CodeFileEditor editor);

    /**
     * Returns whether this action can be shown in the given {@link MainMenuBar}.
     *
     * @param bar the {@link MainMenuBar}.
     * @return whether it's supported.
     */
    public abstract boolean supportsMainMenuState(MainMenuBar bar);

    @Override
    public ContextRegion getRegion() {
        return contextRegion;
    }

    @Override
    public Optional<MainMenuRegion> getMainMenuRegion() {
        return Optional.ofNullable(mainMenuRegion);
    }

    public Optional<IconData> getIcon() {
        return Optional.ofNullable(icon);
    }

    @Override
    public int compareTo(ContextRegionable o) {
        int comp = getRegion().compareTo(o.getRegion());
        if (comp == 0) {
            return getName().compareTo(o.getName());
        }
        return comp;
    }
}
