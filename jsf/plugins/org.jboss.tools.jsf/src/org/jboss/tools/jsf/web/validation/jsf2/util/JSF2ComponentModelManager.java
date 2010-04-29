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

package org.jboss.tools.jsf.web.validation.jsf2.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.DocumentNodeFormatter;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2ComponentModelManager {

	private static JSF2ComponentModelManager instance = new JSF2ComponentModelManager();

	private JSF2ComponentModelManager() {

	}

	public static JSF2ComponentModelManager getManager() {
		return instance;
	}

	public IFile updateJSF2CompositeComponentFile(
			IFile componentFileContatiner, String[] attrNames) {
		IDOMDocument document = null;
		IModelManager manager = StructuredModelManager.getModelManager();
		if (manager == null) {
			return componentFileContatiner;
		}
		IStructuredModel model = null;
		try {
			model = manager.getModelForEdit(componentFileContatiner);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				model.reload(componentFileContatiner.getContents());
				document = domModel.getDocument();
				updateJSF2CompositeComponent(document, attrNames);
				try {
					componentFileContatiner.setContents(
							new ByteArrayInputStream(document
									.getStructuredDocument().getText()
									.getBytes()), true, false,
							new NullProgressMonitor());
				} catch (CoreException e) {
					JSFModelPlugin.getPluginLog().logError(e);
				}
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		} catch (IOException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		} finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
		return componentFileContatiner;
	}

	public void updateJSF2CompositeComponent(IDOMDocument componentDoc,
			String[] attrNames) {
		IDOMElement[] interfaceElement = new IDOMElement[1];
		findInterfaceComponent(componentDoc, interfaceElement);
		createCompositeCompInterface(interfaceElement[0], attrNames);
	}

	private void findInterfaceComponent(Node node,
			IDOMElement[] interfaceElement) {
		if (node instanceof IDOMDocument) {
			IDOMDocument document = (IDOMDocument) node;
			findInterfaceComponent(document.getDocumentElement(),
					interfaceElement);
		}
		if (node instanceof ElementImpl) {
			ElementImpl impl = (ElementImpl) node;
			String nameSpace = impl.getNamespaceURI();
			if (JSF2ResourceUtil.JSF2_URI_PREFIX.equals(nameSpace)) {
				String nodeName = impl.getNodeName();
				String compName = nodeName.substring(
						nodeName.lastIndexOf(':') + 1).trim();
				if ("interface".equals(compName)) { //$NON-NLS-1$
					interfaceElement[0] = impl;
					return;
				}
			} else {
				NodeList nodeList = node.getChildNodes();
				if (nodeList != null) {
					for (int i = 0; i < nodeList.getLength(); i++) {
						findInterfaceComponent(nodeList.item(i),
								interfaceElement);
					}
				}
			}
		}
	}

	private void createCompositeCompInterface(IDOMElement element,
			String[] attrNames) {
		Document document = (Document) element.getOwnerDocument();
		String prefix = element.getPrefix();
		if (prefix != null && !"".equals(prefix)) { //$NON-NLS-1$
			for (int i = 0; i < attrNames.length; i++) {
				Element attrEl = document
						.createElementNS(JSF2ResourceUtil.JSF2_URI_PREFIX,
								prefix + ":attribute"); //$NON-NLS-1$
				attrEl.setAttribute("name", attrNames[i]); //$NON-NLS-1$
				element.appendChild(attrEl);
			}
		} else {
			for (int i = 0; i < attrNames.length; i++) {
				Element attrEl = document.createElementNS(
						JSF2ResourceUtil.JSF2_URI_PREFIX, "attribute"); //$NON-NLS-1$
				attrEl.setAttribute("name", attrNames[i]); //$NON-NLS-1$
				element.appendChild(attrEl);
			}
		}
		DocumentNodeFormatter formatter = new DocumentNodeFormatter();
		formatter.format(document);
	}

	public IFile revalidateCompositeComponentFile(IFile file) {
		IDOMDocument document = JSF2ComponentUtil
				.getReadableDocumentForFile(file);
		if (document == null) {
			return null;
		}
		IDOMElement interfaceElement = checkCompositeInterface(document);
		if (interfaceElement == null) {
			return null;
		}
		return file;
	}

	public IDOMElement checkCompositeInterface(IDOMDocument document) {
		if (document == null) {
			return null;
		}
		Element element = document.getDocumentElement();
		if (element == null) {
			return null;
		}
		if (!"html".equals(element.getNodeName())) { //$NON-NLS-1$
			return null;
		}
		ElementImpl elementImpl = (ElementImpl) element;
		if (!"http://www.w3.org/1999/xhtml".equals(elementImpl.getNamespaceURI())) { //$NON-NLS-1$
			return null;
		}
		NodeList children = element.getChildNodes();
		if (children == null) {
			return null;
		}
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element el = (Element) children.item(i);
				String nodeName = el.getNodeName();
				if (nodeName.indexOf(':') > -1) {
					nodeName = nodeName
							.substring(nodeName.lastIndexOf(":") + 1); //$NON-NLS-1$
				}
				if ("interface".equals(nodeName)) { //$NON-NLS-1$
					if (JSF2ResourceUtil.JSF2_URI_PREFIX
							.equals(((ElementImpl) el).getNamespaceURI())) {
						return (IDOMElement) el;
					}
				}
			}
		}
		return null;
	}

}
