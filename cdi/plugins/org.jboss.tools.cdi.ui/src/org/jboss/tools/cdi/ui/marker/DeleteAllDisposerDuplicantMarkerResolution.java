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
import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.internal.Workbench;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * @author Daniel Azarov
 */
public class DeleteAllDisposerDuplicantMarkerResolution implements IMarkerResolution2 {
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
		DeleteAllDisposerAnnotationsProcessor processor = new DeleteAllDisposerAnnotationsProcessor(file, method);
		ProcessorBasedRefactoring refactoring = new ProcessorBasedRefactoring(processor);
		DeletePreviewWizard wizard = new DeletePreviewWizard(refactoring, RefactoringWizard.WIZARD_BASED_USER_INTERFACE);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
		Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
		try {
			String titleForFailedChecks = CDIUIMessages.CDI_REFACTOR_CONTRIBUTOR_ERROR;
			op.run(shell, titleForFailedChecks);
		} catch (final InterruptedException irex) {
			// operation was canceled
		}

	}
	
	public String getDescription() {
		return null;
	}

	public Image getImage() {
		return null;
	}

}
