package org.jboss.tools.cdi.internal.core.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.model.XModelObject;

public class FileSet {
	private Set<IPath> allpaths = new HashSet<IPath>();
	private Set<IPath> nonmodel = new HashSet<IPath>();
	private Map<IPath, Set<IType>> annotations = new HashMap<IPath, Set<IType>>();
	private Map<IPath, Set<IType>> interfaces = new HashMap<IPath, Set<IType>>();
	private Map<IPath, Set<IType>> classes = new HashMap<IPath, Set<IType>>();
	private Map<IPath, IPackageDeclaration> packages = new HashMap<IPath, IPackageDeclaration>();
	private Map<IPath, XModelObject> beanXMLs = new HashMap<IPath, XModelObject>();

	public FileSet() {}

	public void add(IPath path, IType[] types) throws CoreException {
		allpaths.add(path);
		if(types == null || types.length == 0) {
			nonmodel.add(path);
		} else {
			for (IType type: types) {
				add(path, type);
			}
		}
	}
	public void add(IPath path, IType type) throws CoreException {
		if(type == null) return;
		allpaths.add(path);
			if(!checkType(type, path)) return;
		if(type.isAnnotation()) {
			add(annotations, path, type);
		} else if(type.isInterface()) {
			add(interfaces, path, type);
		} else {
			add(classes, path, type);
			IType[] ts = type.getTypes();
			for (IType t: ts) {
				if(!checkType(t, path)) continue;
				if(Flags.isStatic(t.getFlags())) {
					add(path, t);
				}
			}
		}
	}

	private static Set<IPath> failedPaths = new HashSet<IPath>();

	/**
	 * Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=342757	
	 * This method and field failedPaths should be removed as soon as the 
	 * issue is fixed.
	 * @param type
	 * @param path
	 * @return
	 * @throws CoreException
	 */
	private boolean checkType(IType type, IPath path) throws CoreException {
		try {
			type.isAnnotation();
		} catch (ArrayIndexOutOfBoundsException e) {
			if(failedPaths.contains(path)) return false; // Do not let's be too noisy.
			failedPaths.add(path);
			CDICorePlugin.getDefault().logError("JDT failed to load " + type.getFullyQualifiedName() + " from " + path + "\nSee https://bugs.eclipse.org/bugs/show_bug.cgi?id=342757");
			return false;
		}
		return true;
	}

	private void add(Map<IPath, Set<IType>> target, IPath path, IType type) {
		Set<IType> ts = target.get(path);
		if(ts == null) {
			ts = new HashSet<IType>();
			target.put(path, ts);
		}
		ts.add(type);
	}

	public void add(IPath path, IPackageDeclaration pkg) throws CoreException {
		if(pkg == null) return;
		allpaths.add(path);
		packages.put(path, pkg);
	}

	public Set<IPath> getAllPaths() {
		return allpaths;
	}
	
	public Set<IPath> getNonModelFiles() {
		return nonmodel;
	}
	
	public Map<IPath, Set<IType>> getAnnotations() {
		return annotations;
	}

	public Map<IPath, Set<IType>> getInterfaces() {
		return interfaces;
	}

	public Map<IPath, Set<IType>> getClasses() {
		return classes;
	}

	public Map<IPath, IPackageDeclaration> getPackages() {
		return packages;
	}

	public XModelObject getBeanXML(IPath f) {
		return beanXMLs.get(f);
	}

	public void setBeanXML(IPath f, XModelObject o) {
		beanXMLs.put(f, o);
		allpaths.add(f);
	}

}
