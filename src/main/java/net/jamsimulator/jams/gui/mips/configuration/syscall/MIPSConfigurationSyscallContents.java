package net.jamsimulator.jams.gui.mips.configuration.syscall;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.util.value.ValueEditor;
import net.jamsimulator.jams.gui.util.value.ValueEditors;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.ArrayList;
import java.util.List;

public class MIPSConfigurationSyscallContents extends Explorer {

	private final MIPSConfigurationDisplaySyscallTab syscallTab;
	private final List<Representation> representations;

	public MIPSConfigurationSyscallContents(ScrollPane scrollPane, MIPSConfigurationDisplaySyscallTab syscallTab) {
		super(scrollPane, false, false);
		this.syscallTab = syscallTab;
		this.representations = new ArrayList<>();

		generateMainSection();
		hideMainSectionRepresentation();
	}

	public void selectFirst() {
		if (!mainSection.isEmpty()) {
			mainSection.getElementByIndex(0).ifPresent(this::selectElementAlone);
		}
	}

	public int getBiggestId() {
		int max = 0;
		for (Representation representation : representations) {
			if (representation.getSyscallId() > max) {
				max = representation.getSyscallId();
			}
		}
		return max;
	}

	public void add(int id, SyscallExecutionBuilder<?> builder) {
		boolean wasEmpty = mainSection.isEmpty();
		var representation = new Representation(mainSection, id, builder);
		mainSection.addElement(representation);
		representations.add(representation);
		if(wasEmpty) {
			selectElementAlone(representation);
		}
	}

	public void remove(Representation representation) {
		mainSection.removeElement(representation);
		representations.remove(representation);
	}

	public void sort() {
		mainSection.refreshAllElements();
	}

	public void reload () {
		representations.clear();
		mainSection.clear();
		var data = syscallTab.getConfiguration().getSyscallExecutionBuilders();
		data.forEach(this::add);
	}

	@Override
	protected void generateMainSection() {
		mainSection = new ExplorerSection(this, null, "", 0, (o1, o2) -> {
			if(!(o1 instanceof Representation)) return -1;
			if(!(o2 instanceof Representation)) return 1;
			return ((Representation) o1).getSyscallId() - ((Representation) o2).getSyscallId();
		});

		reload();
		getChildren().add(mainSection);
	}

	private boolean isIdValid(int id) {
		for (Representation representation : representations) {
			if (representation.getSyscallId() == id) {
				return false;
			}
		}
		return true;
	}

	public class Representation extends ExplorerBasicElement {

		private int syscallId;
		private SyscallExecutionBuilder<?> builder;

		private ValueEditor<Integer> idEditor;
		private ValueEditor<SyscallExecutionBuilder> syscallEditor;

		public Representation(ExplorerSection parent, int syscallId, SyscallExecutionBuilder<?> builder) {
			super(parent, String.valueOf(syscallId), 1);
			this.syscallId = syscallId;
			this.builder = builder;

			idEditor.setCurrentValueUnsafe(syscallId);
			syscallEditor.setCurrentValueUnsafe(builder);
		}

		public int getSyscallId() {
			return syscallId;
		}

		public SyscallExecutionBuilder<?> getBuilder() {
			return builder;
		}

		@Override
		public void select() {
			if (selected) return;
			getStyleClass().add("selected-explorer-element");
			selected = true;
			syscallTab.display(builder);
		}

		@Override
		protected void loadElements() {
			idEditor = ValueEditors.getByTypeUnsafe(int.class).build();
			syscallEditor = ValueEditors.getByTypeUnsafe(SyscallExecutionBuilder.class).build();
			var idNode = idEditor.getAsNode();
			var syscallNode = syscallEditor.getAsNode();

			getChildren().addAll(idNode, syscallNode);
			setSpacing(SPACING);
			setAlignment(Pos.CENTER_LEFT);

			((Region) idEditor).setPrefWidth(50);
			((Region) idEditor).setMinWidth(50);
			((Region) syscallNode).setPrefWidth(1000000);


			idEditor.addListener(this::manageIdChange);
			syscallEditor.addListener(this::manageSyscallChange);

			idNode.focusedProperty().addListener((obs, old, val) -> {
				if (val) {
					selectElementAlone(this);
				}
			});
			syscallNode.focusedProperty().addListener((obs, old, val) -> {
				if (val) {
					selectElementAlone(this);
				}
			});
		}

		private void manageIdChange(int value) {
			if (syscallId == value) return;

			if (!isIdValid(value)) {
				idEditor.setCurrentValueUnsafe(syscallId);
				return;
			}

			var builders = syscallTab.getConfiguration().getSyscallExecutionBuilders();
			builders.remove(syscallId);
			builders.put(value, builder);

			syscallId = value;
		}

		private void manageSyscallChange(SyscallExecutionBuilder<?> builder) {
			this.builder = builder;

			var builders = syscallTab.getConfiguration().getSyscallExecutionBuilders();
			builders.put(syscallId, builder);
		}
	}

}
