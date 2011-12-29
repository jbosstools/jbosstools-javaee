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
package org.jboss.tools.cdi.internal.core.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.text.edits.MultiTextEdit;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.refactoring.JBDSFileChange;

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

		if(injectionPoint == null){
			status.addFatalError(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_INJECTION_POINT_NOT_FOUND);
			return status;
		}
		
		IFile injectionPointFile = (IFile)injectionPoint.getClassBean().getResource();
		
		isFileCorrect(injectionPointFile);
		
		return status;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		
		if(selectedBean == null){
			status.addFatalError(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_BEAN_NOT_FOUND);
			return status;
		}
		
		IFile beanFile = (IFile)selectedBean.getBeanClass().getResource();
		
		if(!isFileCorrect(beanFile)){
			return status;
		}
		return status;
	}
	
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		rootChange = new CompositeChange(getLabel());
		
		IFile file = (IFile)selectedBean.getBeanClass().getResource();
		ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
		
		ICompilationUnit compilationUnit = original.getWorkingCopy(pm);
		
		JBDSFileChange fileChange = new JBDSFileChange(file);
		
		MultiTextEdit edit = new MultiTextEdit();

		CDIMarkerResolutionUtils.addQualifiersToBean(qualifiers, selectedBean, compilationUnit, edit);
		
		IFile file2 = (IFile)injectionPoint.getClassBean().getResource();
		ICompilationUnit original2 = injectionPoint.getClassBean().getBeanClass().getCompilationUnit();
		ICompilationUnit compilationUnit2 = original2.getWorkingCopy(pm);
		
		if(!original.equals(original2)){
			compilationUnit.discardWorkingCopy();
			if(edit.getChildrenSize() > 0){
				fileChange.setEdit(edit);
				rootChange.add(fileChange);
			}
			fileChange = new JBDSFileChange(file2);
			
			edit = new MultiTextEdit();
		}else{
			compilationUnit2 = compilationUnit;
		}
	
		CDIMarkerResolutionUtils.addQualifiersToInjectionPoint(qualifiers, injectionPoint, compilationUnit2, edit);
		
		if(edit.getChildrenSize() > 0){
			fileChange.setEdit(edit);
			rootChange.add(fileChange);
		}
		compilationUnit.discardWorkingCopy();
		return rootChange;
	}
	
	@Override
	protected void createRootChange(){
		
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
