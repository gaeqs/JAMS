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

package net.jamsimulator.jams.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @Test
    void basic() {
        var manager = new Manager<>(ResourceProvider.JAMS, "test", Label.class, false) {
            @Override
            protected void loadDefaultElements() {
                add(new Label(ResourceProvider.JAMS, "a"));
            }
        };

        assertFalse(manager.isLoaded(), "Manager is loaded!");
        manager.load();
        assertTrue(manager.isLoaded(), "Manager is not loaded!");
        assertEquals(1, manager.size(), "Manager must have only one element!");
        assertTrue(manager.get("a").isPresent(), "Element a not found!");

        assertTrue(manager.add(new Label(ResourceProvider.JAMS, "b")), "Couldn't add element b!");
        assertEquals(2, manager.size(), "Manager must have two elements!");
        assertTrue(manager.get("b").isPresent(), "Element b not found!");
        assertFalse(manager.add(new Label(ResourceProvider.JAMS, "b")), "Two elements b were added!");
        assertEquals(2, manager.size(), "Manager must have two elements!");

        assertTrue(manager.removeElement("b"), "Couldn't remove element b!");
        assertEquals(1, manager.size(), "Manager must have only one element!");
        assertTrue(manager.get("a").isPresent(), "Element a not found!");
        assertFalse(manager.get("b").isPresent(), "Element b found!");

        manager.add(new Label(ResourceProvider.JAMS, "b"));
        assertEquals(2, manager.size(), "Manager must have two elements!");
        assertTrue(manager.get("b").isPresent(), "Element b not found!");

        manager.removeProvidedBy(ResourceProvider.JAMS);
        assertTrue(manager.isEmpty(), "Manager is not empty!");
    }


    private static record Label(ResourceProvider provider, String name) implements ManagerResource {

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ResourceProvider getResourceProvider() {
            return provider;
        }
    }
}