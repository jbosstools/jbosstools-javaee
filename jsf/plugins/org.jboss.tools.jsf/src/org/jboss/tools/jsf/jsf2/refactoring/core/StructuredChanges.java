package org.jboss.tools.jsf.jsf2.refactoring.core;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;

public class StructuredChanges extends CompositeChange {

	public StructuredChanges(String name, Change[] children) {
		super(name, children);
	}

	public StructuredChanges(String name){
		super(name);
	}
	
}
