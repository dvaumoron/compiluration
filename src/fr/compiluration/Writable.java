package fr.compiluration;

import java.io.IOException;
import java.io.Writer;

public interface Writable {

	public void write(Writer writer, int indentationLevel) throws IOException;

}
