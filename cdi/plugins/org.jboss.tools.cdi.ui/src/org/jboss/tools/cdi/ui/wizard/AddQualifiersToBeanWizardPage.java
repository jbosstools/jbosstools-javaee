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
import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;

public class AddQualifiersToBeanWizardPage extends WizardPage{

	private AddQualifiersToBeanComposite composite;

	protected AddQualifiersToBeanWizardPage(String pageName) {
		super(pageName);
		setTitle(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_TITLE);
	}

	public void createControl(Composite parent) {
		composite = new AddQualifiersToBeanComposite(parent, this);
		setControl(composite);
	}
	
	public ArrayList<IQualifier> getDeployedQualifiers(){
		return composite.getDeployedQualifiers();
	}
	
}
