package net.jamsimulator.jams.mips.simulation.multicycle;

import javafx.scene.Node;

public enum MultiCycleStep {

	FETCH("F", "multi-cycle-fetch"),
	DECODE("D", "multi-cycle-decode"),
	EXECUTE("E", "multi-cycle-execute"),
	MEMORY("M", "multi-cycle-memory"),
	WRITE_BACK("W", "multi-cycle-write-back");

	private final String tag;
	private final String style;

	MultiCycleStep(String tag, String style) {
		this.tag = tag;
		this.style = style;
	}

	public String getTag() {
		return tag;
	}

	public String getStyle() {
		return style;
	}

	public static void removeAllStyles(Node node) {
		for (MultiCycleStep value : values()) {
			node.getStyleClass().remove(value.style);
		}
	}
}
