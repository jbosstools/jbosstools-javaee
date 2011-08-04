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
package org.jboss.tools.cdi.internal.core.refactoring;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.model.project.ProjectHome;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.jst.web.kb.refactoring.RefactorSearcher;

/**
 * @author Daniel Azarov
 */
public abstract class CDIRenameProcessor extends RenameProcessor {
	protected static final RefactoringParticipant[] EMPTY_REF_PARTICIPANT = new  RefactoringParticipant[0];	
	
	protected RefactoringStatus status;
	
	protected CompositeChange rootChange;
	protected TextFileChange lastChange;
	protected IFile declarationFile=null;
	
	private String newName;
	private String oldName;
	
	private CDISearcher searcher = null;
	protected IBean bean;
	
	protected CDISearcher getSearcher(){
		if(searcher == null){
			searcher = new CDISearcher(declarationFile, getOldName());
		}
		return searcher;
	}
	
	public void setNewName(String newName){
		this.newName = newName;
	}
	
	protected String getNewName(){
		return newName;
	}
	
	protected void setOldName(String oldName){
		this.oldName = oldName;
	}
	
	public String getOldName(){
		return oldName;
	}
	
	// lets collect all changes for the same files in one MultiTextEdit
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
	
	protected void findDeclarations(IBean bean) throws CoreException{
		changeDeclarations(bean);
	}
	
	
	private void changeDeclarations(IBean bean) throws CoreException{
		declarationFile = (IFile)bean.getResource();
		
		if(declarationFile == null){
			status.addFatalError(CDICoreMessages.CDI_RENAME_PROCESSOR_BEAN_HAS_NO_FILE);
			return;
		}
		
		//1. Get @Named declared directly, not in stereotype.
		ITextSourceReference nameLocation = bean.getNameLocation(false);
		//2. Get stereotype declaration declaring @Named, if @Named is not declared directly.
		ITextSourceReference stereotypeLocation = nameLocation != null ? null : bean.getNameLocation(true);
		
		if(nameLocation == null && stereotypeLocation == null) {
			status.addFatalError(CDICoreMessages.CDI_RENAME_PROCESSOR_BEAN_HAS_NO_NAME_LOCATION);
			return;
		}
		
		String newText = "@Named(\""+getNewName()+"\")"; //$NON-NLS-1$ //$NON-NLS-2$
		if(nameLocation != null) {
			change(declarationFile, nameLocation.getStartPosition(), nameLocation.getLength(), newText);
		} else if(stereotypeLocation != null) {
			change(declarationFile, stereotypeLocation.getStartPosition() + stereotypeLocation.getLength(), 0, " " + newText);
		}
	}
	
	protected void renameBean(IProgressMonitor pm, IBean bean)throws CoreException{
		pm.beginTask("", 3);
		
		clearChanges();
		
		this.bean = bean;
		
		findDeclarations(bean);
		
		if(status.hasFatalError())
			return;
		
		pm.worked(1);
		
		getSearcher().findELReferences();
		
		pm.done();
	}
	
	ArrayList<String> keys = new ArrayList<String>();
	
	private void clearChanges(){
		keys.clear();
	}
	
	private void change(IFile file, int offset, int length, String text){
		//System.out.println("change file - "+file.getFullPath()+" offset - "+offset+" len - "+length+" text"+text);
		String key = file.getFullPath().toString()+" "+offset;
		if(!keys.contains(key)){
			TextFileChange change = getChange(file);
			TextEdit edit = new ReplaceEdit(offset, length, text);
			change.addEdit(edit);
			keys.add(key);
		}
	}
	
	class CDISearcher extends RefactorSearcher{
		public CDISearcher(IFile declarationFile, String oldName){
			super(declarationFile, oldName, bean.getBeanClass());
		}

		@Override
		protected void outOfSynch(IProject project) {
			status.addFatalError(NLS.bind(CDICoreMessages.CDI_RENAME_PROCESSOR_OUT_OF_SYNC_PROJECT, project.getFullPath().toString()));
		}

		@Override
		protected void match(IFile file, int offset, int length) {
			if(isFileReadOnly(file)){
				status.addFatalError(NLS.bind(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_READ_ONLY_FILE, file.getFullPath().toString()));
			}else
				change(file, offset, length, newName);
		}
		
		protected ELInvocationExpression findComponentReference(ELInvocationExpression invocationExpression){
			if(bean != null)
				return invocationExpression;
			
			ELInvocationExpression invExp = invocationExpression;
			while(invExp != null){
				if(invExp instanceof ELPropertyInvocation){
					if(((ELPropertyInvocation)invExp).getQualifiedName() != null && ((ELPropertyInvocation)invExp).getQualifiedName().equals(propertyName))
						return invExp;
					else
						invExp = invExp.getLeft();
					
				}else{
					invExp = invExp.getLeft();
				}
			}
			return null;
		}

		@Override
		protected IProject[] getProjects() {
			return new IProject[]{baseFile.getProject()};
		}
		
		@Override
		protected IContainer getViewFolder(IProject project) {
			IPath path = ProjectHome.getFirstWebContentPath(baseFile.getProject());
			
			if(path != null)
				return project.getFolder(path.removeFirstSegments(1));
			
			return null;
		}
	}
}