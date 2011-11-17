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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.text.edits.MultiTextEdit;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.ui.refactoring.CDIRefactoringProcessor;
import org.jboss.tools.cdi.ui.wizard.xpl.AddQualifiersToBeanComposite.ValuedQualifier;
import org.jboss.tools.common.EclipseUtil;

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
	
	private ICompilationUnit getCompilationUnit(IFile file) throws CoreException{
		IProject project = file.getProject();
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		IJavaElement element = javaProject.findElement(file.getProjectRelativePath());
		if(element instanceof ICompilationUnit)
			return (ICompilationUnit)element;
		
		return null;
	}
	
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		rootChange = new CompositeChange(label);
		
		IFile file = (IFile)selectedBean.getBeanClass().getResource();
		ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
		
		ICompilationUnit compilationUnit = original.getWorkingCopy(pm);
		
		TextFileChange fileChange = new CDIFileChange(file.getName(), file);
		if(getEditor(file) != null)
			fileChange.setSaveMode(TextFileChange.LEAVE_DIRTY);
		else
			fileChange.setSaveMode(TextFileChange.FORCE_SAVE);
		
		MultiTextEdit edit = new MultiTextEdit();

		MarkerResolutionUtils.addQualifiersToBean(qualifiers, selectedBean, compilationUnit, edit);
		
		IFile file2 = (IFile)injectionPoint.getClassBean().getResource();
		ICompilationUnit original2 = injectionPoint.getClassBean().getBeanClass().getCompilationUnit();
		ICompilationUnit compilationUnit2 = original2.getWorkingCopy(pm);
		
		if(!original.equals(original2)){
			compilationUnit.discardWorkingCopy();
			if(edit.getChildrenSize() > 0){
				fileChange.setEdit(edit);
				rootChange.add(fileChange);
			}
			fileChange = new CDIFileChange(file2.getName(), file2);
			if(getEditor(file2) != null)
				fileChange.setSaveMode(TextFileChange.LEAVE_DIRTY);
			else
				fileChange.setSaveMode(TextFileChange.FORCE_SAVE);
			
			edit = new MultiTextEdit();
		}else{
			compilationUnit2 = compilationUnit;
		}
	
		MarkerResolutionUtils.addQualifiersToInjectionPoint(qualifiers, injectionPoint, compilationUnit2, edit);
		
		if(edit.getChildrenSize() > 0){
			fileChange.setEdit(edit);
			rootChange.add(fileChange);
		}
		compilationUnit.discardWorkingCopy();
		return rootChange;
	}
	
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
