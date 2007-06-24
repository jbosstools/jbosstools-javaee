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
import org.eclipse.jface.text.IDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.jboss.tools.struts.StrutsProject;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLTagAttributeValueHyperlinkPartitioner;

public class StrutsConfigSetPropertyHyperlinkPartitioner extends XMLTagAttributeValueHyperlinkPartitioner {
	public static final String STRUTS_XML_SET_PROPERTY_PARTITION = "org.jboss.tools.common.text.ext.xml.STRUTS_XML_SET_PROPERTY";

	private String[] STRUTS_PROJECT_NATURES = {StrutsProject.NATURE_ID };

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.JSPTagAttributeValueHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		return STRUTS_XML_SET_PROPERTY_PARTITION;
	}
	
	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLContextParamLinkHyperlinkPartitioner#recognizeNature(org.eclipse.jface.text.IDocument)
	 */
	protected boolean recognizeNature(IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			IFile documentFile = smw.getFile();
			IProject project = documentFile.getProject();
			for (int i = 0; i < STRUTS_PROJECT_NATURES.length; i++) {
				if (project.getNature(STRUTS_PROJECT_NATURES[i]) != null) 
					return true;
			}
			return false;
		} catch (Exception x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return false;
		} finally {
			smw.dispose();
		}
	}

	private static final String CLASS_NAME_ATTR = "className";
	private static final String PROPERTY_ATTR = "property";
	
	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		
		if (!super.recognize(document, region)) 
			return false;
		
		if (!recognizeNature(document)) 
			return false;

		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n == null || !(n instanceof Attr)) return false;
			
			Node node = ((Attr)n).getOwnerElement();
			String propertyValue = getAttributeValue(document, node, PROPERTY_ATTR);
			Node parent = node.getParentNode();
			String classNameValue = getAttributeValue(document, parent, CLASS_NAME_ATTR);
			return (classNameValue != null && classNameValue.length() > 0 && 
					propertyValue != null && propertyValue.length() > 0);
		} catch (Exception x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return false;
		} finally {
			smw.dispose();
		}
	}
	
	private String getAttributeValue (IDocument document, Node node, String attrName) {
		try {
			Attr attr = (Attr)node.getAttributes().getNamedItem(attrName);
			return Utils.getTrimmedValue(document, attr);
		} catch (Exception x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}

}
