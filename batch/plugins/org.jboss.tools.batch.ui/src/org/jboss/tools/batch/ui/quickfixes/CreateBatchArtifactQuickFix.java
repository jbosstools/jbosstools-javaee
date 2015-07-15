/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.quickfixes;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.ui.BatchUIPlugin;
import org.jboss.tools.batch.ui.JobImages;
import org.jboss.tools.batch.ui.internal.wizard.NewBatchArtifactDialog;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.quickfix.IQuickFix;
import org.jboss.tools.common.refactoring.TestableResolutionWithDialog;
import org.jboss.tools.common.util.BeanUtil;

public class CreateBatchArtifactQuickFix implements IQuickFix, TestableResolutionWithDialog {
	
	private IFile file;
	private String artifactName;
	private BatchArtifactType type;
	private String label;
	
	public CreateBatchArtifactQuickFix(IFile file, String artifactName, BatchArtifactType type, String label){
		this.file = file;
		this.artifactName = artifactName;
		this.type = type;
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker){
		IBatchProject batchProject = BatchCorePlugin.getBatchProject(file.getProject(), true);
		if(batchProject == null) {
			return;
		}
		IResource[] resource = EclipseUtil.getJavaSourceRoots(batchProject.getProject());
		if(resource.length == 0) {
			return;
		}
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		NewBatchArtifactDialog dialog = new NewBatchArtifactDialog(shell);
		
		
		StructuredSelection selection = new StructuredSelection(resource[0]);
		dialog.init(BatchUIPlugin.getDefault().getWorkbench(), selection);
		
		dialog.open(batchProject, type, false, BeanUtil.getClassName(artifactName));
	}

	@Override
	public void runForTest(IMarker marker) {
		run(marker);
	}
	
	@Override
	public String getDescription() {
		return getLabel();
	}

	@Override
	public Image getImage() {
		return JobImages.getImage(JobImages.QUICKFIX_EDIT_IMAGE);
	}

	@Override
	public int getRelevance() {
		return 100;
	}

	@Override
	public void apply(IDocument document) {
		run(null);
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return getLabel();
	}

	@Override
	public String getDisplayString() {
		return getLabel();
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

}
