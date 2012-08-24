package org.jboss.tools.cdi.gen.model;

public class GenVariable extends GenMember {
	private GenType type;

	public void setType(GenType type) {
		this.type = type;
	}

	public GenType getType() {
		return type;
	}

	public void flush(BodyWriter sb) {
		flushAnnotations(sb, true);
		flushVisibility(sb);
		sb.append(getType().getTypeName()).append(" ").append(getName());
	}
}
