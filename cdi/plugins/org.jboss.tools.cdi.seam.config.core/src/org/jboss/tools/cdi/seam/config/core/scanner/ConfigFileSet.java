/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.core.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
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
