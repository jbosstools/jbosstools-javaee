/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.zip.UnzipOperation;
import org.jboss.tools.jst.web.kb.KbQuery.Tag;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@SuppressWarnings("restriction")
public class BatchUtil {

	/**
	 * Implement this interface to pass object to Util.scanXMLFile()
	 * 
	 *
	 */
	public static interface DocumentScanner {
		public void scanDocument(Document document);
	}

	/**
	 * Utility method that receives IDOMDocument from IModelManager by file and after
	 * invoking DocumentScanner.scanDocument(), releases the model.
	 * 
	 * @param file
	 * @param scanner
	 */
	public static void scanXMLFile(IFile file, DocumentScanner scanner) {
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager != null) {
			IStructuredModel model = null;
			try {
				model = manager.getModelForRead(file);
				if (model instanceof IDOMModel) {
					IDOMModel domModel = (IDOMModel) model;
					IDOMDocument document = domModel.getDocument();
					if(document != null) {
						scanner.scanDocument(document);
					}
				}
			} catch (CoreException e) {
				WebKbPlugin.getDefault().logError(e);
			} catch (IOException e) {
				WebKbPlugin.getDefault().logError(e);
			} finally {
				if (model != null) {
					model.releaseFromRead();
				}
			}
		}
	}

	/**
	 * Returns collection of text source references in xml file to attribute by name and value.
	 * 
	 * @param file
	 * @param attr
	 * @param value
	 * @return
	 */
	public static List<TextSourceReference> getAttributeReferences(IFile file, String name, String value) {
		String expression = "//*[@" + name + "=\"" + value + "\"]/@" + name;
		AttrReferencesRequestor<TextSourceReference> requestor = new AttrReferencesRequestor<TextSourceReference>(file, expression, TextSourceReference.class);
		scanXMLFile(file, requestor);
		return requestor.results;
	}

	/**
	 * Returns collection of text source references in xml file to name attribute of property tag by refValue and nameValue
	 * 
	 * @param file
	 * @param refValue value of ref attribute
	 * @param nameValue value of name attribute
	 * @return
	 */
	public static List<TextSourceReference> getPropertyAttributeReferences(IFile file, String refValue, String propertyName) {
		String expression = "//*[@"+BatchConstants.ATTR_REF+"=\""+refValue+"\"]//*[@" + BatchConstants.ATTR_NAME +
				"=\"" + propertyName + "\"]/@" + BatchConstants.ATTR_NAME;
		AttrReferencesRequestor<TextSourceReference> requestor = new AttrReferencesRequestor<TextSourceReference>(file, expression, TextSourceReference.class);
		scanXMLFile(file, requestor);
		return requestor.results;
	}

	/**
	 * Returns collection of text source references in xml file to name attribute of property tag by refValue and nameValue
	 * 
	 * @param file
	 * @param refValue value of ref attribute
	 * @param nameValue value of name attribute
	 * @return
	 */
	public static List<NodePathTextSourceReference> getNodePathPropertyAttributeReferences(IFile file, String refValue, String propertyName) {
		String expression = "//*[@"+BatchConstants.ATTR_REF+"=\""+refValue+"\"]//*[@" + BatchConstants.ATTR_NAME +
				"=\"" + propertyName + "\"]/@" + BatchConstants.ATTR_NAME;
		AttrReferencesRequestor<NodePathTextSourceReference> requestor = new AttrReferencesRequestor<NodePathTextSourceReference>(file, expression, NodePathTextSourceReference.class);
		scanXMLFile(file, requestor);
		return requestor.results;
	}

	public static class AttrReferencesRequestor<E extends TextSourceReference> implements DocumentScanner {
		IFile file;
		String expression;
		List<E> results = new ArrayList<E>();
		Class<E> cls;

		public AttrReferencesRequestor(IFile file, String expression, Class<E> cls) {
			this.file = file;
			this.expression = expression;
			this.cls = cls;
		}
		
		@Override
		public void scanDocument(Document document) {
			XPath xPath = XPathFactory.newInstance().newXPath();
			try {
				Object result = xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
				if(result instanceof NodeList) {
					NodeList list = (NodeList)result;
					for (int i = 0; i < list.getLength(); i++) {
						Node n = list.item(i);
						if(n instanceof AttrImpl) {
							AttrImpl a = (AttrImpl)n;
							int start0 = a.getValueRegionStartOffset();
							int length0 = a.getValueRegionText().length();
							if(a.getValueRegionText().startsWith("\"")) {
								start0++;
								length0 -= 2;
							}
							final int start = start0;
							final int length = length0;
							try{
						        E ref =   cls.newInstance();
								ref.setLength(length);
								ref.setResource(file);
								ref.setStartPosition(start);
								ref.setNodePath(n);
								
								results.add(ref);
							}catch(InstantiationException e){
								
							}catch(IllegalAccessException e){
								
							}
						}
					}
				}
			} catch (XPathExpressionException e) {
				BatchCorePlugin.pluginLog().logError(e);
			}
		}

		public List<E> getResults() {
			return results;
		}
	}
	
	private static List<Tag> getNodePath(Node node){
		ArrayList<Tag> tags = new ArrayList<Tag>();
		Node n = node;
		if(n instanceof AttrImpl){
			n = ((AttrImpl) node).getOwnerElement();
		}
		
		while(!(n instanceof IDOMDocument) && n != null){
			HashMap<String, String> attributes = new HashMap<String, String>();
			if(n.hasAttributes()){
				
				NamedNodeMap attrs = n.getAttributes();
				for(int index = 0; index < attrs.getLength(); index++){
					Node attribute = attrs.item(index);
					attributes.put(attribute.getNodeName(), attribute.getNodeValue());
				}
				
			}
			Tag tag = new Tag(n.getNodeName(), attributes);
			tags.add(tag);
			n = n.getParentNode();
		}

		return tags;
	}
	
	private static File TEMPLATE_FOLDER;

	public static File getTemplatesFolder() throws IOException {
		if(TEMPLATE_FOLDER == null) {
			Bundle bundle = BatchCorePlugin.getDefault().getBundle();
			String version = bundle.getVersion().toString();
			IPath stateLocation = Platform.getStateLocation(bundle);
			File templatesDir = FileLocator.getBundleFile(bundle);
			if(templatesDir.isFile()) {
				File toCopy = new File(stateLocation.toFile(),version);
				if(!toCopy.exists()) {
					toCopy.mkdirs();
					UnzipOperation unZip = new UnzipOperation(templatesDir.getAbsolutePath());
					unZip.execute(toCopy, "templates.*");
				}
				templatesDir = toCopy;
			}
			TEMPLATE_FOLDER = new File(templatesDir, "templates");
		}
		return TEMPLATE_FOLDER;
	}

	/**
	 * Returns path to existing batch.xml, or to implied batch.xml in Java source folder,
	 * or null, if there is no source folders.
	 *   
	 * @param p
	 * @return
	 */
	public static IPath getBatchXMLPath(IProject p) {
		IPath result = null;
		for (IResource f: EclipseUtil.getJavaSourceRoots(p)) {
			if(f instanceof IFolder) {
				IFolder fm = ((IFolder)f).getFolder(BatchConstants.META_INF);
				IFile batch = fm.getFile(BatchConstants.BATCH_XML);
				if(batch.exists()) {
					return batch.getFullPath();
				} else if(result == null || fm.exists()) {
					result = batch.getFullPath();
				}
			}
		}
		return result;			
	}
	
	public static class TextSourceReference implements ITextSourceReference{
		private IResource resource;
		int startPosition;
		int length;
		
		public TextSourceReference(){
		}

		@Override
		public int getStartPosition() {
			return startPosition;
		}

		@Override
		public int getLength() {
			return length;
		}

		@Override
		public IResource getResource() {
			return resource;
		}
		
		public void setResource(IResource resource) {
			this.resource = resource;
		}

		public void setStartPosition(int startPosition) {
			this.startPosition = startPosition;
		}

		public void setLength(int length) {
			this.length = length;
		}
		
		public void setNodePath(Node node){
			
		}
		
	}
	
	public static class NodePathTextSourceReference extends TextSourceReference{
		private List<Tag> tags;

		public Collection<Tag> getNodePath(){
			return tags;
		}
		
		public void setNodePath(Node node){
			this.tags = BatchUtil.getNodePath(node);
		}
	}
	

}
