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

package net.jamsimulator.jams.gui.editor.code.indexing.element;

/**
 * Represents the scope of an element.
 * <p>
 * An element with a GLOBAL scope can be referenced from all places inside the project.
 * <p>
 * An element with a FILE scope can be referenced from all places inside its file.
 * <p>
 * An element with a MACRO scope can only be reference from inside its macro.
 */
public record ElementScope(Type type, String macroIdentifier) {

    /**
     * An element with this scope can be reached from all places inside the project.
     */
    public static final ElementScope GLOBAL = new ElementScope(Type.GLOBAL);

    /**
     * An element with this scope can only be reached from inside its macro.
     */
    public static final ElementScope FILE = new ElementScope(Type.FILE);

    /**
     * Internal type. This scope can reach ALL elements, including macros.
     * Other scopes can't reach this scope.
     */
    public static final ElementScope INTERNAL = new ElementScope(Type.INTERNAL);

    /**
     * Creates a new scope.
     *
     * @param type the {@link Type scope type}.
     */
    private ElementScope(Type type) {
        this(type, null);
    }

    /**
     * Checks if this scope can be reached from the given scope.
     * <p>
     * WARINING! Scopes doesn't record the file identifier, so this method won't
     * check if the scopes are in the same file.
     *
     * @param scope the scope that wants to reach.
     * @return whether this scope can be reached.
     */
    public boolean canBeReachedFrom(ElementScope scope) {
        if (scope.type.ordinal() > type.ordinal()) return true;
        if (scope.type.ordinal() == type.ordinal()) {
            if (type != Type.MACRO) return true;
            return scope.macroIdentifier.equals(macroIdentifier);
        }
        return false;
    }

    /**
     * The scope file.
     */
    public enum Type {
        /**
         * An element with this scope can be reached from all places inside the project.
         */
        GLOBAL,

        /**
         * An element with this scope can be reached from all places inside the same file.
         */
        FILE,

        /**
         * An element with this scope can only be reached from inside its macro.
         */
        MACRO,

        /**
         * Internal type. This scope can reach ALL elements, including macros.
         * Other scopes can't reach this scope.
         */
        INTERNAL
    }

}
