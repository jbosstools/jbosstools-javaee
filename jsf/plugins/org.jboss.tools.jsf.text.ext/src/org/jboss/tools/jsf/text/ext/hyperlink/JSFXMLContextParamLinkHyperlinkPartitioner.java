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
package org.jboss.tools.jsf.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLContextParamLinkHyperlinkPartitioner;

/**
 * @author Jeremy
 */
public class JSFXMLContextParamLinkHyperlinkPartitioner extends XMLContextParamLinkHyperlinkPartitioner {
	public static final String JSF_XML_CONTEXT_PARAM_LINK_PARTITION = "org.jboss.tools.common.text.ext.xml.JSF_XML_CONTEXT_PARAM_LINK"; //$NON-NLS-1$

	private String[] JSF_PROJECT_NATURES = {
		JSFNature.NATURE_ID
	};
	
	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLContextParamLinkHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		return JSF_XML_CONTEXT_PARAM_LINK_PARTITION;
	}

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLContextParamLinkHyperlinkPartitioner#recognizeNature(org.eclipse.jface.text.IDocument)
	 */
	protected boolean recognizeNature(IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			IFile documentFile = smw.getFile();
			IProject project = documentFile.getProject();
			if(project == null || !project.isAccessible()) return false;

			for (int i = 0; i < JSF_PROJECT_NATURES.length; i++) {
				if (project.getNature(JSF_PROJECT_NATURES[i]) != null) 
					return true;
			}
			return false;
		} catch (CoreException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return false;
		} finally {
			smw.dispose();
		}
	}

	private static final String CONTEXT_PARAM_TAGNAME = "context-param"; //$NON-NLS-1$
	private static final String PARAM_NAME_TAGNAME = "param-name"; //$NON-NLS-1$
	private static final String PARAM_VALUE_TAGNAME = "param-value"; //$NON-NLS-1$
	private static final String[] VALID_CONTEXT_PARAM_NAMES = 
		{"javax.faces.CONFIG_FILES","javax.faces.application.CONFIG_FILES"}; //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		if (!recognizeNature(document)) 
			return false;
		
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (!(n instanceof Text)) return false;
			
			Node paramValueNode = n.getParentNode();
			if (paramValueNode == null || 
					(!PARAM_VALUE_TAGNAME.equals(paramValueNode.getNodeName())))
				return false;
			
			Node contextParamNode = paramValueNode.getParentNode();
			if (contextParamNode == null || 
					(!CONTEXT_PARAM_TAGNAME.equals(contextParamNode.getNodeName())))
				return false;
			
			Node paramNameNode = null;
			NodeList children = contextParamNode.getChildNodes();
			for (int i = 0; paramNameNode == null && children != null && i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element && PARAM_NAME_TAGNAME.equals(child.getNodeName())) {
					paramNameNode = child;
				}
			}
			if (paramNameNode == null) return false;
			
			String paramNameValue = null;
				NodeList list = paramNameNode.getChildNodes();
				for (int i = 0; paramNameValue == null && list != null && i < list.getLength(); i++) {
					if (list.item(i) instanceof Text) {
						Text text = (Text)list.item(i);
						int start = Utils.getValueStart(text);
						if(start < 0) continue;
						int end = Utils.getValueEnd(text);
						if (paramNameValue == null) {
							paramNameValue = Utils.trimQuotes(document.get(start, end - start));
						} else {
							paramNameValue += Utils.trimQuotes(document.get(start, end - start));
						}
					}
				}
			if (paramNameValue == null) return false;
			for (int i = 0; i < VALID_CONTEXT_PARAM_NAMES.length; i++) {
				if (VALID_CONTEXT_PARAM_NAMES[i].equals(paramNameValue)) 
					return true;
			}
			return false;
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return false;
		} finally {
			smw.dispose();
		}
	}

}
