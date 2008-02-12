/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.views;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamPackage;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamReferencedFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof ISeamComponent) {
			ISeamComponent component = (ISeamComponent)element;
			return isComponentDeclaredInThisProject(component);
		} else if(element instanceof ISeamPackage) {
			ISeamPackage pkg = (ISeamPackage)element;
			return isPackageDeclaredInThisProject(pkg);
		}
		
		return true;
	}
	
	public static boolean isComponentDeclaredInThisProject(ISeamComponent component) {
		Set<ISeamComponentDeclaration> ds = component.getAllDeclarations();
		for (ISeamComponentDeclaration d : ds) {
			IResource r = d.getResource();
			if(r == null || !r.exists()) {
				//do not filter out the component if we cannot be sure that 
				//its source is another project 
				return true;
			}
			if(r != null && r.getProject() == d.getSeamProject().getProject()) {
				return true;
			}
		}
		return false;
	}

	boolean isPackageDeclaredInThisProject(ISeamPackage pkg) {
		Set<ISeamComponent> cs = pkg.getComponents();
		for (ISeamComponent c : pkg.getComponents()) {
			if(isComponentDeclaredInThisProject(c)) return true;
		}
		Map<String,ISeamPackage> ps = pkg.getPackages();
		for (ISeamPackage p : ps.values()) {
			if(isPackageDeclaredInThisProject(p)) {
				return true;
			}
		}		
		return false;
	}
}
