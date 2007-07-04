package org.jboss.tools.seam.internal.core.scanner;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamFactory;

public class LoadedDeclarations {
	List<SeamComponentDeclaration> components = new ArrayList<SeamComponentDeclaration>();
	List<SeamFactory> factories = new ArrayList<SeamFactory>();
	
	public List<SeamComponentDeclaration> getComponents() {
		return components;
	}
	
	public List<SeamFactory> getFactories() {
		return factories;
	}

}
