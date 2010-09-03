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

import java.util.Locale;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.IncrementalHelper;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSF2ValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2AttrTempComponent;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2CompositeTempComponent;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2URITempComponent;

/**
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
			if (helper instanceof IncrementalHelper) {
				IncrementalHelper incrementalHelper = (IncrementalHelper) helper;
				IProject project = incrementalHelper.getProject();
				if (project == null) {
					return;
				}
				String[] uris = helper.getURIs();
				if (uris == null || uris.length < 1) {
					return;
				}
				String filePath = uris[0];
				if (filePath == null) {
					return;
				}
				filePath = filePath.substring(filePath.indexOf('/') + 1);
				IResource resource = project.findMember(filePath
						.substring(filePath.indexOf('/') + 1));
				if (resource instanceof IFile) {
					validateFile = (IFile) resource;
					reportProblems(reporter,
							JSF2XMLValidator.getValidationComponents(document,
									(IFile) resource),resource);
				}
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
			IJSF2ValidationComponent[] validationComponents,IResource resource) {
		try {
			resource.deleteMarkers(JSF2XMLValidator.JSF2_PROBLEM_ID, false, IResource.DEPTH_INFINITE);
			for (int i = 0; i < validationComponents.length; i++) {
				Message locMessage = new LocalizedMessage(validationComponents[i], validateFile);
				reporter.addMessage(this, locMessage);
					IMarker marker = resource.createMarker(JSF2XMLValidator.JSF2_PROBLEM_ID);
					marker.setAttributes(locMessage.getAttributes());
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		
	}

	private static class LocalizedMessage extends Message {

		private IJSF2ValidationComponent component;

		public LocalizedMessage(IJSF2ValidationComponent component,
				IFile validateFile) {
			this.component = component;
			setAttribute("problemType", JSF2XMLValidator.JSF2_PROBLEM_ID); //$NON-NLS-1$
			setAttribute(IJSF2ValidationComponent.JSF2_TYPE_KEY, component
					.getType());
			setAttribute(
					"validateResourcePath", validateFile == null ? "" : validateFile.getFullPath().toString()); //$NON-NLS-1$//$NON-NLS-2$
			setAttribute(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY,
					component.getComponentResourceLocation());
			setAttribute(IMarker.LINE_NUMBER, getLineNumber());
			setAttribute(IMarker.SEVERITY, 1);
			setAttribute(ValidatorMessage.ValidationId, "org.jboss.tools.jsf.jsf2.source"); //$NON-NLS-1$
			if (component instanceof JSF2URITempComponent) {
				setAttribute(IJSF2ValidationComponent.JSF2_URI_NAME_KEY,
						((JSF2URITempComponent) component).getURI());
			} else if (component instanceof JSF2AttrTempComponent) {
				setAttribute(IJSF2ValidationComponent.JSF2_ATTR_NAME_KEY,
						((JSF2AttrTempComponent) component).getName());
				setAttribute(JSF2ResourceUtil.JSF2_COMPONENT_NAME, ((JSF2AttrTempComponent) component).getElementName());
			} else if (component instanceof JSF2CompositeTempComponent) {
				String[] attrNames = ((JSF2CompositeTempComponent) component)
						.getAttrNames();
				if (attrNames != null) {
					for (int i = 0; i < attrNames.length; i++) {
						setAttribute(
								IJSF2ValidationComponent.JSF2_ATTR_NAME_KEY
										+ String.valueOf(i), attrNames[i]);
					}
					setAttribute(JSF2ResourceUtil.JSF2_COMPONENT_NAME, ((JSF2CompositeTempComponent) component).getElement().getLocalName());
				}
			}
			setAttribute(IMarker.MESSAGE,getText());
		}

		@Override
		public int getLineNumber() {
			return component.getLine();
		}

		@Override
		public int getLength() {
			return component.getLength();
		}

		@Override
		public int getOffset() {
			return component.getStartOffSet();
		}

		@Override
		public String getText() {
			return component.getValidationMessage();
		}

		@Override
		public String getText(Locale locale) {
			return component.getValidationMessage();
		}

		@Override
		public String getText(Locale locale, ClassLoader classLoader) {
			return component.getValidationMessage();
		}

		@Override
		public String getText(ClassLoader classLoader) {
			return component.getValidationMessage();
		}

		@Override
		public int getSeverity() {
			return IMessage.NORMAL_SEVERITY;
		}

	}

}