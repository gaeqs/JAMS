package net.jamsimulator.jams.project.mips;

import net.jamsimulator.jams.project.ProjectType;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;

import java.io.File;

public class MIPSProjectType extends ProjectType<MIPSProject> {

    public static final String NAME = "mips";
    public static final MIPSProjectType INSTANCE = new MIPSProjectType();

    private MIPSProjectType() {
        super(NAME);
    }

    @Override
    public MIPSProject createProject(String name, File folder) {
        var project = new MIPSProject(name, folder);
        if (project.getData().configurations.isEmpty()) {
            //Add default configuration
            project.getData().addConfiguration(new MIPSSimulationConfiguration("Default"));
        }

        return project;
    }

    @Override
    public MIPSProject loadProject(File folder) {
        return new MIPSProject(folder);
    }
}
