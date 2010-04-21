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

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ComponentParams;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ComponentsUtil;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.internal.XmlContextImpl;
import org.w3c.dom.Element;

/**
 * 
 * @author yzhishko
 *
 */

@SuppressWarnings("restriction")
public class JSF2Validator {

	public static String JSF2_PROBLEM_ID = JSFModelPlugin.PLUGIN_ID
			+ ".jsf2problemmarker"; //$NON-NLS-1$

	private JSF2ValidationInfo validationInfo;
	private IFile file;

	private static JSF2Validator instance = new JSF2Validator();

	public ValidationReport validate(IFile file, String uri) {
		validationInfo = new JSF2ValidationInfo(uri);
		this.file = file;
		validate(file);
		return validationInfo;
	}

	private JSF2Validator() {

	}

	public static JSF2Validator getInstance() {
		return instance;
	}

	private void validate(IFile file) {
		ELContext elContext = PageContextFactory.createPageContext(file);
		if (elContext instanceof IPageContext) {
			validateAsDOM(file);
		} else if (elContext instanceof XmlContextImpl) {
			if ("xhtml".equals(file.getFileExtension())) { //$NON-NLS-1$
				validateAsDOM(file);
			}
		}
	}

	private void validateAsDOM(IFile file) {
		IDOMDocument document = getDocumentForFile(file);
		if (document != null) {
			Map<String, List<Element>> jsf2Components = JSF2ComponentsUtil
					.findJSF2CompositeComponents(document);
			createMarkers(jsf2Components);
		}
	}

	private void createMarkers(Map<String, List<Element>> jsf2Components) {
		Set<String> elementsNameSet = new HashSet<String>(0);
		Collection<List<Element>> elementsCollection = jsf2Components.values();
		for (List<Element> list : elementsCollection) {
			if (list != null) {
				for (Element element : list) {
					if (!elementsNameSet.contains(element.getNodeName())) {
						IDOMElement domElement = (IDOMElement) element;
						createMarkerForElement(domElement);
						elementsNameSet.add(element.getNodeName());
					}
				}
			}
		}
	}

	private void createMarkerForElement(IDOMElement domElement) {
		if (!JSF2ResourceUtil.isJSF2CompositeComponentExists(file.getProject(),
				domElement)) {
			int line = ((IDOMDocument) domElement.getOwnerDocument())
					.getStructuredDocument().getLineOfOffset(
							domElement.getStartOffset());
			validationInfo.addWarning(createWarningMessage(domElement),
					line + 1, 0, validationInfo.getFileURI(), null,
					new Object[] { JSF2ComponentParams.create(domElement) });
		}
	}

	private String createWarningMessage(IDOMElement element) {
		StringBuilder builder = new StringBuilder("Composite component "); //$NON-NLS-1$
		String nodeName = element.getNodeName();
		if (nodeName.lastIndexOf(':') != -1) {
			nodeName = nodeName.substring(nodeName.lastIndexOf(':') + 1);
		}
		builder.append("\"" + nodeName + "\" "); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append("was not found in a project resources folder"); //$NON-NLS-1$
		return builder.toString();
	}

	private IDOMDocument getDocumentForFile(IFile file) {
		IDOMDocument document = null;
		IModelManager manager = StructuredModelManager.getModelManager();
		if (manager == null) {
			return document;
		}
		IStructuredModel model = null;
		try {
			model = manager.getModelForRead(file);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				document = domModel.getDocument();
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		} catch (IOException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return document;
	}

}
