package org.jboss.tools.seam.internal.core.scanner;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.seam.internal.core.SeamComponent;
import org.jboss.tools.seam.internal.core.SeamFactory;

public class LoadedDeclarations {
	List<SeamComponent> components = new ArrayList<SeamComponent>();
	List<SeamFactory> factories = new ArrayList<SeamFactory>();
	
	public List<SeamComponent> getComponents() {
		return components;
	}
	
	public List<SeamFactory> getFactories() {
		return factories;
	}

}
