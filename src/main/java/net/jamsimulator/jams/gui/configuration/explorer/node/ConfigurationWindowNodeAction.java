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

package net.jamsimulator.jams.gui.configuration.explorer.node;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;

import java.util.List;

public class ConfigurationWindowNodeAction extends ConfigurationWindowNode<Action> {

	protected Action action;
	protected VBox combinations;

	public ConfigurationWindowNodeAction(Action action) {
		super(null, null, null, null);
		getStyleClass().add("configuration-window-node-actions");
		this.action = action;
		setAlignment(Pos.CENTER_LEFT);
		init2();
	}


	@Override
	public Action getValue() {
		return action;
	}

	public void setValue(Action value) {
	}

	protected void saveValue(Action value) {
	}

	protected void init() {
	}

	protected void init2() {
		Label label = new Label(action.getName());
		combinations = new VBox();


		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);
		getChildren().addAll(label, region, combinations);

		List<KeyCombination> keys = JamsApplication.getActionManager().getBindCombinations(action.getName());
		for (KeyCombination combination : keys) {
			combinations.getChildren().add(new TextField(combination.toString()));
		}
	}
}
