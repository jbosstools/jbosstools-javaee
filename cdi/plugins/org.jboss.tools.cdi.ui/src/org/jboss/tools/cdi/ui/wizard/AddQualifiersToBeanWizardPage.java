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

import java.util.ArrayList;

import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.internal.core.refactoring.AddQualifiersToBeanProcessor;
import org.jboss.tools.cdi.internal.core.refactoring.ValuedQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.xpl.AddQualifiersToBeanComposite;

public class AddQualifiersToBeanWizardPage extends UserInputWizardPage{

	private AddQualifiersToBeanComposite composite;
	
	protected AddQualifiersToBeanWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		composite = new AddQualifiersToBeanComposite(parent, this);
		setControl(composite);
	}
	
	public ArrayList<ValuedQualifier> getDeployedQualifiers(){
		return composite.getDeployedQualifiers();
	}

	public ArrayList<IQualifier> getAvailableQualifiers(){
		return composite.getAvailableQualifiers();
	}
	
	public void init(){
		composite.init();
		setTitle(NLS.bind(CDIUIMessages.ADD_QUALIFIERS_TO_BEAN_WIZARD_TITLE, ((AddQualifiersToBeanProcessor)((ProcessorBasedRefactoring)getRefactoring()).getProcessor()).getSelectedBean().getElementName()));
	}
	
	public void deploy(ValuedQualifier qualifier){
		composite.deploy(qualifier);
	}

	public void remove(ValuedQualifier qualifier){
		composite.remove(qualifier);
	}
	
	public boolean checkBeans(){
		return composite.checkBeans();
	}
	
	public void setDeployedQualifiers(ArrayList<ValuedQualifier> qualifiers){
		((AddQualifiersToBeanProcessor)((ProcessorBasedRefactoring)getRefactoring()).getProcessor()).setDeployedQualifiers(qualifiers);
	}
}
