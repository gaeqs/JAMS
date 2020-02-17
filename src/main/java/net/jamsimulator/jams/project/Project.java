package net.jamsimulator.jams.project;

import net.jamsimulator.jams.mips.simulation.Simulation;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Project {


	String getName();

	List<File> getFilesToAssemble();

	Simulation assemble() throws IOException;

}
