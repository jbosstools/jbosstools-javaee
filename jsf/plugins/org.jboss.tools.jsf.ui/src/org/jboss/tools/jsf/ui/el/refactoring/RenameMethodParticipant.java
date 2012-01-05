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
package org.jboss.tools.jsf.ui.el.refactoring;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ISharableParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.common.model.project.ProjectHome;
import org.jboss.tools.common.util.BeanUtil;
import org.jboss.tools.jsf.ui.JsfUIMessages;
import org.jboss.tools.jst.web.kb.refactoring.ELProjectSetExtension;
import org.jboss.tools.jst.web.kb.refactoring.IProjectsSet;
import org.jboss.tools.jst.web.kb.refactoring.RefactorSearcher;

public class RenameMethodParticipant extends RenameParticipant implements ISharableParticipant{
	private IJavaElement element;
	private String oldName;
	private String newName;
	private RenameMethodSearcher searcher;
	private RefactoringStatus status;
	private CompositeChange rootChange;
	private TextFileChange lastChange;
	private ArrayList<String> keys = new ArrayList<String>();
	private ArrayList<Object> otherElements = new ArrayList<Object>();
	
	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		if(searcher == null)
			return status;
		if(element instanceof IMethod) {
			IMethod method = (IMethod)element;
			IMethod anotherMethod = getAnotherMethod();
			if(method != null){
				if(BeanUtil.isGetter(method)){
					if(anotherMethod == null || BeanUtil.isGetter(anotherMethod))
						status.addWarning(JsfUIMessages.RENAME_METHOD_PARTICIPANT_GETTER_WARNING);
					
				}else if(BeanUtil.isSetter(method)){
					if(anotherMethod == null || BeanUtil.isSetter(anotherMethod))
						status.addWarning(JsfUIMessages.RENAME_METHOD_PARTICIPANT_SETTER_WARNING);
				}
				
				searcher.findELReferences();
			}
		}
		
		return status;
	}
	
	private IMethod getAnotherMethod(){
		for(Object object : otherElements)
			if(object instanceof IMethod)
				return (IMethod)object;
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		if(rootChange.getChildren().length > 0)
			return rootChange;
		
		return null;
	}

	@Override
	public String getName() {
		return oldName;
	}

	@Override
	protected boolean initialize(Object element) {
		if(!getArguments().getUpdateReferences())
			return false;

		if(element instanceof IMethod){
			IMethod method = (IMethod)element;
			status = new RefactoringStatus();
			
			rootChange = new CompositeChange(JsfUIMessages.RENAME_METHOD_PARTICIPANT_UPDATE_METHOD_REFERENCES);
			
			this.element = method;
			
			oldName = method.getElementName();
			
			newName = RefactorSearcher.getPropertyName(method, getArguments().getNewName());
			searcher = new RenameMethodSearcher((IFile)method.getResource(), oldName);
			return true;
		}
		return false;
	}
	
	// for test only
	public boolean initialize(Object element, String newName) {
		if(element instanceof IMethod){
			IMethod method = (IMethod)element;
			status = new RefactoringStatus();
			
			rootChange = new CompositeChange(JsfUIMessages.RENAME_METHOD_PARTICIPANT_UPDATE_METHOD_REFERENCES);
			
			this.element = method;
			
			oldName = method.getElementName();
			
			this.newName = newName;
			searcher = new RenameMethodSearcher((IFile)method.getResource(), oldName);
			return true;
		}
		return false;
	}
	
	protected TextFileChange getChange(IFile file){
		if(lastChange != null && lastChange.getFile().equals(file))
			return lastChange;
		
		for(int i=0; i < rootChange.getChildren().length; i++){
			TextFileChange change = (TextFileChange)rootChange.getChildren()[i];
			if(change.getFile().equals(file)){
				lastChange = change;
				return lastChange;
			}
		}
		lastChange = new TextFileChange(file.getName(), file);
		MultiTextEdit root = new MultiTextEdit();
		lastChange.setEdit(root);
		rootChange.add(lastChange);
		
		return lastChange;
	}
	
	private void change(IFile file, int offset, int length, String text){
		String key = file.getFullPath().toString()+" "+offset;
		if(!keys.contains(key)){
			TextFileChange change = getChange(file);
			TextEdit edit = new ReplaceEdit(offset, length, text);
			change.addEdit(edit);
			keys.add(key);
		}
	}
	
	class RenameMethodSearcher extends RefactorSearcher{
		IProjectsSet projectSet=null;
		public RenameMethodSearcher(IFile file, String name){
			super(file, name, element);
			ELProjectSetExtension[] extensions = 	ELProjectSetExtension.getInstances();
			if(extensions.length > 0){
				projectSet = extensions[0].getProjectSet();
				if(projectSet != null)
					projectSet.init(file.getProject());
			}
		}

		protected void outOfSynch(IProject project){
			status.addFatalError(NLS.bind(JsfUIMessages.RENAME_METHOD_PARTICIPANT_OUT_OF_SYNC_PROJECT, project.getFullPath().toString()));
		}

		@Override
		protected void match(IFile file, int offset, int length) {
			if(isFileReadOnly(file)){
				status.addFatalError(NLS.bind(JsfUIMessages.RENAME_METHOD_PARTICIPANT_ERROR_READ_ONLY_FILE, file.getFullPath().toString()));
			}else
				change(file, offset, length, newName);
		}

		protected IProject[] getProjects(){
			if(projectSet != null){
				return projectSet.getLinkedProjects();
			}
			return new IProject[]{baseFile.getProject()};
		}
		
		protected IContainer getViewFolder(IProject project){
			if(projectSet != null){
				return projectSet.getViewFolder(project);
			}
			
			return super.getViewFolder(project);
		}
	}

	public void addElement(Object element, RefactoringArguments arguments) {
		otherElements.add(element);
	}

}
