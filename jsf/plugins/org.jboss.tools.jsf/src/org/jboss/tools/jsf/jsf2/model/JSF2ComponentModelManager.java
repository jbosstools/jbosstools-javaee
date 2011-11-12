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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.html.core.internal.encoding.HTMLModelLoader;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.JobSafeStructuredDocument;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.DocumentNodeFormatter;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2ComponentModelManager {
	public final static String INTERFACE = "interface";
	public final static String ATTRIBUTE = "attribute";
	public final static String NAME = "name";
	public final static String XMLNS = "xmlns";
	
	public final static String COLON = ":";
	public final static String DEFAULT_COMPOSITE_PREFIX = "composite";
	public final static String XHTML_URI = "http://www.w3.org/1999/xhtml";
	public final static String HTML = "html";
	public final static String HTML_URI = "http://java.sun.com/jsf/html";
	public final static String HTML_PREFIX = "h";

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
				FileEditorInput input = new FileEditorInput(getFile());
				IDocumentProvider provider = DocumentProviderRegistry.getDefault().getDocumentProvider(input);
				
				provider.connect(input);
			
				IDocument document = provider.getDocument(input);
				
				updateJSF2CompositeComponent(model.getDocument(), attrNames);
				
				provider.aboutToChange(input);
				provider.saveDocument(new NullProgressMonitor(), input, document, true);
				provider.changed(input);
				provider.disconnect(input);
			}
		});
		return file;
	}

	private void updateJSF2CompositeComponent(IDOMDocument componentDoc,
			String[] attrNames) {
		Element interfaceElement = findInterfaceComponent(componentDoc);
		if(interfaceElement == null)
			interfaceElement = createCompositeInterface(componentDoc);
		createCompositeCompInterface(interfaceElement, attrNames);
	}

	private Element findInterfaceComponent(Node node) {
		if (node instanceof IDOMDocument) {
			IDOMDocument document = (IDOMDocument) node;
			Element element = findInterfaceComponent(document.getDocumentElement());
			if(element != null)
				return element;
		}
		if (node instanceof ElementImpl) {
			ElementImpl impl = (ElementImpl) node;
			String nameSpace = impl.getNamespaceURI();
			if (JSF2ResourceUtil.JSF2_URI_PREFIX.equals(nameSpace)) {
				String nodeName = impl.getLocalName();
				if (INTERFACE.equals(nodeName)) { //$NON-NLS-1$
					return impl;
				}
			} else {
				NodeList nodeList = node.getChildNodes();
				if (nodeList != null) {
					for (int i = 0; i < nodeList.getLength(); i++) {
						Element element = findInterfaceComponent(nodeList.item(i));
						if(element != null)
							return element;
					}
				}
			}
		}
		return null;
	}

	private void createCompositeCompInterface(Element element,
			String[] attrNames) {
		Document document = (Document) element.getOwnerDocument();
		String prefix = element.getPrefix();
		Set<String> existInerfaceAttrs = getInterfaceAttrs(element);
		if (prefix != null && !"".equals(prefix)) { //$NON-NLS-1$
			for (int i = 0; i < attrNames.length; i++) {
				if (!existInerfaceAttrs.contains(attrNames[i])) {
					Element attrEl = document.createElementNS(
							JSF2ResourceUtil.JSF2_URI_PREFIX, prefix
									+ COLON+ATTRIBUTE); //$NON-NLS-1$
					attrEl.setAttribute(NAME, attrNames[i]); //$NON-NLS-1$
					element.appendChild(attrEl);
				}
			}
		} else {
			for (int i = 0; i < attrNames.length; i++) {
				if (!existInerfaceAttrs.contains(attrNames[i])) {
					Element attrEl = document.createElementNS(
							JSF2ResourceUtil.JSF2_URI_PREFIX, ATTRIBUTE); //$NON-NLS-1$
					attrEl.setAttribute(NAME, attrNames[i]); //$NON-NLS-1$
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
		
		//Element interfaceElement = checkCompositeInterface(document);
		//if (interfaceElement == null)
			//return null;
		return file;
	}

	public Element checkCompositeInterface(IDOMDocument document) {
		if (document == null) {
			return null;
		}
		Element element = document.getDocumentElement();
		if (element == null) {
			return null;
		}
		
		if(!hasNamespace(element, JSF2ResourceUtil.JSF2_URI_PREFIX))
			return null;
		
		return findInterfaceComponent(element);
	}
	
	private boolean hasNamespace(Node node, String URI){
		NamedNodeMap attributes = node.getAttributes();
		for(int i = 0; i < attributes.getLength(); i++){
			Node attr = attributes.item(i);
			
			if(attr.getNodeName().startsWith(XMLNS+COLON) && URI.equals(attr.getNodeValue()))
				return true;
		}
		return false;
	}

	private String getPrefix(Node node, String URI){
		NamedNodeMap attributes = node.getAttributes();
		for(int i = 0; i < attributes.getLength(); i++){
			Node attr = attributes.item(i);
			
			if(attr.getNodeName().startsWith(XMLNS+COLON) && URI.equals(attr.getNodeValue()))
				return attr.getLocalName();
		}
		return null;
	}
	
	private Element findNodeToCreateCompositeInterface(Element rootNode){
		if(hasNamespace(rootNode, JSF2ResourceUtil.JSF2_URI_PREFIX))
			return rootNode;
		NodeList children = rootNode.getChildNodes();
		for(int i = 0; i < children.getLength(); i++){
			Node child = children.item(i);
			if(child instanceof Element){
				Node result = findNodeToCreateCompositeInterface((Element)child);
				if(result != null && result instanceof Element)
					return (Element)result;
			}
		}
		return null;
	}
	
	private Element createCompositeInterface(Document document){
		Element rootElement = document.getDocumentElement();
		if (rootElement == null) {
			rootElement = createDocumentElementForCompositeInterface(document);
		}
		
		Element node = findNodeToCreateCompositeInterface(rootElement);
		
		if(node == null){
			addURI(document, rootElement);
			node = rootElement;
		}
		
		String prefix = getPrefix(node, JSF2ResourceUtil.JSF2_URI_PREFIX);
		Element interfaceElement = document.createElement(prefix+COLON+INTERFACE);
		
		node.appendChild(interfaceElement);
		
		DocumentNodeFormatter formatter = new DocumentNodeFormatter();
		formatter.format(document);
		
		return interfaceElement;
	}
	
	private Element createDocumentElementForCompositeInterface(Document document){
		Element rootElement = document.createElement(HTML);
		
		Attr attr = document.createAttribute(XMLNS);
		attr.setValue(XHTML_URI);
		rootElement.setAttributeNode(attr);
		
		attr = document.createAttribute(XMLNS+COLON+HTML_PREFIX);
		attr.setValue(HTML_URI);
		rootElement.setAttributeNode(attr);
		
		attr = document.createAttribute(XMLNS+COLON+DEFAULT_COMPOSITE_PREFIX);
		attr.setValue(JSF2ResourceUtil.JSF2_URI_PREFIX);
		rootElement.setAttributeNode(attr);
		
		document.appendChild(rootElement);
		return rootElement;
	}
	
	private void addURI(Document document, Element rootElement){
		Attr attr = document.createAttribute(XMLNS+COLON+DEFAULT_COMPOSITE_PREFIX);
		attr.setValue(JSF2ResourceUtil.JSF2_URI_PREFIX);
		rootElement.setAttributeNode(attr);
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
		InputStream inputStream = null;
		try {
			inputStream = file.getContents();
			if (inputStream != null) {
				StringBuilder buffer = new StringBuilder(); //$NON-NLS-1$
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
			if(inputStream!=null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// Ignore
				}
			}
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

	public Set<String> getInterfaceAttrs(Element interfaceElement) {
		Set<String> interfaceAttrs = new HashSet<String>(0);
		if (interfaceElement != null) {
			String prefix = interfaceElement.getPrefix();
			String nodeName = ATTRIBUTE;
			if (prefix != null && !"".equals(prefix)) { //$NON-NLS-1$
				nodeName = prefix + COLON + nodeName; //$NON-NLS-1$
			}
			NodeList attrsElements = interfaceElement
					.getElementsByTagName(nodeName);
			if (attrsElements != null) {
				for (int i = 0; i < attrsElements.getLength(); i++) {
					Node el = attrsElements.item(i);
					if (el instanceof IDOMElement) {
						IDOMElement element = (IDOMElement) el;
						String attrvalue = element.getAttribute(NAME); //$NON-NLS-1$
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
