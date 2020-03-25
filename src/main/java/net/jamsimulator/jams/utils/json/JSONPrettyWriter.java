package net.jamsimulator.jams.utils.json;

import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONPrettyWriter {

	public static void writeJSONString(Map map, Writer out, int tabs) throws IOException {
		if (map == null) {
			out.write("null");
			return;
		}

		boolean first = true;
		Iterator iter = map.entrySet().iterator();

		out.write("{\n");
		tabs++;
		while (iter.hasNext()) {
			if (first)
				first = false;
			else
				out.write(",\n");

			for (int i = 0; i < tabs; i++) {
				out.write("\t");
			}

			Map.Entry entry = (Map.Entry) iter.next();
			out.write('\"');
			out.write(JSONValue.escape(String.valueOf(entry.getKey())));
			out.write('\"');
			out.write(": ");

			if (entry.getValue() instanceof Map)
				writeJSONString((Map) entry.getValue(), out, tabs);
			else if (entry.getValue() instanceof List)
				writeJSONString((List) entry.getValue(), out, tabs);
			else
				JSONValue.writeJSONString(entry.getValue(), out);
		}
		out.write("\n");
		tabs--;
		for (int i = 0; i < tabs; i++) {
			out.write("\t");
		}
		out.write("}");
	}

	public static void writeJSONString(List list, Writer out, int tabs) throws IOException {
		if (list == null) {
			out.write("null");
			return;
		}

		boolean first = true;
		Iterator iter = list.iterator();

		out.write("[\n");
		tabs++;
		while (iter.hasNext()) {
			if (first)
				first = false;
			else
				out.write(',');

			for (int i = 0; i < tabs + 1; i++) {
				out.write("\t");
			}

			Object value = iter.next();
			if (value == null) {
				out.write("null");
				continue;
			}

			if (value instanceof Map)
				writeJSONString((Map) value, out, tabs);
			else if (value instanceof List)
				writeJSONString((List) value, out, tabs);
			else
				JSONValue.writeJSONString(value, out);
		}
		out.write("\n");

		tabs--;
		for (int i = 0; i < tabs; i++) {
			out.write("\t");
		}

		out.write("]");
	}
}
