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

package net.jamsimulator.jams.gui.util.log;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.PrintStream;
import java.time.LocalDateTime;

public class PrintStreamLog implements Log {

    private final PrintStream stream;
    private final SimpleStringProperty lastLineProperty;
    private final SimpleObjectProperty<LocalDateTime> lastLineTimeProperty;

    public PrintStreamLog(PrintStream stream) {
        this.stream = stream;
        this.lastLineProperty = new SimpleStringProperty(this, "");
        this.lastLineTimeProperty = new SimpleObjectProperty<>(LocalDateTime.now());
    }

    @Override
    public void print(Object object) {
        stream.print(object);
        lastLineProperty.set(lastLineProperty.get() + object);
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void println(Object object) {
        stream.println(object);
        lastLineProperty.set(object.toString());
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void printError(Object object) {
        stream.print(object);
        lastLineProperty.set(lastLineProperty.get() + object);
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void printErrorLn(Object object) {
        stream.println(object);
        lastLineProperty.set(object.toString());
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void printInfo(Object object) {
        stream.print(object);
        lastLineProperty.set(lastLineProperty.get() + object);
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void printInfoLn(Object object) {
        stream.println(object);
        lastLineProperty.set(object.toString());
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void printWarning(Object object) {
        stream.print(object);
        lastLineProperty.set(lastLineProperty.get() + object);
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void printWarningLn(Object object) {
        stream.println(object);
        lastLineProperty.set(object.toString());
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void printDone(Object object) {
        stream.print(object);
        lastLineProperty.set(lastLineProperty.get() + object);
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void printDoneLn(Object object) {
        stream.println(object);
        lastLineProperty.set(object.toString());
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void println() {
        stream.println();
        lastLineProperty.set("");
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public void clear() {
        lastLineProperty.set("");
        lastLineTimeProperty.set(LocalDateTime.now());
    }

    @Override
    public StringProperty lastLineProperty() {
        return lastLineProperty;
    }

    @Override
    public ObjectProperty<LocalDateTime> lastLineTimeProperty() {
        return lastLineTimeProperty;
    }
}
