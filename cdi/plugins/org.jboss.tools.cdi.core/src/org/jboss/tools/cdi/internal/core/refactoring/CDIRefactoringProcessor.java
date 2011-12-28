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
package org.jboss.tools.cdi.internal.core.refactoring;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.MultiTextEdit;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;

public abstract class CDIRefactoringProcessor extends AbstractCDIProcessor {
	protected IFile file;
	
	protected CDIFileChange change;
	protected IClassBean bean;

	public CDIRefactoringProcessor(IFile file, String label){
		super(label);
		this.file = file;
	}
	
	public CDIRefactoringProcessor(String label){
		super(label);
	}
	
	protected void createRootChange(){
		rootChange = new CompositeChange(getLabel());
		change = new CDIFileChange(file);
		
		MultiTextEdit root = new MultiTextEdit();
		change.setEdit(root);
		rootChange.add(change);
		//rootChange.markAsSynthetic();
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

	@Override
	public Object[] getElements() {
		return new Object[]{file};
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		status = new RefactoringStatus();
		
		if(isFileCorrect(file)){
			bean = findClassBean();
		}else
			status.addFatalError(CDICoreMessages.CDI_RENAME_PROCESSOR_ERROR_BEAN_NOT_FOUND);
		
		return status;
	}
}
