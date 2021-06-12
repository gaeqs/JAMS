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

import java.io.PrintStream;

public class PrintStreamLog implements Log {

    private final PrintStream stream;

    public PrintStreamLog(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void print(Object object) {
        stream.print(object);
    }

    @Override
    public void println(Object object) {
        stream.println(object);
    }

    @Override
    public void printError(Object object) {
        stream.print(object);
    }

    @Override
    public void printErrorLn(Object object) {
        stream.println(object);
    }

    @Override
    public void printInfo(Object object) {
        stream.print(object);
    }

    @Override
    public void printInfoLn(Object object) {
        stream.println(object);
    }

    @Override
    public void printWarning(Object object) {
        stream.print(object);
    }

    @Override
    public void printWarningLn(Object object) {
        stream.println(object);
    }

    @Override
    public void printDone(Object object) {
        stream.print(object);
    }

    @Override
    public void printDoneLn(Object object) {
        stream.println(object);
    }

    @Override
    public void println() {
        stream.println();
    }

    @Override
    public void clear() {
    }
}
