package net.jamsimulator.jams.gui.mips.configuration;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.configuration.ConfigurationRegionDisplay;
import net.jamsimulator.jams.gui.util.value.ValueEditors;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfigurationNodePreset;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfigurationPresets;

import java.util.ArrayList;
import java.util.List;

public class MIPSConfigurationDisplayGeneralTab extends VBox {

	private final MIPSSimulationConfiguration configuration;
	private final List<Representation> representations;

	public MIPSConfigurationDisplayGeneralTab(MIPSSimulationConfiguration configuration) {
		setPadding(new Insets(5));
		setSpacing(5);

		this.configuration = configuration;

		Architecture architecture = configuration.getNodeValue(MIPSSimulationConfigurationPresets.ARCHITECTURE);

		representations = new ArrayList<>();


		getChildren().add(new ConfigurationRegionDisplay(Messages.SIMULATION_CONFIGURATION_GENERAL_REGION));
		configuration.getNodes().forEach((preset, value) -> {
			var representation = new Representation(preset, value);
			representation.refreshView(architecture);
			representations.add(representation);
		});

		representations.sort((o1, o2) -> o2.getPreset().getPriority() - o1.getPreset().getPriority());
		representations.stream().filter(Node::isVisible).forEach(getChildren()::add);
	}

	private void update(MIPSSimulationConfigurationNodePreset preset, Object value) {
		configuration.setNodeValue(preset.getName(), value);

		var name = preset.getName();

		if (preset.getType() == Architecture.class) {
			getChildren().clear();
			for (Representation representation : representations) {
				representation.refreshView((Architecture) value);
				representation.refreshEnabled(preset.getName(), value);
				if (representation.isVisible()) {
					getChildren().add(representation);
				}
			}
		} else {
			for (Node child : getChildren()) {
				if (child instanceof Representation) {
					((Representation) child).refreshEnabled(name, value);
				}
			}
		}
	}

	private class Representation extends HBox {

		private final MIPSSimulationConfigurationNodePreset preset;

		public Representation(MIPSSimulationConfigurationNodePreset preset, Object value) {
			setSpacing(5);
			setAlignment(Pos.CENTER_LEFT);

			this.preset = preset;
			var label = new LanguageLabel(preset.getLanguageNode());
			label.setTooltip(new LanguageTooltip(preset.getLanguageNode() + "_TOOLTIP"));
			var editor = ValueEditors.getByTypeUnsafe(preset.getType()).build();

			if (value instanceof Boolean) {
				label.setOnMouseClicked(event -> editor.setCurrentValueUnsafe(!(boolean) editor.getCurrentValue()));
				getChildren().addAll(editor.getAsNode(), label);
			} else {
				getChildren().addAll(label, editor.getAsNode());
			}

			editor.setCurrentValueUnsafe(value);
			editor.addListener(v -> update(preset, v));
		}

		public MIPSSimulationConfigurationNodePreset getPreset() {
			return preset;
		}

		public void refreshView(Architecture architecture) {
			setVisible(preset.supportArchitecture(architecture));
		}

		public void refreshEnabled(String node, Object value) {
			setDisabled(!preset.supportsNode(node, value));
		}

	}
}
