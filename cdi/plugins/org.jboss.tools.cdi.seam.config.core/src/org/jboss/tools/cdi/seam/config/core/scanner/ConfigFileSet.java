package org.jboss.tools.cdi.seam.config.core.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.model.XModelObject;

public class ConfigFileSet {
	private Set<IPath> allpaths = new HashSet<IPath>();
	private Map<IPath, XModelObject> beanXMLs = new HashMap<IPath, XModelObject>();
	private Map<IPath, XModelObject> seambeanXMLs = new HashMap<IPath, XModelObject>();

	public ConfigFileSet() {}

	public Set<IPath> getAllPaths() {
		return allpaths;
	}
	
	public XModelObject getBeanXML(IPath f) {
		return beanXMLs.get(f);
	}

	public XModelObject getSeamBeanXML(IPath f) {
		return seambeanXMLs.get(f);
	}

	public void setBeanXML(IPath f, XModelObject o) {
		beanXMLs.put(f, o);
		allpaths.add(f);
	}

	public void setSeamBeanXML(IPath f, XModelObject o) {
		seambeanXMLs.put(f, o);
		allpaths.add(f);
	}

}
