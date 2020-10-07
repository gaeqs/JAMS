package net.jamsimulator.jams.gui.mips.configuration.cache;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.util.value.ValueEditor;
import net.jamsimulator.jams.gui.util.value.ValueEditors;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;

import java.util.ArrayList;
import java.util.List;

public class MIPSConfigurationCacheContents extends Explorer {

	private final MIPSConfigurationDisplayCacheTab cacheTab;
	private final List<Representation> representations;

	public MIPSConfigurationCacheContents(ScrollPane scrollPane, MIPSConfigurationDisplayCacheTab cacheTab) {
		super(scrollPane, false, false);
		this.cacheTab = cacheTab;
		this.representations = new ArrayList<>();

		generateMainSection();
		hideMainSectionRepresentation();
	}

	public void selectFirst() {
		if (!mainSection.isEmpty()) {
			mainSection.getElementByIndex(0).ifPresent(this::selectElementAlone);
		}
	}

	public void add(CacheBuilder<?> builder) {
		boolean wasEmpty = mainSection.isEmpty();
		var representation = new Representation(mainSection, representations.size(), builder);
		mainSection.addElement(representation);
		representations.add(representation);
		if (wasEmpty) {
			selectElementAlone(representation);
		}
	}

	public void remove(Representation representation) {
		mainSection.removeElement(representation);
		representations.remove(representation);


		for (Representation r : representations) {
			if (r.getIndex() > representation.getIndex()) {
				r.setIndex(r.getIndex() - 1);
			}
		}
	}

	public void sort() {
		mainSection.refreshAllElements();
	}

	public void reload() {
		representations.clear();
		mainSection.clear();
		cacheTab.getConfiguration().getCacheBuilders().forEach(this::add);
		if (mainSection.isEmpty()) {
			cacheTab.display(null);
		}
	}

	@Override
	protected void generateMainSection() {
		mainSection = new ExplorerSection(this, null, "", 0, (o1, o2) -> {
			if (!(o1 instanceof Representation)) return -1;
			if (!(o2 instanceof Representation)) return 1;
			return ((Representation) o1).getIndex() - ((Representation) o2).getIndex();
		});

		reload();
		getChildren().add(mainSection);
	}

	public class Representation extends ExplorerBasicElement {

		private int index;
		private CacheBuilder<?> builder;
		private ValueEditor<CacheBuilder> cacheEditor;
		private Label label;

		public Representation(ExplorerSection parent, int index, CacheBuilder<?> builder) {
			super(parent, String.valueOf(builder.getName()), 1);
			this.index = index;
			this.builder = builder;

			label.setText(String.valueOf(index));

			cacheEditor.setCurrentValueUnsafe(builder);
			cacheEditor.addListener(this::manageCacheChange);
		}

		public CacheBuilder<?> getBuilder() {
			return builder;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
			label.setText(String.valueOf(index));
		}

		@Override
		public void select() {
			if (selected) return;
			getStyleClass().add("selected-explorer-element");
			selected = true;
			cacheTab.display(builder);
		}

		@Override
		protected void loadElements() {
			cacheEditor = ValueEditors.getByTypeUnsafe(CacheBuilder.class).build();
			var cacheNode = cacheEditor.getAsNode();

			label = new Label();
			label.setAlignment(Pos.CENTER);
			label.setPrefWidth(30);
			label.setMinWidth(30);

			getChildren().addAll(label, cacheNode);
			setSpacing(SPACING);
			setAlignment(Pos.CENTER_LEFT);

			((Region) cacheNode).setPrefWidth(1000000);

			cacheNode.focusedProperty().addListener((obs, old, val) -> {
				if (val) {
					selectElementAlone(this);
				}
			});
		}

		private void manageCacheChange(CacheBuilder<?> builder) {
			this.builder = builder.makeNewInstance();

			var caches = cacheTab.getConfiguration().getCacheBuilders();
			caches.set(index, this.builder);

			if (selected) {
				cacheTab.display(this.builder);
			}
		}
	}

}
