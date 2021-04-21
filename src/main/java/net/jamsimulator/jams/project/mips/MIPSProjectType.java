package net.jamsimulator.jams.project.mips;

import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.image.icon.IconManager;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.project.ProjectType;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;

import java.io.File;

public class MIPSProjectType extends ProjectType<MIPSProject> {

    public static final String NAME = "MIPS";
    public static final Image ICON = IconManager.INSTANCE.getOrLoadSafe(Icons.PROJECT_TYPE_MIPS).orElse(null);
    public static final MIPSProjectType INSTANCE = new MIPSProjectType();

    private MIPSProjectType() {
        super(NAME, ICON);
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
