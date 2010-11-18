package org.jboss.tools.seam.internal.core;

import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.seam.core.ISeamContextShortVariable;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ScopeType;

public class SeamContextShortVariable extends SeamObject implements ISeamContextShortVariable {
	ISeamContextVariable original;
	String prefix = null;
	
	public SeamContextShortVariable(ISeamContextVariable original) {
		this.original = original;
	}
	
	public SeamContextShortVariable(ISeamContextVariable original, String prefix) {
		this.original = original;
		this.prefix = prefix;
	}
	
	public ISeamContextVariable getOriginal() {
		return original;
	}

	public String getName() {
		String n = original.getName();
		if(prefix != null && n.startsWith(prefix)) {
			return n.substring(prefix.length());
		}
		int i = n.lastIndexOf('.');
		return n.substring(i + 1);
	}

	public ScopeType getScope() {
		return original.getScope();
	}

	public void setName(String name) {
	}

	public void setScope(ScopeType type) {
	}

	public SeamContextShortVariable clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public ITextSourceReference getLocationFor(String path) {
		return original.getLocationFor(path);
	}
}
