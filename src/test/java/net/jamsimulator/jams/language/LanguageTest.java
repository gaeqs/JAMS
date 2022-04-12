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

package net.jamsimulator.jams.language;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageTest {

    @BeforeAll
    static void initRegistry() {
        Jams.initForTests();
    }

    @Test
    void testBundledLanguages() {
        var manager = Manager.of(Language.class);

        assertNotNull(manager, "Language manager is null!");

        var english = manager.get("English").orElse(null);
        var spanish = manager.get("English").orElse(null);

        assertNotNull(english, "English language not found.");
        assertNotNull(spanish, "Spanish language not found.");

        var englishKeys = english.getMessages().keySet();
        var spanishKeys = spanish.getMessages().keySet();

        System.out.println("Found " + englishKeys.size() + " elements in English.");
        System.out.println("Found " + spanishKeys.size() + " elements in Spanish.");

        assertEquals(englishKeys, spanishKeys, "Mising keys in English or Spanish.");
    }

    @Test
    void testAttachment() {
        var manager = Manager.of(Language.class);
        assertNotNull(manager, "Language manager is null!");

        var english = manager.get("English").orElse(null);
        assertNotNull(english, "English language not found.");

        var message = english.get(Messages.ABOUT).orElse(null);
        assertNotNull(message, "Message ABOUT is null in English.");

        var attachment = new LanguageAttachment(ResourceProvider.JAMS, Map.of(Messages.ABOUT, "test"), 0);

        assertTrue(english.addAttachment(attachment), "Couldn't add attachment");
        assertFalse(english.addAttachment(attachment),
                "Test was able to add attachment when it should have been already present.");

        assertEquals("test", english.getOrDefault(Messages.ABOUT),
                "Attachment not working when using getOrDefault()");
        assertEquals("test", english.getOrEmpty(Messages.ABOUT),
                "Attachment not working when using getOrEmpty()");
        assertEquals("test", english.get(Messages.ABOUT).orElse(null),
                "Attachment not working when using get()");

        assertTrue(english.removeAttachment(attachment), "Couldn't remove attachment.");
        assertFalse(english.removeAttachment(attachment),
                "removeAttachment() returned true when he attachmen should have been not found.");

        assertEquals(message, english.getOrDefault(Messages.ABOUT),
                "Deattachment not working when using getOrDefault()");
        assertEquals(message, english.getOrEmpty(Messages.ABOUT),
                "Deattachment not working when using getOrEmpty()");
        assertEquals(message, english.get(Messages.ABOUT).orElse(null),
                "Deattachment not working when using get()");
    }

    @Test
    void testAttachmentFromSource() {
        var manager = Manager.get(LanguageManager.class);
        assertNotNull(manager, "Language manager is null!");

        var english = manager.get("English").orElse(null);
        assertNotNull(english, "English language not found.");

        var message = english.get(Messages.ABOUT).orElse(null);
        assertNotNull(message, "Message ABOUT is null in English.");

        var jarResource = Jams.class.getResource("/test/language");
        try {
            var map = manager.loadLanguagesInDirectory(
                    ResourceProvider.TEST, Path.of(jarResource.toURI()));
            map.forEach((path, e) -> fail(e));
        } catch (Exception e) {
            fail(e);
        }

        assertEquals("sourcetest", english.getOrDefault(Messages.ABOUT),
                "Attachment not working when using getOrDefault()");
        assertEquals("sourcetest", english.getOrEmpty(Messages.ABOUT),
                "Attachment not working when using getOrEmpty()");
        assertEquals("sourcetest", english.get(Messages.ABOUT).orElse(null),
                "Attachment not working when using get()");

        manager.removeProvidedBy(ResourceProvider.TEST);

        assertEquals(message, english.getOrDefault(Messages.ABOUT),
                "Deattachment not working when using getOrDefault()");
        assertEquals(message, english.getOrEmpty(Messages.ABOUT),
                "Deattachment not working when using getOrEmpty()");
        assertEquals(message, english.get(Messages.ABOUT).orElse(null),
                "Deattachment not working when using get()");
    }
}
