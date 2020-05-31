package net.jamsimulator.jams.collection;

import java.util.*;

public class Bag<E> implements Collection<E> {

	private final Map<E, Integer> amount;

	/**
	 * Creates the bag.
	 */
	public Bag() {
		amount = new HashMap<>();
	}

	/**
	 * Returns the amount of times this element is inside the bag.
	 *
	 * @param o the element.
	 * @return the amount of times.
	 */
	public int amount(Object o) {
		return amount.getOrDefault(o, 0);
	}

	/**
	 * Sets the count of this element to 0.
	 *
	 * @param o the element.
	 * @return whether the element was inside this bag.
	 */
	public boolean removeAllAmounts(Object o) {
		return amount.remove(o) > 0;
	}

	/**
	 * Returns the amount of elements inside this bag.
	 * This only counts the types of elements, not their amount.
	 * <p>
	 * For example, if an instance of a Bag contains two elements "A" and one element "B",
	 * this method returns "2".
	 *
	 * @return the amount of elements inside this bag.
	 */
	@Override
	public int size() {
		return amount.size();
	}

	/**
	 * Returns the amount of elements inside this bag, including all elements of all types.
	 * <p>
	 * For example, if an instance of a Bag contains two elements "A" and one element "B",
	 * this method returns "3".
	 *
	 * @return the amount of elements inside this bag.
	 */
	public int amount() {
		return amount.values().stream().mapToInt(entry -> entry).sum();
	}

	@Override
	public boolean isEmpty() {
		return amount.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return amount.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		return amount.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return amount.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return amount.keySet().toArray(a);
	}

	@Override
	public boolean add(E e) {
		amount.put(e, amount.getOrDefault(e, 0) + 1);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		int am = amount.getOrDefault(o, 0);
		if (am == 0) return false;
		if (am == 1) {
			amount.remove(o);
		} else {
			amount.put((E) o, am - 1);
		}
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return amount.keySet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c) {
			add(e);
		}
		return true;
	}

	/**
	 * Removes one amount from all elements inside the given collection.
	 *
	 * @param c the collection.
	 * @return whether this bag was modified.
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean removed = false;
		for (Object e : c) {
			removed |= remove(e);
		}
		return removed;
	}

	/**
	 * Removes one amount from all elements that are not present in the given collection.
	 *
	 * @param c the collection.
	 * @return whether this bag was modified.
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		Set<E> keys = new HashSet<>(amount.keySet());

		boolean removed = false;
		for (E key : keys) {
			if (!c.contains(key)) {
				removed |= remove(key);
			}
		}

		return removed;
	}

	@Override
	public void clear() {
		amount.clear();
	}

	@Override
	public String toString() {
		return amount.toString();
	}
}
