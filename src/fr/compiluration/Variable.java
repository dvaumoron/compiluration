package fr.compiluration;

import java.io.IOException;
import java.io.Writer;

public class Variable implements Writable {

	private final String name;
	private final String value;

	public Variable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void write(Writer writer, int indentationLevel) throws IOException {
		Type type = getType(value);
		Compiluration.writeIndentation(writer, indentationLevel);
		writer.append("public static final ");
		writer.append(type.toString());
		writer.append(" ");
		writer.append(name);
		writer.append(" = ");
		type.appendValue(writer, value);
		writer.append(";\n");
	}

	private static Type getType(String value)  {
		Type type;
		try {
			Long.parseLong(value);
			type = Type.LONG;
		} catch (NumberFormatException nfe) {
			try {
				Double.parseDouble(value);
				type = Type.DOUBLE;
			} catch (NumberFormatException nfe2) {
				type = Type.STRING;
			}
		}
		return type;
	}

}
