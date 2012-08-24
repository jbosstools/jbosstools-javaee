package org.jboss.tools.cdi.gen.model;

public class BodyWriter {
	StringBuilder sb = new StringBuilder();
	int indent = 0;
	boolean lineStarted = true;
	

	public BodyWriter append(Object o) {
		if(lineStarted) {
			for (int i = 0; i < indent; i++) sb.append("\t");
			lineStarted = false;
		}
		sb.append(o);
		return this;
	}

	public BodyWriter newLine() {
		sb.append("\n");
		lineStarted = true;
		return this;
	}

	public BodyWriter increaseIndent() {
		indent++;
		return this;
	}

	public BodyWriter decreaseIndent() {
		indent--;
		return this;
	}

	public String toString() {
		return sb.toString();
	}

}
