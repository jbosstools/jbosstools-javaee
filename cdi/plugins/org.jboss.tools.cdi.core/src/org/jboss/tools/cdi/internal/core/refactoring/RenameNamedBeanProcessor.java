 /*******************************************************************************
  * Copyright (c) 2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.cdi.internal.core.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * @author Daniel Azarov
 */
public class RenameNamedBeanProcessor extends CDIRenameProcessor {
	
	/**
	 * @param bean Renamed bean
	 */
	public RenameNamedBeanProcessor(IBean bean) {
		super(CDICoreMessages.RENAME_NAMED_BEAN_PROCESSOR_TITLE, bean);
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		status = new RefactoringStatus();
		if(bean != null){
			rootChange = new CompositeChange(CDICoreMessages.RENAME_NAMED_BEAN_PROCESSOR_TITLE);
			
			renameBean(pm);
		}
		return status;
	}

	private void renameBean(IProgressMonitor pm)throws CoreException{
		pm.beginTask("", 3);
		
		clearChanges();
		
		changeDeclarations();
		
		if(status.hasFatalError())
			return;
		
		pm.worked(1);
		
		getSearcher().findELReferences(pm);
		
		pm.done();
	}
	
	private void changeDeclarations() throws CoreException{
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

}