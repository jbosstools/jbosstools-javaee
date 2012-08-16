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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.internal.core.refactoring.DeleteOtherAnnotationsFromParametersProcessor;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.wizard.DeletePreviewWizard;
import org.jboss.tools.common.quickfix.IQuickFix;
import org.jboss.tools.common.refactoring.MarkerResolutionUtils;
import org.jboss.tools.common.refactoring.TestableResolutionWithRefactoringProcessor;

/**
 * @author Daniel Azarov
 */
public class DeleteAllOtherAnnotationsFromParametersMarkerResolution implements IQuickFix, TestableResolutionWithRefactoringProcessor {
	private String label;
	private String annotationName;
	private ILocalVariable parameter;
	private IFile file;
	private String description;
	
	public DeleteAllOtherAnnotationsFromParametersMarkerResolution(String annotationName, ILocalVariable parameter, IFile file){
		String shortName = CDIMarkerResolutionUtils.AT + CDIMarkerResolutionUtils.getShortName(annotationName);
		this.label = MessageFormat.format(CDIUIMessages.DELETE_ALL_OTHER_ANNOTATIONS_MARKER_RESOLUTION_TITLE, new Object[]{shortName, parameter.getElementName()});
		this.annotationName = annotationName;
		this.parameter = parameter;
		this.file = file;
		description = getPreview();
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	private String getPreview(){
		RefactoringProcessor processor = getRefactoringProcessor();
		RefactoringStatus status;
		try {
			status = processor.checkInitialConditions(new NullProgressMonitor());
		
		
			if(status.getEntryMatchingSeverity(RefactoringStatus.FATAL) != null){
				return label;
			}

			status = processor.checkFinalConditions(new NullProgressMonitor(), null);

			if(status.getEntryMatchingSeverity(RefactoringStatus.FATAL) != null){
				return label;
			}

			CompositeChange rootChange = (CompositeChange)processor.createChange(new NullProgressMonitor());
		
			return MarkerResolutionUtils.getPreview(rootChange);
		} catch (OperationCanceledException e) {
			CDIUIPlugin.getDefault().logError(e);
		} catch (CoreException e) {
			CDIUIPlugin.getDefault().logError(e);
		}
		return label;
	}
	
	private void internal_run(){
		DeleteOtherAnnotationsFromParametersProcessor processor = new DeleteOtherAnnotationsFromParametersProcessor(file, annotationName, parameter, label);
		ProcessorBasedRefactoring refactoring = new ProcessorBasedRefactoring(processor);
		DeletePreviewWizard wizard = new DeletePreviewWizard(refactoring);
		wizard.showWizard();
	}
	
	@Override
	public void run(IMarker marker) {
		internal_run();
	}
	
	@Override
	public RefactoringProcessor getRefactoringProcessor(){
		return new DeleteOtherAnnotationsFromParametersProcessor(file, annotationName, parameter, label);
	}
	
	@Override
	public String getDescription() {
		if(description == null) {
			System.out.println("compute description " + this);
			description = getPreview();
			System.out.println(description.length());
		}
		return description;
	}

	@Override
	public Image getImage() {
		return CDIImages.getImage(CDIImages.QUICKFIX_REMOVE);
	}

	@Override
	public int getRelevance() {
		return 100;
	}

	@Override
	public void apply(IDocument document) {
		internal_run();
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return description;
	}

	@Override
	public String getDisplayString() {
		return label;
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}
}