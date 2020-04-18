package net.jamsimulator.jams.utils;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class KeyCombinationBuilder {

	private final KeyCode code;
	private final boolean shift, alt, meta, shortcut;

	public KeyCombinationBuilder(KeyCode code, boolean shift, boolean alt, boolean meta, boolean shortcut) {
		this.code = code;
		this.shift = shift;
		this.alt = alt;
		this.meta = meta;
		this.shortcut = shortcut;
	}

	public KeyCombinationBuilder(KeyEvent event) {
		this.code = event.getCode();
		this.shift = event.isShiftDown();
		this.alt = event.isAltDown();
		this.meta = event.isMetaDown();
		this.shortcut = event.isShortcutDown();
	}

	public KeyCode getCode() {
		return code;
	}

	public boolean isShift() {
		return shift;
	}

	public boolean isAlt() {
		return alt;
	}

	public boolean isMeta() {
		return meta;
	}

	public boolean isShortcut() {
		return shortcut;
	}

	public KeyCodeCombination build() {
		List<KeyCombination.Modifier> modifiers = new ArrayList<>();
		if (shift) modifiers.add(KeyCombination.SHIFT_DOWN);
		if (alt) modifiers.add(KeyCombination.ALT_DOWN);
		if (meta) modifiers.add(KeyCombination.META_DOWN);
		if (shortcut) modifiers.add(KeyCombination.SHORTCUT_DOWN);
		return new KeyCodeCombination(code, modifiers.toArray(new KeyCombination.Modifier[0]));
	}
}
