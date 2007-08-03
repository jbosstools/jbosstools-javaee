package org.jboss.tools.seam.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.event.Change;

public class SeamScope extends SeamObject implements ISeamScope {
	private List<ISeamComponent> components = new ArrayList<ISeamComponent>();
	private ScopeType scopeType = null;

	Map<String, ISeamPackage> packages = new HashMap<String, ISeamPackage>();
	
	SeamScope(SeamProject project, ScopeType scopeType) {
		setParent(project);
		this.scopeType = scopeType;
	}

	public List<ISeamComponent> getComponents() {
		return components;
	}
	
	public Collection<ISeamPackage> getPackages() {
		return packages.values();
	}
	
	public ISeamPackage getPackage(ISeamComponent c) {
		String pkg = SeamProject.getPackageName(c);
		return packages.get(pkg);
	}

	public void addComponent(ISeamComponent component) {
		components.add(component);		
	}
	
	public void removeComponent(ISeamComponent component) {
		components.remove(component);
	}

	public ScopeType getType() {
		return scopeType;
	}

	void revalidatePackages() {
		List<Change> changes = null;
		for (ISeamPackage p : packages.values()) {
			Iterator<ISeamComponent> cs = p.getComponents().iterator();
			while(cs.hasNext()) {
				ISeamComponent c = cs.next();
				String pkg = SeamProject.getPackageName(c);
				if(!components.contains(c) || !p.getName().equals(pkg)) {
					cs.remove();
					changes = Change.addChange(changes, new Change(p, null, c, null));
				}
			}
		}
		for (ISeamComponent c : getComponents()) {
			String pkg = SeamProject.getPackageName(c);
			ISeamPackage p = SeamProject.findOrCreatePackage(this, packages, pkg);
			if(p.getComponents().contains(c)) continue;
			p.getComponents().add(c);
			changes = Change.addChange(changes, new Change(p, null, null, c));
		}
		Iterator<String> ps = packages.keySet().iterator();
		while(ps.hasNext()) {
			ISeamPackage p = packages.get(ps.next());
			if(p.getComponents().size() == 0) {
				ps.remove();
				changes = Change.addChange(changes, new Change(this, null, p, null));
			}
		}
		((SeamProject)getSeamProject()).fireChanges(changes);
	}
	
}
