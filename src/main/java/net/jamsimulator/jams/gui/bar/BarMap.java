package net.jamsimulator.jams.gui.bar;

import net.jamsimulator.jams.utils.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class BarMap {

	private final HashMap<BarType, Bar> bars;
	private BiConsumer<BarType, BarButton> onPut;

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

	public Optional<BarType> getType(Bar bar) {
		for (Map.Entry<BarType, Bar> entry : bars.entrySet()) {
			if (entry.getValue().equals(bar)) return Optional.ofNullable(entry.getKey());
		}
		return Optional.empty();
	}

	public Optional<? extends BarButton> searchButton(String name) {
		Optional<? extends BarButton> optional = Optional.empty();

		for (Bar bar : bars.values()) {
			optional = bar.get(name);
			if (optional.isPresent()) return optional;
		}

		return optional;
	}

	public void callPutEvent(Bar bar, BarButton button) {
		Optional<BarType> optional = getType(bar);
		if (optional.isPresent() && this.onPut != null) {
			this.onPut.accept(optional.get(), button);
		}
	}

	public void setOnPut(BiConsumer<BarType, BarButton> onPut) {
		Validate.notNull(onPut, "Listener cannot be null!");
		this.onPut = onPut;
	}

	public void addOnPut(BiConsumer<BarType, BarButton> onPut) {
		Validate.notNull(onPut, "Listener cannot be null!");
		this.onPut = this.onPut == null ? onPut : this.onPut.andThen(onPut);
	}
}
