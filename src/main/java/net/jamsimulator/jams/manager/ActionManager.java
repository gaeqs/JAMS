/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.manager;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.defaults.explorerelement.*;
import net.jamsimulator.jams.gui.action.defaults.explorerelement.folder.*;
import net.jamsimulator.jams.gui.action.defaults.texteditor.*;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.action.event.ActionRegisterEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnbindEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnregisterEvent;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This singleton stores all {@link Action}s that JAMS may use.
 * <p>
 * To register an {@link Action} use {@link #register(Action)}.
 * To unregister am {@link Action} use {@link #unregister(String)}.
 * <p>
 * To bind an {@link Action} to a {@link KeyCombination} use {@link #bind(KeyCombination, String)}.
 * To unbind them use {@link #unbind(KeyCombination, String)}.
 */
public class ActionManager extends SimpleEventBroadcast {

	public static final String ACTIONS_SECTION = "action";

	public static final String LANGUAGE_NODE_PREFIX = "ACTION_";
	public static final String LANGUAGE_REGION_NODE_PREFIX = "ACTION_REGION_";

	public static final ActionManager INSTANCE = new ActionManager();

	private final Set<Action> actions;
	private final Map<KeyCombination, Map<String, Action>> binds;

	private ActionManager() {
		this.actions = new HashSet<>();
		this.binds = new HashMap<>();
		loadDefaultActions();
		if (loadDefaultBinds(loadBinds())) {
			save();

			try {
				Jams.getMainConfiguration().save(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the {@link Action} that matches the given name, if present.
	 *
	 * @param name the name.
	 * @return the {@link Action}, if present.
	 */
	public Optional<Action> get(String name) {
		Validate.notNull(name, "Name cannot be null!");
		return actions.stream().filter(target -> target.getName().equals(name)).findAny();
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link Action}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<Action> getAll() {
		return Collections.unmodifiableSet(actions);
	}

	/**
	 * Registers the given {@link Action}.
	 * This will fail if an {@link Action} with the same name already exists within this manager.
	 *
	 * @param action the {@link Action}.
	 * @return whether the {@link Action} was registered.
	 */
	public boolean register(Action action) {
		Validate.notNull(action, "Action cannot be null!");

		if (actions.contains(action)) return false;

		ActionRegisterEvent.Before before = callEvent(new ActionRegisterEvent.Before(action));
		if (before.isCancelled()) return false;
		if (!actions.add(action)) return false;
		callEvent(new ActionRegisterEvent.After(action));
		return true;
	}

	/**
	 * Attempts to unregister the {@link Action} that matches the given name.
	 * This will unbind all {@link KeyCombination}s bind to this {@link Action}.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
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

	/**
	 * Returns the {@link Action} bind to the given {@link KeyCombination}
	 * that matches the given region tag, if present.
	 *
	 * @param regionTag   the region tag.
	 * @param combination the {@link KeyCombination}.
	 * @return the {@link Action}, if present.
	 */
	public Optional<Action> getBindAction(String regionTag, KeyCombination combination) {
		Validate.notNull(regionTag, "Region tag cannot be nulL!");
		Validate.notNull(combination, "Combination cannot be null!");


		Map<String, Action> regions = binds.get(combination);
		if (regions == null) return Optional.empty();

		if (regionTag.equals(RegionTags.GENERAL) || regions.containsKey(RegionTags.GENERAL)) {
			return Optional.ofNullable(regions.get(RegionTags.GENERAL));
		}

		return Optional.ofNullable(regions.get(regionTag));
	}


	/**
	 * Returns all {@link Action}s bind to the given {@link KeyCombination}.
	 *
	 * @param combination the {@link KeyCombination}.
	 * @return the {@link Action}s.
	 */
	public Map<String, Action> getBindActions(KeyCombination combination) {
		Validate.notNull(combination, "Combination cannot be null!");

		Map<String, Action> regions = binds.get(combination);
		if (regions == null) return new HashMap<>();
		return Collections.unmodifiableMap(regions);
	}

	/**
	 * Returns all {@link KeyCombination}s bind to the {@link Action} that matches the given name.
	 * This will return an empty {@link List} if there's no registered {@link Action} that matches the given name.
	 *
	 * @param name the name.
	 * @return the {@link List} wit h the {@link KeyCombination}s.
	 */
	public List<KeyCombination> getBindCombinations(String name) {
		Validate.notNull(name, "Name cannot be null!");

		Action action = get(name).orElse(null);
		if (action == null) return new LinkedList<>();

		List<KeyCombination> combinations = new LinkedList<>();
		binds.forEach(((combination, regions) -> {
			if (regions.containsKey(action.getRegionTag()) &&
					regions.get(action.getRegionTag()).getName().equals(action.getName())) {
				combinations.add(combination);
			}
		}));
		return combinations;
	}

	/**
	 * Binds the {@link Action} that matches the given name to the given {@link KeyCombination}.
	 * <p>
	 * This method returns {@code false} if there's no registered {@link Action} that matches the given name or
	 * if the {@link Action} was already bind to the {@link KeyCombination}.
	 * <p>
	 * This will unbind the previous {@link Action}, or all {@link Action}s bind to the {@link KeyCombination} if
	 * the region of the new {@link Action} is {@link RegionTags#GENERAL}.
	 *
	 * @param combination the {@link KeyCombination}.
	 * @param name        the name of the {@link Action}.
	 * @return whether the operation was successful.
	 */
	public boolean bind(KeyCombination combination, String name) {
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

		if (action.equals(map.get(action.getRegionTag()))) return false;

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

		ActionBindEvent.Before before = callEvent(new ActionBindEvent.Before(action, combination, actionsToRemove));
		if (before.isCancelled()) {
			if (map.isEmpty()) binds.remove(combination);
			return false;
		}

		actionsToRemove.forEach((region, target) -> map.remove(region));
		map.put(action.getRegionTag(), action);

		callEvent(new ActionBindEvent.After(action, combination, actionsToRemove));
		return true;
	}

	/**
	 * Unbinds the {@link Action} that matches the given region from the given {@link KeyCombination}.
	 * This returns {@code false} if the {@link Action} is not present.
	 *
	 * @param combination the {@link KeyCombination}.
	 * @param region      the region of the {@link Action}.
	 * @return whether the operation was successful.
	 */
	public boolean unbind(KeyCombination combination, String region) {
		Validate.notNull(combination, "Combination cannot be null!");

		Map<String, Action> map = binds.get(combination);
		if (map == null)
			return false;

		Action action = map.get(region);
		if (action == null)
			return false;

		ActionUnbindEvent.Before event = callEvent(new ActionUnbindEvent.Before(action, combination));
		if (event.isCancelled())
			return false;


		map.remove(region);
		if (map.isEmpty()) binds.remove(combination);
		if (map.isEmpty()) binds.remove(combination);
		callEvent(new ActionUnbindEvent.After(action, combination));
		return true;
	}

	/**
	 * Adds all {@link Action}s to the given {@link Scene}.
	 *
	 * @param scene the {@link Scene}.
	 * @param clear whether this method should clear all previous accelerators.
	 */
	public void addAcceleratorsToScene(Scene scene, boolean clear) {
		if (clear) {
			scene.getAccelerators().clear();
		}

		binds.forEach((combination, regions) -> scene.getAccelerators().put(combination, () -> {
			Node node = scene.getFocusOwner();

			if (node instanceof ActionRegion) {
				regions.forEach((region, action) -> {
					if (((ActionRegion) node).supportsActionRegion(region)) {
						action.run(node);
					}
				});
			} else {
				if (regions.containsKey(RegionTags.UNKNOWN)) regions.get(RegionTags.UNKNOWN).run(node);
			}


		}));
	}

	/**
	 * Saves all {@link Action}s in the main {@link Configuration}.
	 * <p>
	 * You should invoke {@link Configuration#save(File, boolean)} after this if you want
	 * to save this configuration inside a file.
	 */
	public void save() {
		Configuration root = Jams.getMainConfiguration().getOrCreateConfiguration(ACTIONS_SECTION);
		root.clear();
		actions.forEach(action -> root.set(action.getName(), getBindCombinations(action.getName()).stream()
				.map(KeyCombination::getName).collect(Collectors.toList())));
	}

	private void loadDefaultActions() {
		//TEXT EDITOR
		actions.add(new TextEditorActionCompile());
		actions.add(new TextEditorActionNextFile());
		actions.add(new TextEditorActionPreviousFile());
		actions.add(new TextEditorActionReformat());
		actions.add(new TextEditorActionSave());
		actions.add(new TextEditorActionShowAutocompletionPopup());

		//EXPLORER ELEMENT
		actions.add(new ExplorerElementActionContractOrSelectParent());
		actions.add(new ExplorerElementActionExpandOrSelectNext());
		actions.add(new ExplorerElementActionSelectNext());
		actions.add(new ExplorerElementActionSelectNextMultiple());
		actions.add(new ExplorerElementActionSelectPrevious());
		actions.add(new ExplorerElementActionSelectPreviousMultiple());

		//FOLDER EXPLORER ELEMENT
		actions.add(new FolderExplorerElementActionCopy());
		actions.add(new FolderExplorerElementActionDelete());
		actions.add(new FolderExplorerElementActionPaste());

		actions.add(new FolderExplorerElementActionShowInFiles());

		actions.add(new FolderExplorerElementActionNewFile());
		actions.add(new FolderExplorerElementActionNewAssemblyFile());
		actions.add(new FolderExplorerElementActionNewFolder());
	}

	private List<Action> loadBinds() {
		List<Action> presentActions = new ArrayList<>();
		Optional<Configuration> optional = Jams.getMainConfiguration().get(ACTIONS_SECTION);
		if (!optional.isPresent()) return presentActions;
		Configuration configuration = optional.get();

		configuration.getAll(false).forEach((key, value) -> {
			if (!(value instanceof List)) {
				System.err.println("Error while parsing action " + key + ". Bad format.");
				return;
			}
			Action action = get(key).orElse(null);
			if (action == null) {
				System.err.println("Couldn't found action " + key + ".");
				return;
			}
			presentActions.add(action);

			String string;
			KeyCombination combination;
			for (Object o : (List) value) {
				string = o.toString();
				if (string.isEmpty()) continue;
				try {
					combination = KeyCombination.valueOf(string);
				} catch (IllegalArgumentException ex) {
					System.err.println("Error loading action combination " + string + " for action " + key + ". Bad format.");
					continue;
				}
				bind(combination, key);
			}
		});

		return presentActions;
	}

	private boolean loadDefaultBinds(List<Action> presentActions) {
		boolean requiresSave = false;

		for (Action action : actions) {
			if (!action.getDefaultCodeCombination().isPresent() || presentActions.contains(action)) continue;
			if (getBindAction(action.getRegionTag(), action.getDefaultCodeCombination().get()).isPresent()) continue;

			bind(action.getDefaultCodeCombination().get(), action.getName());
			requiresSave = true;
		}
		return requiresSave;
	}
}
