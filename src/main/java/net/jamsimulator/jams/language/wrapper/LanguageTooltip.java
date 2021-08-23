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

package net.jamsimulator.jams.language.wrapper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.manager.event.ManagerDefaultElementChangeEvent;
import net.jamsimulator.jams.manager.event.ManagerSelectedElementChangeEvent;
import net.jamsimulator.jams.utils.StringUtils;

import java.lang.reflect.Field;

public class LanguageTooltip extends Tooltip {

    public static final int DEFAULT_DELAY = 200;
    private final String[] replacements;
    private String node;

    public LanguageTooltip(String node, String... replacements) {
        this(node, DEFAULT_DELAY, replacements);
    }

    public LanguageTooltip(String node, int showDelay, String... replacements) {
        this.node = node;
        this.replacements = replacements;
        hackTooltipStartTiming(this, showDelay);
        refreshMessage();
        Jams.getLanguageManager().registerListeners(this, true);
    }

    private static void hackTooltipStartTiming(Tooltip tooltip, int showDelay) {
        try {
            tooltip.setShowDelay(new Duration(showDelay));
        } catch (NoSuchMethodError ex) {
            try {
                Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
                fieldBehavior.setAccessible(true);
                Object objBehavior = fieldBehavior.get(tooltip);

                Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
                fieldTimer.setAccessible(true);
                Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

                objTimer.getKeyFrames().clear();
                objTimer.getKeyFrames().add(new KeyFrame(new Duration(showDelay)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setNode(String node, int showDelay) {
        this.node = node;
        hackTooltipStartTiming(this, showDelay);
        refreshMessage();
    }

    private void refreshMessage() {
        if (node == null) return;
        var parsed = StringUtils.parseEscapeCharacters(Jams.getLanguageManager().getSelected().getOrDefault(node));

        for (int i = 0; i < replacements.length - 1; i += 2) {
            parsed = parsed.replace(replacements[i], replacements[i + 1]);
        }

        setText(StringUtils.addLineJumps(parsed, 70));
    }

    @Listener
    public void onSelectedLanguageChange(ManagerSelectedElementChangeEvent.After<Language> event) {
        refreshMessage();
    }

    @Listener
    public void onDefaultLanguageChange(ManagerDefaultElementChangeEvent.After<Language> event) {
        refreshMessage();
    }
}
