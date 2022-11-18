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
			Map<String, Writable> properties = new LinkedHashMap<String, Writable>();
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
			String[] subKeys, String value, Map<String, Writable> properties) {
		int i = 1;
		for (String subKey : subKeys) {
			Writable property = properties.get(subKey);
			if (property == null) {
				if (i == subKeys.length) {
					properties.put(subKey, new Variable(subKey, value));
				} else {
					Class tempProperties = new Class(subKey);
					properties.put(subKey, tempProperties);
					properties = tempProperties.getWritables();
				}
			} else if (i != subKeys.length) {
				properties = ((Class) property).getWritables();
			}
			i++;
		}
	}

	private static void writeClassContent(Writer writer, Map<String, Writable> properties, int indentationLevel) throws IOException {
		for(Writable writable : properties.values()) {
			writable.write(writer, indentationLevel);
		}
	}

	public static void writeIndentation(Writer writer, int level) throws IOException {
		for(int i = 0 ; i < level; i++) {
			writer.append("\t");
		}
	}

}
