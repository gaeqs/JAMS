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

package net.jamsimulator.jams.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

class ListenerMethod {

	private Class<? extends Event> event;
	private boolean weakReference;

	private Object instance;
	private Method method;
	private Listener listener;

	private WeakReference<Object> instanceWeakReference;

	ListenerMethod(Object instance, Method method, Class<? extends Event> event, Listener listener, boolean weakReference) {
		this.method = method;
		this.event = event;
		this.listener = listener;
		this.weakReference = weakReference;

		if (weakReference) {
			this.instanceWeakReference = new WeakReference<>(instance);
		} else {
			this.instance = instance;
		}
	}

	Class<? extends Event> getEvent() {
		return event;
	}

	Listener getListener() {
		return listener;
	}

	boolean matches(Object instance, Method method) {
		//We want to check that it's the same instance, not an equivalent one.
		return this.method.equals(method) && instance == this.instance;
	}

	boolean isReferenceValid() {
		return !weakReference || instanceWeakReference.get() != null;
	}

	void call(Event event) {
		try {
			if (weakReference) {
				Object instance = instanceWeakReference.get();
				if (instance != null) {
					method.invoke(instance, event);
				}
			} else {
				method.invoke(instance, event);
			}
		} catch (Exception e) {
			System.err.println("Error while calling listener " + method.getName() + "!");
			e.printStackTrace();
		}
	}
}
