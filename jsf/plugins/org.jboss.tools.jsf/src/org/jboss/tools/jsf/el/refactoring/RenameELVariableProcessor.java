 /*******************************************************************************
  * Copyright (c) 2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jsf.el.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.ltk.internal.core.refactoring.Messages;
import org.jboss.tools.common.el.core.ElCoreMessages;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.refactoring.RenameModelObjectChange;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.pv.JSFBeanSearcher;

/**
 * @author Daniel Azarov
 */
public class RenameELVariableProcessor extends ELRenameProcessor {
	IFile file;
	
	/**
	 * @param file where refactor was called
	 */
	public RenameELVariableProcessor(IFile file, String oldName) {
		super(file, oldName);
		this.file = file;
		setOldName(oldName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkFinalConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		status = new RefactoringStatus();
		
		rootChange = new CompositeChange(ElCoreMessages.RENAME_EL_VARIABLE_PROCESSOR_TITLE);
		
		renameELVariable(pm, file);
		
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkInitialConditions(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus result = new RefactoringStatus();
		
		if(findManagedBean() == null)
			result.addFatalError(Messages.format(ElCoreMessages.RENAME_EL_VARIABLE_PROCESSOR_CAN_NOT_FIND_EL_VARIABLE, getOldName()));
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		
		return rootChange;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
	 */
	@Override
	public Object[] getElements() {
		return new String[]{getNewName()};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getProcessorName()
	 */
	@Override
	public String getProcessorName() {
		return ElCoreMessages.RENAME_EL_VARIABLE_PROCESSOR_TITLE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
	 */
	@Override
	public boolean isApplicable() throws CoreException {
		return getNewName()!=null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#loadParticipants(org.eclipse.ltk.core.refactoring.RefactoringStatus, org.eclipse.ltk.core.refactoring.participants.SharableParticipants)
	 */
	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
			SharableParticipants sharedParticipants) throws CoreException {
		return EMPTY_REF_PARTICIPANT;
	}
	
	private void renameELVariable(IProgressMonitor pm, IFile file){
		XModelObject managedBean = findManagedBean();
		if(managedBean != null){
			Change managedBeanChange = RenameModelObjectChange.createChange(new XModelObject[]{managedBean}, getNewName(), "managed-bean-name");
			rootChange.add(managedBeanChange);
			getSearcher().findELReferences();
		}
	}
	
	private XModelObject findManagedBean(){
		IModelNature nature = EclipseResourceUtil.getModelNature(file.getProject());
		if(nature == null)
			return null;
		XModel model = nature.getModel();
		if(model == null)
			return null;
		JSFBeanSearcher beanSearcher = new JSFBeanSearcher(model);
		beanSearcher.parse(getOldName());
		XModelObject managedBean = beanSearcher.getBean();
		
		return managedBean;
	}
}