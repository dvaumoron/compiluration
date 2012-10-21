package fr.compiluration;

import java.io.IOException;
import java.io.Writer;

public enum Type {

	INT("int"),
	DOUBLE("double"),
	STRING("String") {
		public void appendValue(Writer builder, String value) throws IOException {
			builder.append("\"");
			builder.append(value);
			builder.append("\"");
		}
	};
	
	private String name;

	private Type(String name) {
		this.name = name;
	}

	public void appendValue(Writer writer, String value) throws IOException {
		writer.append(value);
	}

	@Override
	public String toString() {
		return name;
	}
}