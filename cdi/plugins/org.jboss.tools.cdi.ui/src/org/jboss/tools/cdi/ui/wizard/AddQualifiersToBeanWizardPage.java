/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.wizard;

import java.text.MessageFormat;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.cdi.ui.CDIUIMessages;

public class AddQualifiersToBeanWizardPage extends WizardPage{

	protected AddQualifiersToBeanWizardPage(String pageName) {
		super(pageName);
		setTitle(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_TITLE);
	}

	public void createControl(Composite parent) {
		setControl(new AddQualifiersToBeanComposite(parent, ((AddQualifiersToBeanWizard)getWizard()).getBean()));
		setMessage(MessageFormat.format(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_MESSAGE,
				new Object[]{((AddQualifiersToBeanWizard)getWizard()).getBean().getBeanClass().getElementName()}));
	}

}
