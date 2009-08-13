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
package org.jboss.tools.jsf.model.handlers.bean;

import java.util.Properties;

import org.eclipse.jdt.core.IField;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.model.pv.*;

public class JSFRenameFieldHelper {

	public static Properties getReplacements(XModelObject beanProperty, String newName) {
		Properties p = new Properties();
		if(beanProperty == null) return p;
		XModelObject parent = beanProperty.getParent();
		String oldName = beanProperty.getAttributeValue("property-name"); //$NON-NLS-1$
		String b = "#{" + parent.getAttributeValue("managed-bean-name") + "." + oldName + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		String e = "#{" + parent.getAttributeValue("managed-bean-name") + "." + newName + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		p.setProperty(b, e);
		return p;
	}

	public static Properties getReplacements(XModel model, IField field, String newName) {
		Properties p = new Properties();
		if(model == null || field == null) return p;
		String oldName = field.getElementName();
		XModelObject[] beans = getBeanList(model, field);
		for (int i = 0; i < beans.length; i++) {
			String b = "#{" + beans[i].getAttributeValue("managed-bean-name") + "." + oldName + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			String e = "#{" + beans[i].getAttributeValue("managed-bean-name") + "." + newName + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			p.setProperty(b, e);			
		}
		return p;
	}
	
	public static XModelObject[] getBeanList(XModel model, IField field) {
		String clsname = field.getDeclaringType().getFullyQualifiedName();
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		JSFProjectBeans beans = (root == null) ? null : (JSFProjectBeans)root.getChildByPath(JSFProjectTreeConstants.BEANS);
		XModelObject[] cs = beans.getTreeChildren();
		for (int i = 0; i < cs.length; i++) {
			JSFProjectBean bean = (JSFProjectBean)cs[i];
			if(clsname.equals(bean.getAttributeValue("class name"))) return bean.getBeanList(); //$NON-NLS-1$
		}
		return new XModelObject[0];
	}

}
