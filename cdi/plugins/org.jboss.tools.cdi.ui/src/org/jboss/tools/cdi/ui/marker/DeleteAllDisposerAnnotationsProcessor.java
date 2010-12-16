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
package org.jboss.tools.cdi.ui.marker;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.ltk.internal.core.refactoring.Messages;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.text.ITextSourceReference;

public class DeleteAllDisposerAnnotationsProcessor extends RefactoringProcessor {
	protected static final RefactoringParticipant[] EMPTY_REF_PARTICIPANT = new  RefactoringParticipant[0];	
	private IFile file;
	private IMethod method;
	private RefactoringStatus status;
	
	private CompositeChange rootChange;
	private TextFileChange change;
	private IClassBean bean;

	
	public DeleteAllDisposerAnnotationsProcessor(IFile file, IMethod method){
		this.file = file;
		this.method = method;
	}
	
	private IClassBean findClassBean(){
		CDICoreNature cdiNature = CDICorePlugin.getCDI(file.getProject(), true);
		if(cdiNature == null)
			return null;
		
		ICDIProject cdiProject = cdiNature.getDelegate();
		
		if(cdiProject == null)
			return null;
		
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		
		for(IBean bean : beans){
			if(bean instanceof IClassBean)
				return (IClassBean)bean;
		}
		
		return null;
	}
	
	private void changeDisposers(IClassBean bean) {
		Set<IBeanMethod> disposers = bean.getDisposers();
		if (disposers.isEmpty()) {
			return;
		}

		Set<IBeanMethod> boundDisposers = new HashSet<IBeanMethod>();
		Set<IProducer> producers = bean.getProducers();
		for (IProducer producer : producers) {
			if (producer instanceof IProducerMethod) {
				IProducerMethod producerMethod = (IProducerMethod) producer;
				Set<IBeanMethod> disposerMethods = producer.getCDIProject().resolveDisposers(producerMethod);
				boundDisposers.addAll(disposerMethods);
				if (disposerMethods.size() > 1) {
					for (IBeanMethod disposerMethod : disposerMethods) {
						if(!disposerMethod.getMethod().isSimilar(method)){
							Set<ITextSourceReference> disposerDeclarations = CDIUtil.getAnnotationPossitions(disposerMethod, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
							for (ITextSourceReference declaration : disposerDeclarations) {
								TextEdit edit = new ReplaceEdit(declaration.getStartPosition(), declaration.getLength(), "");
								change.addEdit(edit);
							}
						}
					}
				}
			}
		}
	}
	
	private boolean isFileCorrect(IFile file){
		if(!file.isSynchronized(IResource.DEPTH_ZERO)){
			status.addFatalError(Messages.format(CDICoreMessages.CDI_RENAME_PROCESSOR_OUT_OF_SYNC_FILE, file.getFullPath().toString()));
			return false;
		}else if(file.isPhantom()){
			status.addFatalError(Messages.format(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_PHANTOM_FILE, file.getFullPath().toString()));
			return false;
		}else if(file.isReadOnly()){
			status.addFatalError(Messages.format(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_READ_ONLY_FILE, file.getFullPath().toString()));
			return false;
		}
		return true;
	}

	@Override
	public Object[] getElements() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return "";
	}

	@Override
	public String getProcessorName() {
		return MessageFormat.format(CDIUIMessages.DELETE_ALL_DISPOSER_DUPLICANT_MARKER_RESOLUTION_TITLE, new Object[]{method.getElementName()});
	}

	@Override
	public boolean isApplicable() throws CoreException {
		return true;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		status = new RefactoringStatus();
		
		if(isFileCorrect(file)){
			bean = findClassBean();
		}else
			status.addFatalError("CDI Bean Class not found");
		
		return status;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		
		rootChange = new CompositeChange(MessageFormat.format(CDIUIMessages.DELETE_ALL_DISPOSER_DUPLICANT_MARKER_RESOLUTION_TITLE, new Object[]{method.getElementName()}));
		change = new TextFileChange(file.getName(), file);
		MultiTextEdit root = new MultiTextEdit();
		change.setEdit(root);
		rootChange.add(change);
		
		if(bean != null)
			changeDisposers(bean);
		
		return status;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return rootChange;
	}

	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
			SharableParticipants sharedParticipants) throws CoreException {
		return EMPTY_REF_PARTICIPANT;
	}

}
