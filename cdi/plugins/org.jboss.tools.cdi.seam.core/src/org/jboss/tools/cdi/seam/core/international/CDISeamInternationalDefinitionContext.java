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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.CDICoreNature;
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
			copy.allBundles.addAll(allBundles);
		}
		return copy;
	}

	protected void doApplyWorkingCopy() {
		CDISeamInternationalDefinitionContext copy = (CDISeamInternationalDefinitionContext)workingCopy;
		bundles = copy.bundles;
		allBundles = copy.allBundles;
	}

	public synchronized void clean() {
		bundles.clear();
		allBundles.clear();
	}

	public synchronized void clean(IPath path) {
		Set<XModelObject> bs = null;
		bs = bundles.remove(path);
		if(bs != null) {
			allBundles.removeAll(bs);
		}
	}

	public synchronized void addDefinitions(BundleFileSet fileSet) {
		for (IPath path: fileSet.getAllPaths()) {
			clean(path);
			Set<XModelObject> bs = fileSet.getBundles(path);
			bundles.put(path, bs);
			allBundles.addAll(bs);
		}
	}

	synchronized Set<XModelObject> getBundles() {
		//filter out obsolete objects.
		Iterator<XModelObject> i = allBundles.iterator();
		while(i.hasNext()) {
			if(!i.next().isActive()) {
				i.remove();
			}
		}
		return allBundles;
	}

	public synchronized Set<XModelObject> getAllBundles() {
		Set<XModelObject> result = new HashSet<XModelObject>();
		result.addAll(getBundles());
		Set<CDICoreNature> ns = root.getProject().getCDIProjects(true);
		for (CDICoreNature n: ns) {
			CDISeamInternationalExtension extension = CDISeamInternationalExtension.getExtension(n);
			if(extension != null) {
				result.addAll(extension.getContext().getBundles());
			}
		}
		return result;
	}

}
