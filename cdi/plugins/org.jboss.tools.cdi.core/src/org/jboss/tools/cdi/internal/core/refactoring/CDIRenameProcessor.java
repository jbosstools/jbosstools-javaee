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
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.model.project.ProjectHome;
import org.jboss.tools.common.refactoring.BaseFileChange;
import org.jboss.tools.jst.web.kb.refactoring.RefactorSearcher;

/**
 * @author Daniel Azarov
 */
public abstract class CDIRenameProcessor extends AbstractCDIProcessor {
	protected BaseFileChange lastChange;
	protected IFile declarationFile=null;
	
	private String newName;
	private String oldName;
	
	private CDISearcher searcher = null;
	protected IBean bean;

	public CDIRenameProcessor(String label, IBean bean) {
		super(label);
		this.bean = bean;
		setOldName(bean.getName());
	}
	
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus result = new RefactoringStatus();
		if(bean==null) {
			result.addFatalError(CDICoreMessages.RENAME_NAMED_BEAN_PROCESSOR_ERROR);
		}
		return result;
	}

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
	protected BaseFileChange getChange(IFile file){
		if(lastChange != null && lastChange.getFile().equals(file))
			return lastChange;
		
		for(int i=0; i < rootChange.getChildren().length; i++){
			BaseFileChange change = (BaseFileChange)rootChange.getChildren()[i];
			if(change.getFile().equals(file)){
				lastChange = change;
				return lastChange;
			}
		}
		lastChange = new BaseFileChange(file);
		
		MultiTextEdit root = new MultiTextEdit();
		lastChange.setEdit(root);
		rootChange.add(lastChange);
		
		return lastChange;
	}
	
	
	
	ArrayList<String> keys = new ArrayList<String>();
	
	protected void clearChanges(){
		keys.clear();
	}
	
	protected void change(IFile file, int offset, int length, String text){
		String key = file.getFullPath().toString()+" "+offset;
		if(!keys.contains(key)){
			BaseFileChange change = getChange(file);
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
			status.addFatalError(NLS.bind(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_OUT_OF_SYNC_PROJECT, project.getFullPath().toString()));
		}

		@Override
		protected void match(IFile file, int offset, int length) {
			if(isFileReadOnly(file)){
				status.addFatalError(NLS.bind(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_READ_ONLY_FILE, file.getFullPath().toString()));
			}else
				change(file, offset, length, newName);
		}
		
		@Override
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
	}
	
	@Override
	public boolean isApplicable() throws CoreException {
		return bean!=null;
	}
	
	@Override
	public Object[] getElements() {
		return new IBean[]{bean};
	}
}