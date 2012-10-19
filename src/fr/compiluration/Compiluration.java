package fr.compiluration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.ResourceBundle;


public class Compiluration {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("use with a file name");
		} else {
			ResourceBundle bundle = ResourceBundle.getBundle(args[0]);
			Enumeration<String> keys = bundle.getKeys();
			File file = new File("gen/fr/compiluration/Properties.java");
			if (!file.exists()) {
				file.createNewFile();
			}
			Writer writer = new BufferedWriter(new FileWriter(file));
			writer.append("package fr.compiluration;\n\npublic final class Properties {\n");
			while(keys.hasMoreElements()) {
				String key = keys.nextElement();
				computeOneKey(writer, key, bundle.getString(key));
			}
			writer.append("}");
			writer.flush();
			writer.close();
		}
	}

	private static void computeOneKey(Writer writer, String key, String value) throws IOException {
		writer.append("\tpublic static final ");
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
		int i = 0;
		String[] keys = key.split("\\.");
		while (i < keys.length - 1) {
			writer.append("class ");
			writer.append(keys[i]);
			writer.append(" {\n\t");
			int j = -1;
			while (j < i) {
				writer.append("\t");
				j++;
			}
			writer.append("public static final ");
			i++;
		}
		writer.append(type.toString());
		writer.append(" ");
		writer.append(keys[i]);
		writer.append(" = ");
		type.appendValue(writer, value);
		writer.append(";\n");
		while (i > 0) {
			int j = i;
			while (j > 0) {
				writer.append("\t");
				j--;
			}
			writer.append("}\n");
			i--;
		}
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
