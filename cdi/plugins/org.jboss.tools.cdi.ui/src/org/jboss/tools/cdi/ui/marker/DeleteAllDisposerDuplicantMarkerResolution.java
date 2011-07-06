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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.DeletePreviewWizard;

/**
 * @author Daniel Azarov
 */
public class DeleteAllDisposerDuplicantMarkerResolution implements IMarkerResolution2, TestableResolutionWithRefactoringProcessor {
	private String label;
	private IMethod method;
	private IFile file;
	
	public DeleteAllDisposerDuplicantMarkerResolution(IMethod method, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.DELETE_ALL_DISPOSER_DUPLICANT_MARKER_RESOLUTION_TITLE, new Object[]{method.getElementName()});
		this.method = method;
		this.file = file;
	}

	public String getLabel() {
		return label;
	}
	

	public void run(IMarker marker) {
		DeleteAllDisposerAnnotationsProcessor processor = new DeleteAllDisposerAnnotationsProcessor(file, method, label);
		ProcessorBasedRefactoring refactoring = new ProcessorBasedRefactoring(processor);
		DeletePreviewWizard wizard = new DeletePreviewWizard(refactoring);
		//RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
		wizard.showWizard();
	}
	
	public RefactoringProcessor getRefactoringProcessor(){
		return new DeleteAllDisposerAnnotationsProcessor(file, method, label);
	}
	
	public String getDescription() {
		return label;
	}

	public Image getImage() {
		return null;
	}

}
