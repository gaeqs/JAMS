package net.jamsimulator.jams.gui.mips.configuration;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.event.MIPSSimulationConfigurationAddEvent;
import net.jamsimulator.jams.project.mips.event.MIPSSimulationConfigurationRemoveEvent;

import java.util.Comparator;

public class MIPSConfigurationsListContents extends Explorer {

	private final MIPSConfigurationWindow window;

	public MIPSConfigurationsListContents(ScrollPane scrollPane, MIPSConfigurationWindow window) {
		super(scrollPane, false, false);
		this.window = window;

		generateMainSection();
		hideMainSectionRepresentation();

		window.getProjectData().registerListeners(this, true);
	}

	public void refreshName(MIPSSimulationConfiguration configuration) {
		mainSection.removeElementIf(target -> target instanceof Representation
				&& ((Representation) target).configuration.equals(configuration));

		var representation = new Representation(mainSection, configuration);
		mainSection.addElement(representation);
		selectElementAlone(representation);
	}

	public void selectFirst() {
		if (!mainSection.isEmpty()) {
			mainSection.getElementByIndex(0).ifPresent(this::selectElementAlone);
		}
	}

	@Override
	protected void generateMainSection() {
		mainSection = new ExplorerSection(this, null, "", 0, Comparator.comparing(ExplorerElement::getName));

		var data = window.getProjectData();
		for (var config : data.getConfigurations()) {
			mainSection.addElement(new Representation(mainSection, config));
		}

		getChildren().add(mainSection);
	}

	@Listener
	private void onConfigurationAdd(MIPSSimulationConfigurationAddEvent.After event) {
		boolean wasEmpty = mainSection.isEmpty();
		var representation = new Representation(mainSection, event.getMipsSimulationConfiguration());
		mainSection.addElement(representation);
		if (wasEmpty) selectElementAlone(representation);
	}

	@Listener
	private void onConfigurationRemove(MIPSSimulationConfigurationRemoveEvent.After event) {
		mainSection.removeElementIf(target -> target instanceof Representation
				&& ((Representation) target).configuration.equals(event.getMipsSimulationConfiguration()));
	}

	public class Representation extends ExplorerBasicElement {

		private final MIPSSimulationConfiguration configuration;

		public Representation(ExplorerSection parent, MIPSSimulationConfiguration configuration) {
			super(parent, configuration.getName(), 1);
			this.configuration = configuration;
		}

		public MIPSSimulationConfiguration getConfiguration() {
			return configuration;
		}

		@Override
		public void select() {
			super.select();
			window.display(configuration);
		}
	}

}
