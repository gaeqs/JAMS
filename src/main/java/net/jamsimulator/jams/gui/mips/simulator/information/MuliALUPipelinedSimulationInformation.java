/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.gui.mips.simulator.information;

import javafx.scene.Node;
import net.jamsimulator.jams.gui.configuration.RegionDisplay;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.architecture.MultiALUPipelinedArchitecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.multialupipelined.MultiALUPipelinedSimulation;

public class MuliALUPipelinedSimulationInformation extends SingleCycleSimulationInformation {

    protected final MultiALUPipelinedSimulation simulation;

    protected final LanguageLabel textRAWs;
    protected final LanguageLabel textWAWs;
    protected final LanguageLabel textOtherStalls;
    protected final LanguageLabel textTotalStalls;

    public MuliALUPipelinedSimulationInformation(Simulation<?> simulation) {
        super(simulation);

        textRAWs = new LanguageLabel(Messages.SIMULATION_INFORMATION_RAWS);
        textWAWs = new LanguageLabel(Messages.SIMULATION_INFORMATION_WAWS);
        textOtherStalls = new LanguageLabel(Messages.SIMULATION_INFORMATION_OTHER_STALLS);
        textTotalStalls = new LanguageLabel(Messages.SIMULATION_INFORMATION_STALLS);

        getChildren().add(new RegionDisplay(Messages.SIMULATION_INFORMATION_SECTION_HAZARDS));
        getChildren().add(textRAWs);
        getChildren().add(textWAWs);
        getChildren().add(textOtherStalls);
        getChildren().add(textTotalStalls);

        if (!(simulation instanceof MultiALUPipelinedSimulation s)) {
            this.simulation = null;
        } else {
            this.simulation = s;
        }

        refreshValues();
    }

    @Override
    protected void refreshValues() {
        super.refreshValues();
        if (simulation == null) return;
        var p = simulation.getPipeline();
        textRAWs.setReplacements(new String[]{"{RAWS}", String.valueOf(p.getRAWs())});
        textWAWs.setReplacements(new String[]{"{WAWS}", String.valueOf(p.getWAWs())});
        textOtherStalls.setReplacements(new String[]{"{STALLS}", String.valueOf(p.getOtherStalls())});

        long total = p.getRAWs() + p.getWAWs() + p.getOtherStalls();
        textTotalStalls.setReplacements(new String[]{"{STALLS}", String.valueOf(total)});
    }

    public static final class Builder implements SimulationInformationBuilder {

        @Override
        public Node buildNewNode(Simulation<?> simulation) {
            return new MuliALUPipelinedSimulationInformation(simulation);
        }

        @Override
        public ResourceProvider getResourceProvider() {
            return ResourceProvider.JAMS;
        }

        @Override
        public String getName() {
            return MultiALUPipelinedArchitecture.NAME;
        }
    }


}
