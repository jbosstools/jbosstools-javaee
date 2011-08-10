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
package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * CDI Named Bean Filter
 * 
 * @author Victor V. Rubezhny
 */
public class CDINamedBeanReferencedFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IBean) {
			IBean bean = (IBean)element;
			return isBeanDeclaredInThisProject(bean);
		}
		
		return true;
	}
	
	public static boolean isBeanDeclaredInThisProject(IBean bean) {
		ICDIProject beanProject = bean.getCDIProject();
		IResource beanResource = null;
		
		//1. Get @Named declared directly, not in stereotype.
		ITextSourceReference nameLocation = bean.getNameLocation(false);
		//2. Get stereotype declaration declaring @Named, if @Named is not declared directly.
		ITextSourceReference stereotypeLocation = nameLocation != null ? null : bean.getNameLocation(true);
		if (nameLocation != null) {
			beanResource = nameLocation.getResource();
		} else if (stereotypeLocation != null) {
			beanResource = stereotypeLocation.getResource();
		}
		if (beanResource == null)
			return false;
		
		ICDIProject cdiProject = CDICorePlugin.getCDIProject(beanResource.getProject(),
				true);
		return cdiProject == beanProject;
	}
}
