package org.jboss.tools.seam.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.seam.core.ISeamPropertiesDeclaration;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.event.Change;

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
	
	public SeamProperty addStringProperty(String name, String value) {
		SeamProperty p = new SeamProperty(name, value);
		addProperty(p);
		return p;
	}

	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param d
	 * @return list of changes
	 */
	public List<Change> merge(SeamComponentDeclaration d) {
		List<Change> changes = super.merge(d);
		SeamPropertiesDeclaration pd = (SeamPropertiesDeclaration)d;
		
		Change children = new Change(this, null, null, null);

		String[] names = properties.keySet().toArray(new String[0]);
		for (int i = 0; i < names.length; i++) {
			SeamProperty p1 = (SeamProperty)properties.get(names[i]);
			SeamProperty p2 = (SeamProperty)pd.properties.get(names[i]);
			if(p2 == null) {
				changes = Change.addChange(changes, new Change(this, null, p1, null));
				properties.remove(names[i]);
			} else {
				List<Change> cc = p1.merge(p2);
				if(cc != null && cc.size() > 0) children.addChildren(cc);
			}
		}
		names = pd.properties.keySet().toArray(new String[0]);
		for (int i = 0; i < names.length; i++) {
			SeamProperty p1 = (SeamProperty)properties.get(names[i]);
			SeamProperty p2 = (SeamProperty)pd.properties.get(names[i]);
			if(p1 == null) {
				changes = Change.addChange(changes, new Change(this, null, null, p2));
				properties.put(names[i], p2);
			}
		}
		properties = pd.properties;

		changes = Change.addChange(changes, children);

		return changes;
	}

}
