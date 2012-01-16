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
package org.jboss.tools.cdi.internal.core.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.text.edits.MultiTextEdit;

public class DeleteOtherAnnotationsFromParametersProcessor extends CDIRefactoringProcessor {
	private ILocalVariable parameter;
	private String annotationName;
	
	public DeleteOtherAnnotationsFromParametersProcessor(IFile file, String annotationName, ILocalVariable parameter, String label){
		super(file, label);
		this.parameter = parameter;
		this.annotationName = annotationName;
	}
	
	private void change() throws JavaModelException {
		if(parameter.getParent() instanceof IMethod){
			IMethod method = (IMethod)parameter.getParent();
			ICompilationUnit original = method.getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			for (ILocalVariable param : method.getParameters()) {
				if(!param.getTypeSignature().equals(parameter.getTypeSignature()) || !param.getElementName().equals(parameter.getElementName())){
					CDIMarkerResolutionUtils.deleteAnnotation(annotationName, compilationUnit, param, (MultiTextEdit)change.getEdit());
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

		change();
		
		return status;
	}

}
