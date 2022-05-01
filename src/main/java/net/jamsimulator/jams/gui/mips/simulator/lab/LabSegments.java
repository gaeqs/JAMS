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

package net.jamsimulator.jams.gui.mips.simulator.lab;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryHalfwordSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;


public class LabSegments extends HBox {

    public static final String SEGMENT_STYLE = "segment";
    public static final String SELECTED_SEGMENT_STYLE = "selected-segment";

    public static final String CONFIG_NODE_1 = "simulation.mips.lab_segment_1_address";
    public static final String CONFIG_NODE_2 = "simulation.mips.lab_segment_2_address";

    public LabSegments(Simulation<?> simulation, Memory memory) {
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);
        setSpacing(10);
        getChildren().addAll(
                new Segment(simulation, memory, CONFIG_NODE_2),
                new Segment(simulation, memory, CONFIG_NODE_1)
        );
    }


    private static class Segment extends AnchorPane {

        private final Rectangle[] rectangles = new Rectangle[8];
        private final String node;
        private final Memory memory;
        private int address;

        public Segment(Simulation<?> simulation, Memory memory, String node) {
            this.node = node;
            this.memory = memory;
            this.address = Jams.getMainConfiguration().data().getNumber(node).orElse(0).intValue();
            rectangles[0] = new Rectangle(10, 0, 40, 10);
            rectangles[1] = new Rectangle(50, 10, 10, 40);
            rectangles[2] = new Rectangle(50, 60, 10, 40);
            rectangles[3] = new Rectangle(10, 100, 40, 10);
            rectangles[4] = new Rectangle(0, 60, 10, 40);
            rectangles[5] = new Rectangle(0, 10, 10, 40);
            rectangles[6] = new Rectangle(10, 50, 40, 10);
            rectangles[7] = new Rectangle(70, 100, 10, 10);
            getChildren().addAll(rectangles);

            memory.getBottomMemory().registerListeners(this, true);
            simulation.registerListeners(this, true);
            Jams.getMainConfiguration().data().registerListeners(this, true);

            for (Rectangle rectangle : rectangles) {
                rectangle.getStyleClass().add(SEGMENT_STYLE);
            }

            refresh();
        }

        @Listener
        private void onMemorySetByte(MemoryByteSetEvent.After event) {
            if (event.getAddress() == address) {
                refresh(event.getValue());
            }
        }

        @Listener
        private void onMemorySetWord(MemoryWordSetEvent.After event) {
            if (event.getAddress() >> 2 == address >> 2) {
                byte value = (byte) (event.getValue() >> 8 * (address & 0x3));
                refresh(value);
            }
        }

        @Listener
        private void onMemorySetHalfword(MemoryHalfwordSetEvent.After event) {
            if (event.getAddress() >> 1 == address >> 1) {
                byte value = (byte) (event.getValue() >> 8 * (address & 0x1));
                refresh(value);
            }
        }

        @Listener
        private void onSimulationStop(SimulationStopEvent event) {
            refresh();
        }

        @Listener
        private void onSimulationReset(SimulationResetEvent event) {
            refresh();
        }

        @Listener
        private void onNodeChange(ConfigurationNodeChangeEvent.After event) {
            if (event.getNode().equals(node)) {
                var number = (Number) event.getNewValueAs().orElse(0);
                address = number.intValue();
            }
        }

        private void refresh() {
            refresh(memory.getByte(address, false, true, false));
        }

        private void refresh(byte b) {
            for (int i = 0; i < 8; i++) {
                if (((b >> i) & 0x1) == 1) {
                    if (!rectangles[i].getStyleClass().contains(SELECTED_SEGMENT_STYLE)) {
                        rectangles[i].getStyleClass().add(SELECTED_SEGMENT_STYLE);
                    }
                } else {
                    rectangles[i].getStyleClass().remove(SELECTED_SEGMENT_STYLE);
                }
            }
        }
    }

}
