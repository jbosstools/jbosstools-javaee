/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation.jsf2.action;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.jboss.tools.jsf.JSFModelPlugin;

@SuppressWarnings("restriction")
public abstract class JSF2AbstractProposal implements IMarkerResolution,
		ICompletionProposal {

	protected JSF2AbstractProposal() {

	}

	protected JSF2AbstractProposal(IResource resource) {
		validateResource = resource;
	}

	protected IResource validateResource = null;

	public String getLabel() {
		return getDisplayString();
	}

	public final void run(IMarker marker) {
		try {
			runWithMarker(marker);
			Job validateJob = new Job("JSF 2 Components Validator") { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						if (validateResource != null) {
							ValidationFramework.getDefault().validate(
									new IProject[] { validateResource
											.getProject() }, false, false,
									new NullProgressMonitor());
						}
					} catch (CoreException e) {
						return new Status(IStatus.CANCEL,
								JSFModelPlugin.PLUGIN_ID, "Cancel"); //$NON-NLS-1$
					}
					return IValidatorJob.OK_STATUS;
				}
			};
			validateJob.schedule(500);
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
	}

	public void apply(IDocument document) {
		run(null);
	}

	public String getAdditionalProposalInfo() {
		return null;
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}

	public Point getSelection(IDocument document) {
		return null;
	}

	protected abstract void runWithMarker(IMarker marker) throws CoreException;
}
