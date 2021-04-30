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
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;


public class LabSegments extends HBox {

    public static final String SEGMENT_STYLE = "segment";
    public static final String SELECTED_SEGMENT_STYLE = "selected-segment";

    public static final String CONFIG_NODE_1 = "simulation.mips.lab_segment_1_address";
    public static final String CONFIG_NODE_2 = "simulation.mips.lab_segment_2_address";

    public LabSegments(Memory memory) {
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);
        setSpacing(10);
        getChildren().addAll(new Segment(memory, CONFIG_NODE_2), new Segment(memory, CONFIG_NODE_1));
    }


    private static class Segment extends AnchorPane {

        private final Rectangle[] rectangles = new Rectangle[8];
        private final String node;
        private int address;

        public Segment(Memory memory, String node) {
            this.node = node;
            this.address = Jams.getMainConfiguration().getNumber(node).orElse(0).intValue();
            rectangles[0] = new Rectangle(10, 0, 40, 10);
            rectangles[1] = new Rectangle(50, 10, 10, 40);
            rectangles[2] = new Rectangle(50, 60, 10, 40);
            rectangles[3] = new Rectangle(10, 100, 40, 10);
            rectangles[4] = new Rectangle(0, 60, 10, 40);
            rectangles[5] = new Rectangle(0, 10, 10, 40);
            rectangles[6] = new Rectangle(10, 50, 40, 10);
            rectangles[7] = new Rectangle(70, 100, 10, 10);
            getChildren().addAll(rectangles);

            memory.registerListeners(this, true);
            Jams.getMainConfiguration().registerListeners(this, true);

            for (Rectangle rectangle : rectangles) {
                rectangle.getStyleClass().add(SEGMENT_STYLE);
            }
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
        private void onNodeChange(ConfigurationNodeChangeEvent.After event) {
            if (event.getNode().equals(node)) {
                var number = (Number) event.getNewValueAs().orElse(0);
                address = number.intValue();
            }
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