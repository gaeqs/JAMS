package net.jamsimulator.jams.gui.util.log;

public interface Log {

	void print(Object object);

	void println(Object object);

	void printError(Object object);

	void printErrorLn(Object object);

	void printInfo(Object object);

	void printInfoLn(Object object);

	void printWarning(Object object);

	void printWarningLn(Object object);

	void printDone(Object object);

	void printDoneLn(Object object);

	void println();

	void clear();
}
