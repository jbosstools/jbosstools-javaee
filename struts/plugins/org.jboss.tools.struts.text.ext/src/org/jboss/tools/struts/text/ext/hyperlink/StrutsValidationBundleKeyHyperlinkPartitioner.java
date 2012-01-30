/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.text.ext.hyperlink;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLTagAttributeValueHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 *
 */
public class StrutsValidationBundleKeyHyperlinkPartitioner extends XMLTagAttributeValueHyperlinkPartitioner {
	public static final String STRUTS_VALIDATION_BUNDLE_KEY_PARTITION = "org.jboss.tools.common.text.ext.xml.STRUTS_VALIDATION_BUNDLE_KEY";

	private static final String RESOURCE_ATTRNAME = "resource";
	private static final String FALSE_ATTRVALUE = "false";
	
	
	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLTagAttributeValueHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		return STRUTS_VALIDATION_BUNDLE_KEY_PARTITION;
	}
	
	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, int offset, IHyperlinkRegion region) {
		if (!super.recognize(document, offset, region)) 
			return false;
		
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);
			if (!(n instanceof Attr)) return false;
			Node parentNode = ((Attr)n).getOwnerElement();
			
			String resourceAttrValue = getAttributeValue(document, parentNode, RESOURCE_ATTRNAME);
			if (resourceAttrValue != null && FALSE_ATTRVALUE.equals(resourceAttrValue)) 
				return false;

			return true;
		} finally {
			smw.dispose();
		}

	}
	
	protected String getAttributeValue (IDocument document, Node node, String attrName) {
		if(document == null || node == null || attrName == null) return null;
		try {
			Attr attr = (Attr)node.getAttributes().getNamedItem(attrName);
			return Utils.getTrimmedValue(document, attr);
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}
}