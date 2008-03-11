package org.jboss.tools.seam.internal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.event.Change;

public class SeamPackageUtil {
	
	public static List<Change> revalidatePackages(ISeamElement parent, Map<String,SeamComponent> allComponents, Collection<ISeamComponent> components, Map<String,ISeamPackage> packages) {
		List<Change> changes = null;
		changes = checkPackages(allComponents, packages, changes);
		changes = fillPackages(parent, packages, components, changes);
		changes = cleanPackages(parent, packages, changes);
		return changes;
	}	

	static List<Change> checkPackages(Map<String,SeamComponent> allComponents, Map<String,ISeamPackage> packages, List<Change> changes) {
		for (ISeamPackage p : packages.values()) {
			Iterator<ISeamComponent> cs = p.getComponents().iterator();
			while(cs.hasNext()) {
				ISeamComponent c = cs.next();
				String pkg = getPackageName(c);
				if(allComponents.get(c.getName()) == null || !p.getQualifiedName().equals(pkg)) {
					cs.remove();
					changes = Change.addChange(changes, new Change(p, null, c, null));
				}
			}
			changes = checkPackages(allComponents, p.getPackages(), changes);
		}
		return changes;
	}

	static List<Change> fillPackages(ISeamElement parent, Map<String,ISeamPackage> packages, Collection<ISeamComponent> components, List<Change> changes) {
		for (ISeamComponent c : components) {
			String pkg = getPackageName(c);
			ISeamPackage p = findOrCreatePackage(parent, packages, pkg);
			if(p.getComponents().contains(c)) continue;
			p.getComponents().add(c);
			changes = Change.addChange(changes, new Change(p, null, null, c));
		}
		return changes;
	}
	
	static List<Change> cleanPackages(ISeamElement parent, Map<String,ISeamPackage> packages, List<Change> changes) {
		Iterator<String> ps = packages.keySet().iterator();
		while(ps.hasNext()) {
			ISeamPackage p = packages.get(ps.next());
			changes = cleanPackages(p, p.getPackages(), changes);
			if(p.getComponents().size() == 0 && p.getPackages().size() == 0) {
				ps.remove();
				changes = Change.addChange(changes, new Change(parent, null, p, null));
			}
		}
		return changes;
	}

	static String getPackageName(ISeamComponent c) {
		// Package name can be based 
		// 1) on qualified java class name, 
		//    then it is java package name
		// 2) on seam component name, 
		//    then name is processed by analogy with java,
		//    and package name is its part until the last dot.
		
		// Presently, only second approach is implemented,
		// in future an option can be added, that 
		// will allow user to customize view by selecting 
		// the 'kind' of packages.
		String cls = c.getName();
				//c.getClassName();
		if(cls == null || cls.length() == 0) {
			return "(unspecified)"; //$NON-NLS-1$
		} else if(cls.startsWith("${") || cls.startsWith("#{")) { //$NON-NLS-1$ //$NON-NLS-2$
			return "(specified with EL)"; //$NON-NLS-1$
		} else {
			int d = cls.lastIndexOf('.');
			return (d < 0) ? "(default package)" : cls.substring(0, d); //$NON-NLS-1$
		}
	}
	
	static ISeamPackage findPackage(ISeamElement parent, Map<String, ISeamPackage> packages, String qualifiedName) {
		return findOrCreatePackage(parent, packages, qualifiedName, false);
	}

	static ISeamPackage findOrCreatePackage(ISeamElement parent, Map<String, ISeamPackage> packages, String qualifiedName) {
		return findOrCreatePackage(parent, packages, qualifiedName, true);
	}

	static ISeamPackage findOrCreatePackage(ISeamElement parent, Map<String, ISeamPackage> packages, String qualifiedName, boolean create) {
		int i = qualifiedName.indexOf('.');
		String firstName = i < 0 ? qualifiedName : qualifiedName.substring(0, i);
		String tail = i < 0 ? null : qualifiedName.substring(i + 1);
		
		ISeamPackage p = packages.get(firstName);
		if(p == null) {
			if(!create) return null;
			SeamPackage pi = new SeamPackage(firstName);
			pi.setParent(parent);
			pi.setSourcePath(parent.getSourcePath());
			p = pi;
			packages.put(firstName, pi);
		}
		return (tail != null) ? findOrCreatePackage(p, p.getPackages(), tail) : p;
	}
	
	public static void collectAllPackages(Map<String, ISeamPackage> packages, Collection<ISeamPackage> list) {
		for (ISeamPackage p : packages.values()) {
			if(p.getComponents().size() > 0) list.add(p);
			collectAllPackages(p.getPackages(), list);
		}
	}
	
}
