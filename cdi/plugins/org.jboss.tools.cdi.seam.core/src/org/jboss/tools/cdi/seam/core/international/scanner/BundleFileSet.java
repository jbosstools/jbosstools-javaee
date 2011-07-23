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
package org.jboss.tools.cdi.seam.core.international.scanner;

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
public class BundleFileSet {
	private Set<IPath> allpaths = new HashSet<IPath>();
	private Map<IPath, Set<XModelObject>> bundles = new HashMap<IPath, Set<XModelObject>>();

	public BundleFileSet() {}

	public Set<IPath> getAllPaths() {
		return allpaths;
	}
	
	public Set<XModelObject> getBundles(IPath f) {
		return bundles.get(f);
	}

	public void setBundle(IPath f, XModelObject o) {
		Set<XModelObject> set = bundles.get(f);
		if(set == null) {
			set = new HashSet<XModelObject>();
			bundles.put(f, set);
		}
		set.add(o);
		allpaths.add(f);
	}

	public void setBundles(IPath f, Set<XModelObject> set) {
		bundles.put(f, set);
		allpaths.add(f);
	}

}
