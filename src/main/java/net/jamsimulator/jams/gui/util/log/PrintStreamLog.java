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
