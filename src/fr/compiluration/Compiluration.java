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
				addToProperties(
						key.split("\\."),
						bundle.getString(key),
						properties);
			}

			FileWriter fileWriter = null;
			Writer writer = null;
			try {
				File dir = new File("gen/fr/compiluration");
				dir.mkdirs();
				File file = new File(dir.getPath() + "/Properties.java");
				if (!file.exists()) {
					file.createNewFile();
				}
				fileWriter = new FileWriter(file);
				writer = new BufferedWriter(fileWriter);
				writer.append("package fr.compiluration;\n\npublic final class Properties {\n");
				writeClassContent(writer, properties, 1);
				writer.append("}");
			} finally {
				if (writer != null) {
					writer.close();
				}
				if (fileWriter != null) {
					fileWriter.close();
				}
			}
		}
	}

	private static void addToProperties(
			String[] subKeys, String value, Map<String, Object> properties) {
		int i = 1;
		for (String subKey : subKeys) {
			Object property = properties.get(subKey);
			if (property == null) {
				if (i == subKeys.length) {
					properties.put(subKey, value);
				} else {
					Map<String, Object> tempProperties = new LinkedHashMap<String, Object>();
					properties.put(subKey, tempProperties);
					properties = tempProperties;
				}
			} else {
				if (i != subKeys.length) {
					properties = (Map<String, Object>) property;
				}
			}
			i++;
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

	private static void writeClassContent(Writer writer, Map<String, Object> properties, int indentationLevel) throws IOException {
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

	private static void writeIndentation(Writer writer, int level) throws IOException {
		for(int i = 0 ; i < level; i++) {
			writer.append("\t");
		}
	}

}
