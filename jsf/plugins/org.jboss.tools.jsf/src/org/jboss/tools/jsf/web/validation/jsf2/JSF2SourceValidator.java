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

package org.jboss.tools.jsf.web.validation.jsf2;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.web.validation.IJSFValidationComponent;
import org.jboss.tools.jsf.web.validation.LocalizedMessage;

/**
 * Validates when we change smth in file
 * 
 * @author yzhishko
 * @author mareshkau
 * 
 */

@SuppressWarnings("restriction")
public class JSF2SourceValidator implements IValidator, ISourceValidator {

	private IDOMDocument document;
	private IFile validateFile;
	
	public void cleanup(IReporter reporter) {
	}

	public void validate(IValidationContext helper, IReporter reporter)
			throws ValidationException {
		if (document != null) {
				IResource resource = JSF2ResourceUtil.getValidatingResource(helper);
				if (resource instanceof IFile) {
					validateFile = (IFile) resource;
					reportProblems(reporter,
							JSF2XMLValidator.getValidationComponents(document,
									(IFile) resource),resource);
				}
			}
	}

	public void connect(IDocument document) {
		this.document = JSF2ComponentModelManager
				.getReadableDOMDocument(document);
	}

	public void disconnect(IDocument document) {
		document = null;
		validateFile = null;
	}

	public void validate(IRegion dirtyRegion, IValidationContext helper,
			IReporter reporter) {
	}

	private void reportProblems(IReporter reporter,
			IJSFValidationComponent[] validationComponents,IResource resource) {
		try {
			resource.deleteMarkers(JSF2XMLValidator.JSF2_PROBLEM_ID, false, IResource.DEPTH_INFINITE);
			for (int i = 0; i < validationComponents.length; i++) {
				Message locMessage = LocalizedMessage.createJSF2LocalizedMessage(validationComponents[i], validateFile);
				reporter.addMessage(this, locMessage);
					IMarker marker = resource.createMarker(JSF2XMLValidator.JSF2_PROBLEM_ID);
					marker.setAttributes(locMessage.getAttributes());
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		
	}
}