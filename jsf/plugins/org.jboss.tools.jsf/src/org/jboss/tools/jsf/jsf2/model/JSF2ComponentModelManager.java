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

package org.jboss.tools.jsf.jsf2.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.html.core.internal.encoding.HTMLModelLoader;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.JobSafeStructuredDocument;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.DocumentNodeFormatter;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.util.JSF2ComponentUtil;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
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
			final IFile componentFileContatiner, final String[] attrNames) {
		IFile file = updateFileContent(new EditableDOMFile() {

			@Override
			public IFile getFile() {
				return componentFileContatiner;
			}

			@Override
			protected void edit(IDOMModel model) throws CoreException,
					IOException {
				IDOMDocument document = model.getDocument();
				updateJSF2CompositeComponent(document, attrNames);
				IStructuredDocument structuredDocument = document
						.getStructuredDocument();
				String text = structuredDocument.getText();
				model.reload(new ByteArrayInputStream(text.getBytes()));
				model.save();
			}
		});
		return file;
	}

	public void renameCompositeComponents(IResource resource, String URI,
			String oldName, String newName) throws CoreException {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			renameCompositeComponentsInFile(file, URI, oldName, newName);
		} else if (resource instanceof IProject) {
			IResource[] children = ((IProject) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					renameCompositeComponents(children[i], URI, oldName,
							newName);
				}
			}
		} else if (resource instanceof IFolder) {
			IResource[] children = ((IFolder) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					renameCompositeComponents(children[i], URI, oldName,
							newName);
				}
			}
		}
	}

	private boolean isFileCorrect(IFile file) {
		if (file == null) {
			return false;
		}
		if (!"xhtml".equals(file.getFileExtension()) && !"jsp".equals(file.getFileExtension())) { //$NON-NLS-1$ //$NON-NLS-2$
			IContentType contentType = IDE.getContentType(file);
			if (contentType == null) {
				return false;
			}
			String id = contentType.getId();
			if (!"org.eclipse.jst.jsp.core.jspsource".equals(id) && !"org.eclipse.wst.html.core.htmlsource".equals(id)) { //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
		}
		return true;
	}

	private void renameCompositeComponentsInFile(final IFile file,
			final String URI, final String oldName, final String newName) {
		if (!isFileCorrect(file)) {
			return;
		}
		updateFileContent(new EditableDOMFile() {

			@Override
			public IFile getFile() {
				return file;
			}

			@Override
			protected void edit(IDOMModel model) throws CoreException,
					IOException {
				IDOMDocument document = model.getDocument();
				Map<String, List<Element>> compositeComponentsMap = JSF2ComponentUtil
						.findCompositeComponents(document);
				List<Element> compositeComponents = compositeComponentsMap
						.get(URI);
				if (compositeComponents != null) {
					for (Element element : compositeComponents) {
						if (oldName.equals(element.getLocalName())) {
							renameElement((IDOMElement) element, oldName,
									newName);
						}
					}
					model.save();
					ValidationFramework.getDefault().validate(file,
							new NullProgressMonitor());
				}
			}
		});
	}

	public void renameURIs(IResource resource, Map<String, String> urisMap)
			throws CoreException {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			renameURIsInFile(file, urisMap);
		} else if (resource instanceof IProject) {
			IResource[] children = ((IProject) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					renameURIs(children[i], urisMap);
				}
			}
		} else if (resource instanceof IFolder) {
			IResource[] children = ((IFolder) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					renameURIs(children[i], urisMap);
				}
			}
		}
	}

	private void renameURIsInFile(final IFile file,
			final Map<String, String> urisMap) {
		if (!isFileCorrect(file)) {
			return;
		}
		updateFileContent(new EditableDOMFile() {

			@Override
			public IFile getFile() {
				return file;
			}

			@Override
			protected void edit(IDOMModel model) throws CoreException,
					IOException {
				IDOMDocument document = model.getDocument();
				IDOMAttr[] uriAttrs = JSF2ComponentUtil
						.findURIContainers(document);
				for (int i = 0; i < uriAttrs.length; i++) {
					if (urisMap.containsKey(uriAttrs[i].getValue())) {
						renameURIAttr(uriAttrs[i], urisMap.get(uriAttrs[i]
								.getValue()));
					}
				}
				model.save();
				ValidationFramework.getDefault().validate(file,
						new NullProgressMonitor());
			}

		});
	}

	private void renameURIAttr(IDOMAttr idomAttr, final String replaceValue) {
		int startOffset = idomAttr.getStartOffset();
		String attrValue = idomAttr.getValue();
		IStructuredDocument document = idomAttr.getStructuredDocument();
		String value = document.getText().substring(idomAttr.getStartOffset());
		value = value.substring(0, value.indexOf(attrValue)
				+ idomAttr.getValue().length());
		try {
			document.replace(startOffset + value.indexOf(attrValue), attrValue
					.length(), replaceValue);
		} catch (BadLocationException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		idomAttr.getName();
	}

	private void renameElement(final IDOMElement element, final String oldName,
			final String newName) {
		String sourceString = element.getSource();
		IStructuredDocument structuredDocument = element
				.getStructuredDocument();
		int startOffset = element.getStartOffset();
		int endStartOffset = element.getEndStartOffset();
		int endOffset = element.getEndOffset();
		try {
			if (endOffset != endStartOffset) {
				structuredDocument.replace(sourceString.lastIndexOf(oldName)
						+ startOffset, oldName.length(), newName);
			}
			structuredDocument.replace(element.getStartOffset()
					+ sourceString.indexOf(oldName), oldName.length(), newName);
		} catch (BadLocationException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
	}

	private void updateJSF2CompositeComponent(IDOMDocument componentDoc,
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
				String nodeName = impl.getLocalName();
				if ("interface".equals(nodeName)) { //$NON-NLS-1$
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
		Set<String> existInerfaceAttrs = getInterfaceAttrs(element);
		if (prefix != null && !"".equals(prefix)) { //$NON-NLS-1$
			for (int i = 0; i < attrNames.length; i++) {
				if (!existInerfaceAttrs.contains(attrNames[i])) {
					Element attrEl = document.createElementNS(
							JSF2ResourceUtil.JSF2_URI_PREFIX, prefix
									+ ":attribute"); //$NON-NLS-1$
					attrEl.setAttribute("name", attrNames[i]); //$NON-NLS-1$
					element.appendChild(attrEl);
				}
			}
		} else {
			for (int i = 0; i < attrNames.length; i++) {
				if (!existInerfaceAttrs.contains(attrNames[i])) {
					Element attrEl = document.createElementNS(
							JSF2ResourceUtil.JSF2_URI_PREFIX, "attribute"); //$NON-NLS-1$
					attrEl.setAttribute("name", attrNames[i]); //$NON-NLS-1$
					element.appendChild(attrEl);
				}
			}
		}
		DocumentNodeFormatter formatter = new DocumentNodeFormatter();
		formatter.format(document);
	}

	public IFile revalidateCompositeComponentFile(IFile file) {
		IDOMDocument document = getReadableDOMDocument(file);
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
		IDOMElement[] interfaceElement = new IDOMElement[1];
		findInterfaceComponent(element, interfaceElement);
		return interfaceElement[0];
	}

	public static IDOMDocument getReadableDOMDocument(IFile file) {
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

	public static IDOMDocument getReadableDOMDocument(JarEntryFile file) {
		IDOMDocument document = null;
		IStructuredModel model = null;
		InputStream inputStream;
		try {
			inputStream = file.getContents();
			if (inputStream != null) {
				StringBuffer buffer = new StringBuffer(""); //$NON-NLS-1$
				Scanner in = new Scanner(inputStream);
				while (in.hasNextLine()) {
					buffer.append(in.nextLine());
				}
				model = new HTMLModelLoader().newModel();
				model.setStructuredDocument(new JobSafeStructuredDocument(
						new XMLSourceParser()));
				model.getStructuredDocument().set(buffer.toString());
				if (model instanceof IDOMModel) {
					document = ((IDOMModel) model).getDocument();
				}
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		} finally {
			model = null;
		}
		return document;
	}

	public static IDOMDocument getReadableDOMDocument(IDocument textDocument) {
		IDOMDocument document = null;
		if (!(textDocument instanceof IStructuredDocument)) {
			return document;
		}
		IModelManager manager = StructuredModelManager.getModelManager();
		if (manager == null) {
			return document;
		}
		IStructuredModel model = null;
		try {
			model = manager.getModelForRead((IStructuredDocument) textDocument);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				document = domModel.getDocument();
			}
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return document;
	}

	public Set<String> getInterfaceAttrs(IDOMElement interfaceElement) {
		Set<String> interfaceAttrs = new HashSet<String>(0);
		if (interfaceElement != null) {
			String prefix = interfaceElement.getPrefix();
			String nodeName = "attribute"; //$NON-NLS-1$
			if (prefix != null && !"".equals(prefix)) { //$NON-NLS-1$
				nodeName = prefix + ":" + nodeName; //$NON-NLS-1$
			}
			NodeList attrsElements = interfaceElement
					.getElementsByTagName(nodeName);
			if (attrsElements != null) {
				for (int i = 0; i < attrsElements.getLength(); i++) {
					Node el = attrsElements.item(i);
					if (el instanceof IDOMElement) {
						IDOMElement element = (IDOMElement) el;
						String attrvalue = element.getAttribute("name"); //$NON-NLS-1$
						if (attrvalue != null && !"".equals(attrvalue)) { //$NON-NLS-1$
							interfaceAttrs.add(attrvalue);
						}
					}
				}
			}
		}
		return interfaceAttrs;
	}

	private abstract class EditableDOMFile {

		public EditableDOMFile() {
		}

		public IFile editFile() {
			IFile file = getFile();
			if (file == null) {
				return file;
			}
			IModelManager manager = StructuredModelManager.getModelManager();
			if (manager == null) {
				return file;
			}
			IStructuredModel model = null;
			try {
				model = manager.getModelForEdit(file);
				if (model instanceof IDOMModel) {
					IDOMModel domModel = (IDOMModel) model;
					edit(domModel);
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
			return file;
		};

		protected abstract void edit(IDOMModel model) throws CoreException,
				IOException;

		public abstract IFile getFile();

	}

	private IFile updateFileContent(EditableDOMFile domFile) {
		return domFile.editFile();
	}

}
