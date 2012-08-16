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

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.text.edits.MultiTextEdit;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IClassBean;

public class DeleteAllInjectedConstructorsProcessor extends CDIRefactoringProcessor {
	private IMethod method;
	
	public DeleteAllInjectedConstructorsProcessor(IFile file, IMethod method, String label){
		super(file, label);
		this.method = method;
	}
	
	private void changeConstructors(IClassBean bean) throws JavaModelException {
		Collection<IBeanMethod> constructors = bean.getBeanConstructors();
		if(constructors.size()>1) {
			ICompilationUnit original = constructors.iterator().next().getMethod().getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			for (IBeanMethod constructor : constructors) {
				if(!constructor.getMethod().isSimilar(method)){
					CDIMarkerResolutionUtils.deleteAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME, compilationUnit, constructor.getMethod(), (MultiTextEdit)change.getEdit());
				}
			}
			compilationUnit.discardWorkingCopy();
		}
	}

	
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		
		createRootChange();
		
		if(bean != null)
			changeConstructors(bean);
		
		return status;
	}

}
