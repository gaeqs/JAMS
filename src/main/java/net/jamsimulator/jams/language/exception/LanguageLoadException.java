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

package net.jamsimulator.jams.language.exception;

public class LanguageLoadException extends Exception {

    public Type type;

    public LanguageLoadException(Type type) {
        super(type.message);
        this.type = type;
    }

    public LanguageLoadException(Throwable cause, Type type) {
        super(type.message, cause);
        this.type = type;
    }

    public LanguageLoadException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, Type type) {
        super(type.message, cause, enableSuppression, writableStackTrace);
        this.type = type;
    }

    public enum Type {

        RESOURCE_NOT_FOUND("Resource not found."),
        INVALID_RESOURCE("Invalid resource."),
        INVALID_HEADER("Invalid header."),
        ALREADY_LOADED("Language already loaded."),
        NOT_LOADED("Language not loaded."),
        UNKNOWN("Unknown.");

        private final String message;

        Type(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
