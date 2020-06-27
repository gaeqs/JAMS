package net.jamsimulator.jams.utils;

import java.util.Comparator;

public class NumericStringComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if (o1 == null && o2 == null) return 0;
		if (o1 == null) return -1;
		if (o2 == null) return 1;

		String[] sl1 = o1.split(" ");
		String[] sl2 = o2.split(" ");

		int max = Math.min(sl1.length, sl2.length);
		int result;
		for (int i = 0; i < max; i++) {
			try {
				result = NumericUtils.decodeInteger(sl1[i]) - NumericUtils.decodeInteger(sl2[i]);
			} catch (NumberFormatException ex) {
				result = sl1[i].compareTo(sl2[i]);
			}
			if (result != 0) return result;
		}

		return sl1.length - sl2.length;
	}
}
