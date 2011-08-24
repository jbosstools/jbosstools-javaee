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

import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.xpl.AddQualifiersToBeanComposite.ValuedQualifier;
import org.jboss.tools.common.model.ui.ModelUIImages;

public class AddQualifiersToBeanWizard extends AbstractModifyInjectionPointWizard{
	private AddQualifiersToBeanWizardPage page;
	
	public AddQualifiersToBeanWizard(IInjectionPoint injectionPoint, List<IBean> beans, IBean bean){
		super(injectionPoint, beans, bean);
		setWindowTitle(CDIUIMessages.SELECT_BEAN_WIZARD_TITLE);
		
		setDefaultPageImageDescriptor(ModelUIImages.getImageDescriptor(ModelUIImages.WIZARD_DEFAULT));
	}
	
    public void addPages() {
    	page = new AddQualifiersToBeanWizardPage("");
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
