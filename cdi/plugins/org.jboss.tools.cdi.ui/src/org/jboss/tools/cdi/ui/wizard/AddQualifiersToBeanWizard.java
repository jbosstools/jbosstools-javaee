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

import org.eclipse.jface.wizard.Wizard;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.model.ui.ModelUIImages;

public class AddQualifiersToBeanWizard extends Wizard{
	private IInjectionPoint injectionPoint;
	private List<IBean> beans;
	private IBean bean;
	private AddQualifiersToBeanWizardPage page;
	
	public AddQualifiersToBeanWizard(IInjectionPoint injectionPoint, List<IBean> beans, IBean bean){
		this.injectionPoint = injectionPoint;
		this.beans = beans;
		this.bean = bean;
		setWindowTitle(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_TITLE);
		
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
	
	public List<IQualifier> getDeployedQualifiers(){
		return page.getDeployedQualifiers();
	}
	
	public IInjectionPoint getInjectionPoint(){
		return injectionPoint;
	}
	
	public List<IBean> getBeans(){
		return beans;
	}
	
	public IBean getBean(){
		return bean;
	}
}
