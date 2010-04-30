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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.IncrementalHelper;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSF2ValidationComponent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2SourceValidator implements IValidator, ISourceValidator {

	private IDOMDocument document;

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
				String filePath = helper.getURIs()[0];
				if (filePath == null) {
					return;
				}
				filePath = filePath.substring(filePath.indexOf('/') + 1);
				IResource resource = project.findMember(filePath
						.substring(filePath.indexOf('/') + 1));
				if (resource instanceof IFile) {
					reportProblems(reporter,
							JSF2XMLValidator.getValidationComponents(document,
									(IFile) resource));
				}
			}
		}
	}

	public void connect(IDocument document) {
		if (document instanceof IStructuredDocument) {
			IStructuredModel model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			try {
				if (model instanceof IDOMModel) {
					this.document = ((IDOMModel) model).getDocument();
				}
			} finally {
				if(model!=null) {
					model.releaseFromRead();
				}
			}
		}
	}

	public void disconnect(IDocument document) {
		document = null;
	}

	public void validate(IRegion dirtyRegion, IValidationContext helper,
			IReporter reporter) {
		if (document != null) {
			Element element = findNodeFromRegion(dirtyRegion);
			if (element == null) {
				return;
			}
			if (helper instanceof IncrementalHelper) {
				IncrementalHelper incrementalHelper = (IncrementalHelper) helper;
				IProject project = incrementalHelper.getProject();
				if (project == null) {
					return;
				}
				String filePath = helper.getURIs()[0];
				if (filePath == null) {
					return;
				}
				filePath = filePath.substring(filePath.indexOf('/') + 1);
				IResource resource = project.findMember(filePath
						.substring(filePath.indexOf('/') + 1));
				if (resource instanceof IFile) {
					reportProblems(reporter, JSF2XMLValidator
							.getValidationComponents(element, (IFile) resource));
				}
			}
		}
	}

	private void reportProblems(IReporter reporter,
			IJSF2ValidationComponent[] validationComponents) {
		for (int i = 0; i < validationComponents.length; i++) {
			reporter.addMessage(this, new LocalizedMessage(
					validationComponents[i]));
		}
	}

	private static class LocalizedMessage extends Message {

		private IJSF2ValidationComponent component;

		public LocalizedMessage(IJSF2ValidationComponent component) {
			this.component = component;
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

	private Element findNodeFromRegion(IRegion region) {
		int offset = region.getOffset();
		Element[] elements = new Element[1];
		findElementAttOffSet(offset, document.getDocumentElement(), elements);
		return elements[0];
	}

	private void findElementAttOffSet(int offSet, Node scanEl,
			Element[] returnEl) {
		if (scanEl instanceof IDOMElement) {
			if (isElementAttOffset(offSet, (IDOMElement) scanEl)) {
				returnEl[0] = (IDOMElement) scanEl;
				return;
			}
			NodeList childNodes = scanEl.getChildNodes();
			if (childNodes != null) {
				for (int i = 0; i < childNodes.getLength(); i++) {
					findElementAttOffSet(offSet, childNodes.item(i), returnEl);
				}
			}
		}
	}

	private boolean isElementAttOffset(int offSet, IDOMElement element) {
		return element.getStartOffset() == offSet;
	}

}
