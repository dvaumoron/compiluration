package fr.compiluration;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class Class implements Writable {

	private final String name;
	private final Map<String, Writable> writables = new LinkedHashMap<String, Writable>();

	public Class(String name) {
		this.name = name;
	}

	@Override
	public void write(Writer writer, int indentationLevel) throws IOException {
		int indentationLevel2 = indentationLevel + 1;
		Compiluration.writeIndentation(writer, indentationLevel);
		writer.append("public static final class ").append(name).append(" {\n");
		for(Writable writable : writables.values()) {
			writable.write(writer, indentationLevel2);
		}
		Compiluration.writeIndentation(writer, indentationLevel);
		writer.append("}\n");
	}

	public Map<String, Writable> getWritables() {
		return writables;
	}

}
