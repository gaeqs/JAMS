/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.simulator.cache;

import javafx.application.Platform;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.language.wrapper.LanguagePieChartData;

/**
 * Stats tab of the cache visualizer.
 */
public class CacheStatsVisualizer extends AnchorPane {

    private final CacheVisualizer visualizer;

    private final PieChart.Data hitRate;
    private final PieChart.Data missRate;

    private final Button resetButton;
    private final Label operationsCount, hitsCount, missesCount;

    public CacheStatsVisualizer(CacheVisualizer visualizer) {
        this.visualizer = visualizer;
        this.hitRate = new PieChart.Data("Hit rate", 0);
        this.missRate = new PieChart.Data("Miss rate", 0);

        getChildren().addAll(
                new LanguagePieChartData(hitRate, Messages.CACHE_HITS),
                new LanguagePieChartData(missRate, Messages.CACHE_MISSES));

        //Chart creation.
        var chart = new PieChart();
        chart.getData().addAll(hitRate, missRate);
        chart.setLegendVisible(false);
        AnchorUtils.setAnchor(chart, 0, 70, 0, 0);
        getChildren().add(chart);

        //Stats.
        operationsCount = new Label();
        hitsCount = new Label();
        missesCount = new Label();
        loadStats();

        //Reset button.
        resetButton = new LanguageButton(Messages.CACHE_RESET);
        loadResetButton();

        //Refresh all data
        refresh();
    }

    /**
     * Called when the simulation is started.
     */
    void onStart() {
        resetButton.setDisable(true);
    }

    /**
     * Called when the simulation is stopped or reset.
     */
    void onStop() {
        resetButton.setDisable(false);
    }

    /**
     * Refreshes the data of the stats' visualizer.
     */
    void refresh() {
        Platform.runLater(() -> {
            var cache = visualizer.getSelectedCache();
            var stats = cache.getStats();
            var rate = stats.hits() / (double) stats.operations();
            if (Double.isNaN(rate)) rate = 1;
            hitRate.setPieValue(rate);
            missRate.setPieValue(1 - rate);

            operationsCount.setText(String.valueOf(stats.operations()));
            hitsCount.setText(String.valueOf(stats.hits()));
            missesCount.setText(String.valueOf(stats.misses()));
        });
    }

    /**
     * Loads the stats section of the visualizer.
     */
    private void loadStats() {
        var operations = new LanguageLabel(Messages.CACHE_STATS_OPERATIONS);
        var hits = new LanguageLabel(Messages.CACHE_STATS_HITS);
        var misses = new LanguageLabel(Messages.CACHE_STATS_MISSES);

        var opHBox = new HBox(operations, operationsCount);
        var hitsHBox = new HBox(hits, hitsCount);
        var missesHBox = new HBox(misses, missesCount);
        opHBox.setSpacing(10);
        hitsHBox.setSpacing(10);
        missesHBox.setSpacing(10);

        hits.prefWidthProperty().bind(operations.widthProperty());
        misses.prefWidthProperty().bind(operations.widthProperty());


        AnchorUtils.setAnchor(opHBox, -1, 70, 10, 0);
        AnchorUtils.setAnchor(hitsHBox, -1, 55, 10, 0);
        AnchorUtils.setAnchor(missesHBox, -1, 40, 10, 0);

        getChildren().add(opHBox);
        getChildren().add(hitsHBox);
        getChildren().add(missesHBox);
    }

    /**
     * Loads the reset button.
     */
    private void loadResetButton() {
        AnchorUtils.setAnchor(resetButton, -1, 5, 5, 5);
        resetButton.setOnAction(event -> {
            visualizer.getSimulation().resetCaches();
            refresh();
        });
        getChildren().add(resetButton);
    }
}
