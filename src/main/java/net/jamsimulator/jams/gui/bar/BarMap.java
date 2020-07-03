package net.jamsimulator.jams.gui.bar;

import java.util.HashMap;
import java.util.Optional;

public class BarMap {

	private final HashMap<BarType, Bar> bars;

	public BarMap() {
		bars = new HashMap<>();
	}

	public Bar put(BarType type, Bar bar) {
		bar.setBarMap(this);
		return bars.put(type, bar);
	}

	public Optional<Bar> get(BarType type) {
		return Optional.ofNullable(bars.get(type));
	}

	public Optional<? extends BarButton> searchButton(String name) {
		Optional<? extends BarButton> optional = Optional.empty();

		for (Bar bar : bars.values()) {
			optional = bar.get(name);
			if (optional.isPresent()) return optional;
		}

		return optional;
	}

}
