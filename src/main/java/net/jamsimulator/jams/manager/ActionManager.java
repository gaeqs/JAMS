package net.jamsimulator.jams.manager;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.TaggedRegion;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.defaults.texteditor.TextEditorActionReformat;
import net.jamsimulator.jams.gui.action.defaults.texteditor.TextEditorActionSave;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.action.event.ActionRegisterEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnbindEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnregisterEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

public class ActionManager extends SimpleEventBroadcast {

	public static final ActionManager INSTANCE = new ActionManager();

	private final Set<Action> actions;
	private final Map<KeyCodeCombination, Map<String, Action>> binds;

	public ActionManager() {
		this.actions = new HashSet<>();
		this.binds = new HashMap<>();
		loadDefaultActions();
	}

	public Optional<Action> get(String name) {
		Validate.notNull(name, "Name cannot be null!");
		return actions.stream().filter(target -> target.getName().equals(name)).findAny();
	}

	public Set<Action> getActions() {
		return Collections.unmodifiableSet(actions);
	}

	public boolean register(Action action) {
		Validate.notNull(action, "Action cannot be null!");

		ActionRegisterEvent.Before before = callEvent(new ActionRegisterEvent.Before(action));
		if (before.isCancelled()) return false;
		if (!actions.add(action)) return false;
		callEvent(new ActionRegisterEvent.After(action));
		return true;
	}

	public boolean unregister(String name) {
		Validate.notNull(name, "Name cannot be null!");
		Action action = get(name).orElse(null);
		if (action == null) return false;
		ActionUnregisterEvent.Before before = callEvent(new ActionUnregisterEvent.Before(action));
		if (before.isCancelled()) return false;
		getBindCombinations(name).forEach(key -> unbind(key, action.getRegionTag()));
		actions.remove(action);
		callEvent(new ActionUnregisterEvent.After(action));
		return true;
	}

	public Optional<Action> getBindAction(String regionTag, KeyCodeCombination combination) {
		Validate.notNull(regionTag, "Region tag cannot be nulL!");
		Validate.notNull(combination, "Combination cannot be null!");


		Map<String, Action> regions = binds.get(combination);
		if (regions == null) return Optional.empty();

		if (regionTag.equals(RegionTags.GENERAL) || regions.containsKey(RegionTags.GENERAL)) {
			return Optional.ofNullable(regions.get(RegionTags.GENERAL));
		}

		return Optional.ofNullable(regions.get(regionTag));
	}

	public List<KeyCodeCombination> getBindCombinations(String name) {
		Validate.notNull(name, "Name cannot be null!");

		Action action = get(name).orElse(null);
		if (action == null) return new LinkedList<>();

		List<KeyCodeCombination> combinations = new LinkedList<>();
		binds.forEach(((combination, regions) -> {
			if (regions.containsKey(action.getRegionTag()) &&
					regions.get(action.getRegionTag()).getName().equals(action.getName())) {
				combinations.add(combination);
			}
		}));
		return combinations;
	}

	public boolean bind(KeyCodeCombination combination, String name) {
		Validate.notNull(combination, "Combination cannot be null!");
		Validate.notNull(name, "Name cannot be null!");
		Action action = get(name).orElse(null);

		if (action == null) return false;

		Map<String, Action> map;
		if (!binds.containsKey(combination)) {
			map = new HashMap<>();
			binds.put(combination, map);
		} else {
			map = binds.get(combination);
		}

		Map<String, Action> actionsToRemove = new HashMap<>();

		if (action.getRegionTag().equals(RegionTags.GENERAL)) {
			actionsToRemove.putAll(map);
		} else {
			if (map.containsKey(RegionTags.GENERAL)) {
				actionsToRemove.put(RegionTags.GENERAL, map.get(RegionTags.GENERAL));
			}
			if (!action.getRegionTag().equals(RegionTags.GENERAL) && map.containsKey(action.getRegionTag())) {
				actionsToRemove.put(action.getRegionTag(), map.get(action.getRegionTag()));
			}
		}

		ActionBindEvent.Before before = callEvent(new ActionBindEvent.Before(action, actionsToRemove));
		if (before.isCancelled()) {
			if (map.isEmpty()) binds.remove(combination);
			return false;
		}

		actionsToRemove.forEach((region, target) -> map.remove(region));
		map.put(action.getRegionTag(), action);
		return true;
	}

	public boolean unbind(KeyCodeCombination combination, String region) {
		Validate.notNull(combination, "Combination cannot be null!");

		Map<String, Action> map = binds.get(combination);
		if (map == null) return false;

		Action action = map.get(region);
		if (action == null) return false;

		ActionUnbindEvent.Before event = callEvent(new ActionUnbindEvent.Before(action, combination));
		if (event.isCancelled()) return false;

		map.remove(region);
		if (map.isEmpty()) binds.remove(combination);
		callEvent(new ActionUnbindEvent.After(action, combination));
		return true;
	}

	public void addAcceleratorsToScene(Scene scene, boolean clear) {
		if (clear) {
			scene.getAccelerators().clear();
		}

		binds.forEach((combination, regions) -> scene.getAccelerators().put(combination, () -> {
			Node node = scene.getFocusOwner();
			String region = node instanceof TaggedRegion ? ((TaggedRegion) node).getTag() : RegionTags.UNKNOWN;
			if (regions.containsKey(region)) regions.get(region).run(node);
		}));
	}

	private void loadDefaultActions() {
		actions.add(new TextEditorActionReformat());
		actions.add(new TextEditorActionSave());
		bind(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN), TextEditorActionReformat.NAME);
		bind(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), TextEditorActionSave.NAME);
	}
}
