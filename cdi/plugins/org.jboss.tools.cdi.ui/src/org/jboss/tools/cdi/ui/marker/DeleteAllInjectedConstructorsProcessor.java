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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IClassBean;

public class DeleteAllInjectedConstructorsProcessor extends MarkerResolutionRefactoringProcessor {

	
	public DeleteAllInjectedConstructorsProcessor(IFile file, IMethod method, String label){
		super(file, method, label);
	}
	
	private void changeConstructors(IClassBean bean) {
		Set<IBeanMethod> constructors = bean.getBeanConstructors();
		if(constructors.size()>1) {
			Set<IAnnotationDeclaration> injects = new HashSet<IAnnotationDeclaration>();
			for (IBeanMethod constructor : constructors) {
				if(!constructor.getMethod().isSimilar(method)){
					IAnnotationDeclaration inject = constructor.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
					if(inject!=null) {
						injects.add(inject);
					}
				}
			}
			for (IAnnotationDeclaration inject : injects) {
				TextEdit edit = new ReplaceEdit(inject.getStartPosition(), inject.getLength(), "");
				change.addEdit(edit);
			}
		}
	}

	
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		
		rootChange = new CompositeChange(label);
		change = new TextFileChange(file.getName(), file);
		change.setSaveMode(TextFileChange.LEAVE_DIRTY);
		MultiTextEdit root = new MultiTextEdit();
		change.setEdit(root);
		rootChange.add(change);
		
		if(bean != null)
			changeConstructors(bean);
		
		return status;
	}

}
