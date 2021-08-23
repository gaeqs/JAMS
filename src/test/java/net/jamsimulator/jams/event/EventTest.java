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

package net.jamsimulator.jams.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class EventTest {

    private static final int SHOULD_COUNT = 7;
    private int count;

    @Test
    void testEvents() {
        count = 0;
        var broadcast = new SimpleEventBroadcast();
        broadcast.registerListeners(this, true);

        var event = new TestEvent<>(new B());
        broadcast.callEvent(event);
        assertEquals(SHOULD_COUNT, count);
    }

    @Listener
    private void shouldCall(TestEvent event) {
        System.out.println("SC1");
        count++;
    }

    @Listener
    private void shoudCall2(TestEvent<B> event) {
        System.out.println("SC2");
        count++;
    }

    @Listener
    private void shoudCall3(TestEvent<?> event) {
        System.out.println("SC3");
        count++;
    }

    @Listener
    private void shoudCall4(TestEvent<? extends A> event) {
        System.out.println("SC4");
        count++;
    }

    @Listener
    private void shoudCall5(TestEvent<? extends B> event) {
        System.out.println("SC5");
        count++;
    }

    @Listener
    private void shoudCall6(TestEvent<? super B> event) {
        System.out.println("SC6");
        count++;
    }

    @Listener
    private void shoudCall7(TestEvent<? super C> event) {
        System.out.println("SC7");
        count++;
    }

    @Listener
    private void shouldntCall(TestEvent<C> event) {
        fail();
    }

    @Listener
    private void shouldntCall2(TestEvent<A> event) {
        fail();
    }

    @Listener
    private void shouldntCall3(TestEvent<D> event) {
        fail();
    }

    @Listener
    private void shouldntCall4(TestEvent<? extends C> event) {
        fail();
    }

    @Listener
    private void shouldntCall5(TestEvent<? extends D> event) {
        fail();
    }

    @Listener
    private void shouldntCall6(TestEvent<? super A> event) {
        fail();
    }

    @Listener
    private void shouldntCall7(TestEvent<? super D> event) {
        fail();
    }

    private static class TestEvent<E> extends GenericEvent<E> {

        private final E element;

        public TestEvent(E element) {
            super((Class<E>) element.getClass());
            this.element = element;
        }

        public E getElement() {
            return element;
        }
    }


    private static class A {
    }

    private static class B extends A {
    }

    private static class C extends B {
    }

    private static class D extends A {
    }

}