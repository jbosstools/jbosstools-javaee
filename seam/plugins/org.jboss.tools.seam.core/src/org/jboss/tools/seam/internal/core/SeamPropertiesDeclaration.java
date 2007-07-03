package org.jboss.tools.seam.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.seam.core.ISeamPropertiesDeclaration;
import org.jboss.tools.seam.core.ISeamProperty;

public class SeamPropertiesDeclaration extends SeamComponentDeclaration
		implements ISeamPropertiesDeclaration {

	protected Map<String,ISeamProperty> properties = new HashMap<String, ISeamProperty>();

	public void addProperty(ISeamProperty property) {
		properties.put(property.getName(), property);
	}

	public List<ISeamProperty> getProperties(String propertyName) {
		List<ISeamProperty> list = new ArrayList<ISeamProperty>();
		ISeamProperty p = properties.get(propertyName);
		if(p != null) list.add(p);
		return list;
	}

	public Collection<ISeamProperty> getProperties() {
		return properties.values();
	}

	public ISeamProperty getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	public void removeProperty(ISeamProperty property) {
		properties.remove(property.getName());		
	}
	
	public void addStringProperty(String name, String value) {
		SeamProperty p = new SeamProperty(name, value);
		addProperty(p);
	}

}
