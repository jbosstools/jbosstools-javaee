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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.common.el.core.ELReference;
import org.jboss.tools.common.el.core.ElCoreMessages;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.resolver.ELCompletionEngine;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.MessagePropertyELSegment;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.refactoring.RefactorSearcher;

/**
 * @author Daniel Azarov
 */
public class RenameMessagePropertyProcessor extends ELRenameProcessor {
	IFile file;
	MessagePropertyELSegment segment;
	MessagePropertySearcher searcher;
	
	/**
	 * @param file where refactor was called
	 */
	public RenameMessagePropertyProcessor(IFile file, MessagePropertyELSegment segment) {
		super(file, segment.getToken().getText());
		this.file = file;
		this.segment = segment;
		setOldName(segment.getToken().getText());
		searcher = new MessagePropertySearcher(file, segment.getToken().getText());
	}
	
	protected RefactorSearcher getSearcher(){
		return searcher;
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
		
		rootChange = new CompositeChange(ElCoreMessages.RENAME_MESSAGE_PROPERTY_PROCESSOR_TITLE);
		
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
		
		//if(findManagedBean(file, getOldName()) == null)
			//result.addFatalError(Messages.format(ElCoreMessages.RENAME_EL_VARIABLE_PROCESSOR_CAN_NOT_FIND_EL_VARIABLE, getOldName()));
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
		return ElCoreMessages.RENAME_MESSAGE_PROPERTY_PROCESSOR_TITLE;
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
		TextFileChange change = getChange((IFile)segment.getMessageBundleResource());
		TextEdit edit = new ReplaceEdit(segment.getMessagePropertySourceReference().getStartPosition(), segment.getMessagePropertySourceReference().getLength(), getNewName());
		change.addEdit(edit);
		getSearcher().findELReferences();
	}
	
	class MessagePropertySearcher extends ELSearcher{

		public MessagePropertySearcher(IFile file, String oldName) {
			super(file, oldName);
		}
		
		protected void searchInCach(IFile file){
			ELContext context = PageContextFactory.createPageContext(file);
			
			if(context == null)
				return;
			
			ELReference[] references = context.getELReferences();
			ELResolver[] resolvers = context.getElResolvers();
			
			for(ELReference reference : references){
				int offset = reference.getStartPosition();
				for(ELExpression operand : reference.getEl()){
					for (ELResolver resolver : resolvers) {
						if (!(resolver instanceof ELCompletionEngine))
							continue;
						
						ELResolution resolution = resolver.resolve(context, operand, offset);
						
						if(resolution == null)
							continue;
	
						List<ELSegment> segments = resolution.findSegmentsByMessageProperty(segment.getBaseName(), propertyName);
						
						for(ELSegment segment : segments){
							match(file, offset+segment.getSourceReference().getStartPosition(), segment.getSourceReference().getLength());
						}
					}
				}
			}
			
		}
		
	}
	
}