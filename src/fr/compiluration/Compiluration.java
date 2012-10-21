package fr.compiluration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class Compiluration {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("use with a file name");
		} else {
			ResourceBundle bundle = ResourceBundle.getBundle(args[0]);
			Enumeration<String> keys = bundle.getKeys();
			Map<String, Object> properties = new LinkedHashMap<String, Object>();
			while(keys.hasMoreElements()) {
				String key = keys.nextElement();
				String[] subKeys = key.split("\\.");
				int i = 1;
				Map<String, Object> currentProperties = properties;
				for (String subKey : subKeys) {
					Object property = currentProperties.get(subKey);
					if (property == null) {
						if (i == subKeys.length) {
							currentProperties.put(subKey, bundle.getString(key));
						} else {
							Map<String, Object> tempProperties = new LinkedHashMap<String, Object>();
							currentProperties.put(subKey, tempProperties);
							currentProperties = tempProperties;
						}
					} else {
						if (i != subKeys.length) {
							currentProperties = (Map<String, Object>) property;
						}
					}
					i++;
				}
			}
			
			File file = new File("gen/fr/compiluration/Properties.java");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file);
			Writer writer = new BufferedWriter(fileWriter);
			writer.append("package fr.compiluration;\n\npublic final class Properties {\n");
			writeClassContent(writer, properties, 1);
			writer.append("}");
			writer.close();
			fileWriter.close();
		}
	}

	public static void writeIndentation(Writer writer, int level) throws IOException {
		for(int i = 0 ; i < level; i++) {
			writer.append("\t");
		}
	}

	public static void writeClassContent(Writer writer, Map<String, Object> properties, int indentationLevel) throws IOException {
		for(Map.Entry<String, Object> entry : properties.entrySet()) {
			if (entry.getValue() instanceof String) {
				computeOneKey(writer, entry.getKey(), (String) entry.getValue(), indentationLevel); 
			} else {
				Map<String, Object> subProperties = (Map<String, Object>) entry.getValue();
				writeIndentation(writer, indentationLevel);
				writer.append("public static final class ").append(entry.getKey()).append(" {\n");
				writeClassContent(writer, subProperties, indentationLevel + 1);
				writeIndentation(writer, indentationLevel);
				writer.append("}\n");
			}
		}
	}

	private static void computeOneKey(Writer writer, String key, String value, int indentationLevel) throws IOException {
		writeIndentation(writer, indentationLevel);
		writer.append("public static final ");
		Type type;
		try {
			Integer.parseInt(value);
			type = Type.INT;
		} catch (NumberFormatException nfe) {
			try {
				Double.parseDouble(value);
				type = Type.DOUBLE;
			} catch (NumberFormatException nfe2) {
				type = Type.STRING;
			}
		}
		writer.append(type.toString());
		writer.append(" ");
		writer.append(key);
		writer.append(" = ");
		type.appendValue(writer, value);
		writer.append(";\n");
	}

	private static enum Type {
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

}
