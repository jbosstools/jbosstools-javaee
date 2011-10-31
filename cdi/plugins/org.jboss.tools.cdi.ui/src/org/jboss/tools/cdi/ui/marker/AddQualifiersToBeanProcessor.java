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
package org.jboss.tools.cdi.ui.marker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.ui.refactoring.CDIRefactoringProcessor;
import org.jboss.tools.cdi.ui.wizard.xpl.AddQualifiersToBeanComposite.ValuedQualifier;

public class AddQualifiersToBeanProcessor extends CDIRefactoringProcessor {
	protected IBean selectedBean;
	protected IInjectionPoint injectionPoint;
	protected List<IBean> beans;
	protected ArrayList<ValuedQualifier> qualifiers;
	
	public AddQualifiersToBeanProcessor(String label, IInjectionPoint injectionPoint, List<IBean> beans, IBean bean) {
		super(label);
		this.selectedBean = bean;
		this.injectionPoint = injectionPoint;
		this.beans = beans;
	}
	
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		status = new RefactoringStatus();
		
		return status;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		
		createRootChange();

		MarkerResolutionUtils.addQualifiersToBean(qualifiers, selectedBean, rootChange);
		
		MarkerResolutionUtils.addQualifiersToInjectionPoint(qualifiers, injectionPoint, rootChange);
		
		return status;
	}
	
	protected void createRootChange(){
		rootChange = new CompositeChange(label);
	}
	
	public IBean getSelectedBean(){
		return selectedBean;
	}

	public IInjectionPoint getInjectionPoint(){
		return injectionPoint;
	}
	
	public List<IBean> getBeans(){
		return beans;
	}
	
	public void setSelectedBean(IBean bean){
		selectedBean = bean;
	}
	
	public void setDeployedQualifiers(ArrayList<ValuedQualifier> qualifiers){
		this.qualifiers = qualifiers;
	}
}
