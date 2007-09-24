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

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;

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
		
		return true;
	}

}
