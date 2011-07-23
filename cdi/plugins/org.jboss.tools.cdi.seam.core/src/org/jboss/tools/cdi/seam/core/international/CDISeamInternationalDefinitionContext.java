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
package org.jboss.tools.cdi.seam.core.international;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.seam.core.international.scanner.BundleFileSet;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamInternationalDefinitionContext extends AbstractDefinitionContextExtension {
	private Map<IPath, Set<XModelObject>> bundles = new HashMap<IPath, Set<XModelObject>>();
	private Set<XModelObject> allBundles = new HashSet<XModelObject>();

	public CDISeamInternationalDefinitionContext() {}

	@Override
	protected AbstractDefinitionContextExtension copy(boolean clean) {
		CDISeamInternationalDefinitionContext copy = new CDISeamInternationalDefinitionContext();
		copy.root = root;
		if(!clean) {
			copy.bundles.putAll(bundles);
		}
		return copy;
	}

	protected void doApplyWorkingCopy() {
		CDISeamInternationalDefinitionContext copy = (CDISeamInternationalDefinitionContext)workingCopy;
		bundles = copy.bundles;
		allBundles = copy.allBundles;
	}

	public void clean() {
		synchronized(bundles) {
			bundles.clear();
		}
		synchronized (allBundles) {
			allBundles.clear();
		}
	}

	public void clean(IPath path) {
		Set<XModelObject> bs = null;
		synchronized(bundles) {
			bs = bundles.remove(path);
		}
		if(bs != null) {
			synchronized (allBundles) {
				allBundles.removeAll(bs);
			}
		}
	}

	public void addDefinitions(BundleFileSet fileSet) {
		for (IPath path: fileSet.getAllPaths()) {
			Set<XModelObject> bs = fileSet.getBundles(path);
			synchronized(bundles) {
				bundles.put(path, bs);
			}
			synchronized (allBundles) {
				allBundles.addAll(bs);
			}
		}
	}

	public Set<XModelObject> getAllBundles() {
		return allBundles;
	}

}
