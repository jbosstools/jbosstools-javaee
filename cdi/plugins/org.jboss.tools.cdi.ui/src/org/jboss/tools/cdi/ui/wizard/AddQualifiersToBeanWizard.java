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

import java.util.List;

import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.xpl.AddQualifiersToBeanComposite.ValuedQualifier;
import org.jboss.tools.common.model.ui.ModelUIImages;

public class AddQualifiersToBeanWizard extends AbstractModifyInjectionPointWizard{
	private AddQualifiersToBeanWizardPage page;
	
	public AddQualifiersToBeanWizard(ProcessorBasedRefactoring refactoring){
		super(refactoring);
		setWindowTitle(CDIUIMessages.SELECT_BEAN_WIZARD_TITLE);
		
		setDefaultPageImageDescriptor(ModelUIImages.getImageDescriptor(ModelUIImages.WIZARD_DEFAULT));
	}
	
    public void addUserInputPages() {
    	page = new AddQualifiersToBeanWizardPage(NLS.bind(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_TITLE, getSelectedBean().getElementName()));
    	addPage(page);
    }

	@Override
	public boolean performFinish() {
		return true;
	}
	
	public List<ValuedQualifier> getDeployedQualifiers(){
		return page.getDeployedQualifiers();
	}
	
	public List<IQualifier> getAvailableQualifiers(){
		return page.getAvailableQualifiers();
	}
	
	public void deploy(ValuedQualifier qualifier){
		page.deploy(qualifier);
	}

	public void remove(ValuedQualifier qualifier){
		page.remove(qualifier);
	}
	
	public boolean checkBeans(){
		return page.checkBeans();
	}
}
