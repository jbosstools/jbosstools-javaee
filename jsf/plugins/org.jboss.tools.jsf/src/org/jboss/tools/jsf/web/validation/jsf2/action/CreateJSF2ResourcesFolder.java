/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
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
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.jsf2.JSF2XMLValidator;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSF2ValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ResourceUtil;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class CreateJSF2ResourcesFolder implements IMarkerResolution,
		ICompletionProposal {

	private String componentPath = null;
	private IResource validateResource = null;

	public String getLabel() {
		return JSFUIMessages.Create_JSF_2_Resources_Folder;
	}

	public CreateJSF2ResourcesFolder() {
	}

	public CreateJSF2ResourcesFolder(IResource validateResource, String compPath) {
		this.componentPath = compPath;
		this.validateResource = validateResource;
	}

	public void run(final IMarker marker) {
		try {
			if (marker != null) {
				componentPath = (String) marker
						.getAttribute(IJSF2ValidationComponent.JSF2_URI_NAME_KEY);
				validateResource = marker.getResource();
			}
			JSF2ResourceUtil.createResourcesFolderByNameSpace(validateResource
					.getProject(), componentPath);
			validateResource.getProject().deleteMarkers(
					JSF2XMLValidator.JSF2_PROBLEM_ID, false, 1);
			Job validateJob = new Job("JSF 2 Components Validator") { //$NON-NLS-1$

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						ValidationFramework.getDefault()
								.validate(
										new IProject[] { validateResource
												.getProject() }, false, false,
										new NullProgressMonitor());
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

	public String getDisplayString() {
		return JSFUIMessages.Create_JSF_2_Resources_Folder;
	}

	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}

	public Point getSelection(IDocument document) {
		return null;
	}

}
