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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPTagAttributeValueHyperlinkPartitioner;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;

/**
 * @author Jeremy
 */
public class StrutsFormActionHyperlinkPartitioner extends JSPTagAttributeValueHyperlinkPartitioner {
	public static final String STRUTS_JSP_FORM_ACTION_PARTITION = "org.jboss.tools.common.text.ext.jsp.STRUTS_JSP_FORM_ACTION";

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.JSPTagAttributeValueHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		return STRUTS_JSP_FORM_ACTION_PARTITION;
	}
	
	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		if (!super.recognize(document, region)) 
			return false;
		
		
		return checkTypeIsEmpty(document, region);
	}
	
	private boolean checkTypeIsEmpty(IDocument document, IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (n == null || !(n instanceof Attr)) return false;
			Node node = ((Attr)n).getOwnerElement();
			Attr typeAttr = (Attr)node.getAttributes().getNamedItem("type");
			if (typeAttr == null) return true;
			String typeValue = Utils.getTrimmedValue(document, typeAttr);
			return (typeValue == null || typeValue.length() == 0);
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return false;
		} finally {
			smw.dispose();
		}
	}

}