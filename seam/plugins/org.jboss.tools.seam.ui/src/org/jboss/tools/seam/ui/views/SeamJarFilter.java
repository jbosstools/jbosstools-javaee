/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.views;

import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamJarFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof ISeamComponent) {
			return isUserDefinedComponent((ISeamComponent)element);
		}
		return true;
	}
	
	boolean isUserDefinedComponent(ISeamComponent c) {
		Set<ISeamComponentDeclaration> ds = c.getAllDeclarations();
		for (ISeamComponentDeclaration d : ds) {
			IPath path = d.getSourcePath();
			if(path != null && !path.toString().endsWith(".jar")) {
				return true;
			}
		}
		
		return false;
	}
	
	

}
