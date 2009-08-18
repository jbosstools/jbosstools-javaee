/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLContextParamLinkHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.struts.StrutsProject;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Jeremy
 */
public class StrutsXMLContextParamLinkHyperlinkPartitioner extends XMLContextParamLinkHyperlinkPartitioner {
	public static final String STRUTS_XML_CONTEXT_PARAM_LINK_PARTITION = "org.jboss.tools.common.text.ext.xml.STRUTS_XML_CONTEXT_PARAM_LINK";

	private String[] STRUTS_PROJECT_NATURES = {StrutsProject.NATURE_ID };
	
	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLContextParamLinkHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		return STRUTS_XML_CONTEXT_PARAM_LINK_PARTITION;
	}

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLContextParamLinkHyperlinkPartitioner#recognizeNature(org.eclipse.jface.text.IDocument)
	 */
	protected boolean recognizeNature(IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			IFile documentFile = smw.getFile();
			if (documentFile == null)
				return false;

			IProject project = documentFile.getProject();

			for (int i = 0; i < STRUTS_PROJECT_NATURES.length; i++) {
				if (project.getNature(STRUTS_PROJECT_NATURES[i]) != null) 
					return true;
			}
			return false;
		} catch (CoreException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return false;
		} finally {
			smw.dispose();
		}
	}

	private static final String SERVLET_TAGNAME = "servlet";
	private static final String SERVLET_CLASS_TAGNAME = "servlet-class";
	private static final String INIT_PARAM_TAGNAME = "init-param";
	private static final String PARAM_NAME_TAGNAME = "param-name";
	private static final String PARAM_VALUE_TAGNAME = "param-value";
	private static final String[] VALID_INIT_PARAM_NAMES = 
		{"config"};
	private static final String[] VALID_SERVLET_CLASSES = 
		{"org.apache.struts.action.ActionServlet"};

	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		
		if (!recognizeNature(document)) 
			return false;
		
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (!(n instanceof Text)) return false;
			
			Node paramValueNode = n.getParentNode();
			if (paramValueNode == null || 
					(!PARAM_VALUE_TAGNAME.equals(paramValueNode.getNodeName())))
				return false;
			
			Node initParamNode = paramValueNode.getParentNode();
			if (initParamNode == null || 
					(!INIT_PARAM_TAGNAME.equals(initParamNode.getNodeName())))
				return false;
			
			Node servletNode = initParamNode.getParentNode();
			if (servletNode == null ||
					(!SERVLET_TAGNAME.equals(servletNode.getNodeName())))
				return false;
				
			Node paramNameNode = null;
			NodeList children = initParamNode.getChildNodes();
			for (int i = 0; paramNameNode == null && children != null && i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element && PARAM_NAME_TAGNAME.equals(child.getNodeName())) {
					paramNameNode = child;
				}
			}
			if (paramNameNode == null) return false;
			
			String paramNameValue = null;
				NodeList list = paramNameNode.getChildNodes();
				for (int i = 0; list != null && i < list.getLength(); i++) {
					if (list.item(i) instanceof Text) {
						Text text = (Text)list.item(i);
						int start = Utils.getValueStart(text);
						int end = Utils.getValueEnd(text);
						if(start < 0) continue;
						if (paramNameValue == null) {
							paramNameValue = Utils.trimQuotes(document.get(start, end - start));
						} else {
							paramNameValue += Utils.trimQuotes(document.get(start, end - start));
						}
					}
				}
			if (paramNameValue == null) return false;
			boolean paramNameValueIsCorrect = false;
			for (int i = 0; i < VALID_INIT_PARAM_NAMES.length; i++) {
				if (VALID_INIT_PARAM_NAMES[i].equals(paramNameValue))
					paramNameValueIsCorrect = true;
					break;
			}
			if(!paramNameValueIsCorrect && paramNameValue.startsWith("config/")) {
				paramNameValueIsCorrect = true;
			}

			if (!paramNameValueIsCorrect) return false;
			
			Node servletClassNode = null;
			children = servletNode.getChildNodes();
			for (int i = 0; servletClassNode == null && children != null && i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element && SERVLET_CLASS_TAGNAME.equals(child.getNodeName())) {
					servletClassNode = child;
				}
			}
			if (servletClassNode == null) return false;
			
			String servletClassValue = null;

				list = servletClassNode.getChildNodes();
				for (int i = 0; list != null && i < list.getLength(); i++) {
					if (list.item(i) instanceof Text) {
						Text text = (Text)list.item(i);
						int start = Utils.getValueStart(text);
						int end = Utils.getValueEnd(text);
						if(start < 0) continue;
						if (servletClassValue == null) {
							servletClassValue = Utils.trimQuotes(document.get(start, end - start));
						} else {
							servletClassValue += Utils.trimQuotes(document.get(start, end - start));
						}
					}
				}

			if (servletClassValue == null) return false;
			
			boolean servletClassValueIsCorrect = false;
			for (int i = 0; i < VALID_SERVLET_CLASSES.length; i++) {
				if (VALID_SERVLET_CLASSES[i].equals(servletClassValue))
					servletClassValueIsCorrect = true;
					break;
			}

			return (paramNameValueIsCorrect && servletClassValueIsCorrect);
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return false;
		} finally {
			smw.dispose();
		}
	}
}
