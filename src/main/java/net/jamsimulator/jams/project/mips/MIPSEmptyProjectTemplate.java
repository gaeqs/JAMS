package net.jamsimulator.jams.project.mips;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.gui.configuration.ConfigurationRegionDisplay;
import net.jamsimulator.jams.gui.util.PathAndNameEditor;
import net.jamsimulator.jams.gui.util.propertyeditor.PropertyEditors;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.project.ProjectData;
import net.jamsimulator.jams.project.ProjectTemplate;
import net.jamsimulator.jams.project.ProjectTemplateBuilder;
import net.jamsimulator.jams.project.exception.MIPSTemplateBuildException;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.utils.FolderUtils;

import java.io.File;

public class MIPSEmptyProjectTemplate extends ProjectTemplate<MIPSProject> {

    private final PathAndNameEditor editor;

    private final Property<AssemblerBuilder> assemblerBuilderProperty;
    private final Property<InstructionSet> instructionSetProperty;
    private final Property<DirectiveSet> directiveSetProperty;
    private final Property<RegistersBuilder> registersBuilderProperty;

    public MIPSEmptyProjectTemplate() {
        super(MIPSProjectType.INSTANCE);

        assemblerBuilderProperty = new SimpleObjectProperty<>(null,
                "assembler", Jams.getAssemblerBuilderManager().getDefault());
        instructionSetProperty = new SimpleObjectProperty<>(null,
                "instructionSet", Jams.getInstructionSetManager().getDefault());
        directiveSetProperty = new SimpleObjectProperty<>(null,
                "directiveSet", Jams.getDirectiveSetManager().getDefault());
        registersBuilderProperty = new SimpleObjectProperty<>(null,
                "registers", Jams.getRegistersBuilderManager().getDefault());

        editor = new PathAndNameEditor();

        PropertyEditors.getEditor(assemblerBuilderProperty).ifPresent(p ->
                editor.addEntry(Messages.PROJECT_CREATOR_MIPS_ASSEMBLER, p.thisInstanceAsNode()));
        PropertyEditors.getEditor(instructionSetProperty).ifPresent(p ->
                editor.addEntry(Messages.PROJECT_CREATOR_MIPS_INSTRUCTION_SET, p.thisInstanceAsNode()));
        PropertyEditors.getEditor(directiveSetProperty).ifPresent(p ->
                editor.addEntry(Messages.PROJECT_CREATOR_MIPS_DIRECTIVE_SET, p.thisInstanceAsNode()));
        PropertyEditors.getEditor(registersBuilderProperty).ifPresent(p ->
                editor.addEntry(Messages.PROJECT_CREATOR_MIPS_REGISTERS, p.thisInstanceAsNode()));

        editor.getChildren().add(0, new ConfigurationRegionDisplay(Builder.LANGUAGE_NODE));
        var region = new Region();
        region.setPrefHeight(20);
        editor.getChildren().add(1, region);
    }


    public AssemblerBuilder getAssemblerBuilder() {
        return assemblerBuilderProperty.getValue();
    }

    public void setAssemblerBuilder(AssemblerBuilder assemblerBuilder) {
        assemblerBuilderProperty.setValue(assemblerBuilder);
    }

    public InstructionSet getInstructionSet() {
        return instructionSetProperty.getValue();
    }

    public void setInstructionSet(InstructionSet instructionSet) {
        instructionSetProperty.setValue(instructionSet);
    }

    public DirectiveSet getDirectiveSet() {
        return directiveSetProperty.getValue();
    }

    public void setDirectiveSet(DirectiveSet directiveSet) {
        directiveSetProperty.setValue(directiveSet);
    }

    public RegistersBuilder getRegistersBuilder() {
        return registersBuilderProperty.getValue();
    }

    public void setRegistersBuilder(RegistersBuilder builder) {
        registersBuilderProperty.setValue(builder);
    }

    @Override
    public Node getBuilderNode() {
        return editor;
    }

    @Override
    public BooleanProperty validProperty() {
        return editor.validProperty();
    }

    @Override
    public MIPSProject build() throws MIPSTemplateBuildException {
        var folder = new File(editor.getPath());
        if (!FolderUtils.checkFolder(folder))
            throw new MIPSTemplateBuildException("Couldn't create folder " + editor.getPath() + "!");
        var metadataFolder = new File(folder, ProjectData.METADATA_FOLDER_NAME);
        if (!FolderUtils.checkFolder(metadataFolder))
            throw new MIPSTemplateBuildException("Couldn't create folder " + metadataFolder.getAbsolutePath() + "!");

        var metadataFile = new File(metadataFolder, ProjectData.METADATA_DATA_NAME);

        try {
            var config = new RootConfiguration(metadataFile);
            config.set(ProjectData.NAME_NODE, editor.getName());
            config.convertAndSet(MIPSProjectData.NODE_ASSEMBLER, assemblerBuilderProperty.getValue(), AssemblerBuilder.class);
            config.convertAndSet(MIPSProjectData.NODE_REGISTERS, registersBuilderProperty.getValue(), RegistersBuilder.class);
            config.convertAndSet(MIPSProjectData.NODE_DIRECTIVES, directiveSetProperty.getValue(), DirectiveSet.class);
            config.convertAndSet(MIPSProjectData.NODE_INSTRUCTIONS, instructionSetProperty.getValue(), InstructionSet.class);
            new MIPSSimulationConfiguration("Default").save(config, MIPSProjectData.NODE_CONFIGURATIONS);
            return new MIPSProject(folder);
        } catch (Exception e) {
            throw new MIPSTemplateBuildException(e);
        }

    }

    public static class Builder extends ProjectTemplateBuilder<MIPSProject> {

        public static final String NAME = "mips-empty";
        public static final String LANGUAGE_NODE = Messages.PROJECT_CREATOR_MIPS_EMPTY;
        public static final Image ICON = MIPSProjectType.ICON;

        public Builder() {
            super(NAME, LANGUAGE_NODE, ICON);
        }

        @Override
        public ProjectTemplate<MIPSProject> createBuilder() {
            return new MIPSEmptyProjectTemplate();
        }
    }
}
