package net.jamsimulator.jams.gui.popup;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.manager.ActionManager;
import net.jamsimulator.jams.utils.KeyCombinationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BindActionWindow extends VBox {

	private Action action;
	private KeyCodeCombination combination;

	private BindActionWindow(Stage stage, Action action) {
		this.action = action;

		setAlignment(Pos.BOTTOM_CENTER);
		getStyleClass().add("v-box");
		LanguageLabel title = new LanguageLabel(Messages.CONFIG_ACTION_BIND_ENTER);
		title.setPadding(new Insets(10));
		getChildren().add(title);

		Label combinationDisplay = new Label();
		getChildren().add(combinationDisplay);

		LanguageButton yes = new LanguageButton(Messages.GENERAL_CONFIRM);
		LanguageButton cancel = new LanguageButton(Messages.GENERAL_CANCEL);


		yes.setOnAction(event -> {
			stage.close();
			title.dispose();
			yes.dispose();
			cancel.dispose();
			event.consume();
			confirm();
		});

		cancel.setOnAction(event -> {
			stage.close();
			title.dispose();
			yes.dispose();
			cancel.dispose();
			event.consume();
		});

		HBox box = new HBox();
		box.setSpacing(10);
		box.setPadding(new Insets(5));
		box.getStyleClass().add("h-box");


		Region vRegion = new Region();
		VBox.setVgrow(vRegion, Priority.ALWAYS);
		getChildren().add(vRegion);

		Region hRegion = new Region();
		box.getChildren().addAll(hRegion, yes, cancel);
		HBox.setHgrow(hRegion, Priority.ALWAYS);
		getChildren().add(box);

		setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				stage.close();
				title.dispose();
				yes.dispose();
				cancel.dispose();
				event.consume();
			} else {
				if (event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.SHIFT
						|| event.getCode() == KeyCode.ALT || event.getCode() == KeyCode.META
						|| event.getCode() == KeyCode.SHORTCUT) return;
				combination = new KeyCombinationBuilder(event).build();
				combinationDisplay.setText(combination.toString());
			}
		});
	}

	private void confirm() {
		Map<String, Action> map = JamsApplication.getActionManager().getBindActions(combination);
		if (map.isEmpty()) {
			bind();
			return;
		}

		if (action.equals(map.get(action.getRegionTag()))) return;

		List<Action> removed = new ArrayList<>();
		if (action.getRegionTag().equals(RegionTags.GENERAL)) {
			removed.addAll(map.values());
		} else {
			if (map.containsKey(action.getRegionTag())) {
				removed.add(map.get(action.getRegionTag()));
			}
		}

		if (!removed.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			Language language = Jams.getLanguageManager().getSelected();

			removed.forEach(target -> builder.append(" - ").append(language
					.getOrDefault(ActionManager.LANGUAGE_NODE_PREFIX + target.getName())).append('\n'));
			ConfirmationWindow.open(language.getOrDefault(Messages.CONFIG_ACTION_BIND_CONFIRM) + "\n\n"
					+ builder + '\n' + language.getOrDefault(Messages.CONFIG_ACTION_BIND_CONFIRM_2), this::bind, () -> {
			});
			return;
		}
		bind();
	}

	private void bind() {
		JamsApplication.getActionManager().bind(combination, action.getName());
		JamsApplication.getActionManager().save();
	}

	public static void open(Action action) {
		Stage stage = new Stage();
		stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.CONFIG_ACTION_BIND_TITLE));
		PopupWindowHelper.open(stage, new BindActionWindow(stage, action), -1, -1, false);
	}
}