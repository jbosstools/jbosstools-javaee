/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.xml.ui.editor.form;

import org.jboss.tools.cdi.ui.wizard.NewBeanCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewDecoratorCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewInterceptorCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewStereotypeCreationWizard;
import org.jboss.tools.cdi.xml.beans.model.CDIBeansConstants;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.wizards.INewClassWizard;
import org.jboss.tools.common.model.ui.wizards.INewClassWizardFactory;

public class CDINewClassWizardFactory implements INewClassWizardFactory {

	public INewClassWizard createWizard(XModelObject context,
			XAttribute attribute) {
		if(context != null) {
			XModelObject folder = context;
			String entity = context.getModelEntity().getName();
			if(entity.equals(CDIBeansConstants.ENT_CDI_CLASS) || entity.equals(CDIBeansConstants.ENT_CDI_STEREOTYPE)) {
				folder = context.getParent();
			}
			String folderName = folder.getAttributeValue(CDIBeansConstants.ATTR_NAME);
			if("Interceptors".equals(folderName)) {
				return new NewInterceptorCreationWizard();
			} else if("Decorators".equals(folderName)) {
				return new NewDecoratorCreationWizard();
			} else if("Alternatives".equals(folderName)) {
				if("stereotype".equals(attribute.getName())) {
					return new NewStereotypeCreationWizard();
				} else if("class".equals(attribute.getName())) {
					return new NewBeanCreationWizard();
				}
			}
		}

		return null;
	}

}
