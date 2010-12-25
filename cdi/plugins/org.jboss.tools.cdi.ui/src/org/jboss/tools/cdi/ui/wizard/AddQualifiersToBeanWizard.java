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

import org.eclipse.jface.wizard.Wizard;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.model.ui.ModelUIImages;

public class AddQualifiersToBeanWizard extends Wizard{
	private IBean bean;
	
	public AddQualifiersToBeanWizard(IBean bean){
		this.bean = bean;
		setWindowTitle(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_TITLE);
		
		setDefaultPageImageDescriptor(ModelUIImages.getImageDescriptor(ModelUIImages.WIZARD_DEFAULT));
	}
	
    public void addPages() {
    	addPage(new AddQualifiersToBeanWizardPage(""));
    }


	@Override
	public boolean performFinish() {
		return false;
	}
	
	public IBean getBean(){
		return bean;
	}
}
