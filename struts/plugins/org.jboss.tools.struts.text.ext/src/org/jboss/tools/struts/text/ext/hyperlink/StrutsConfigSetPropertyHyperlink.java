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

import java.text.MessageFormat;

import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.text.ext.hyperlink.ClassMethodHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class StrutsConfigSetPropertyHyperlink extends ClassMethodHyperlink {
	private static final String CLASSNAME_ATTRNAME = "className";
	private static final String PROPERTY_ATTRNAME = "property";

	protected String getMethodName(IRegion region) {
		String propertyName = getAttributeValue(region, PROPERTY_ATTRNAME);
		if (propertyName == null || propertyName.length() == 0) return null;
		return ("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
	}
	
	protected String getClassName(IRegion region) {
		if(region == null) return null;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n == null || !(n instanceof Attr)) return null;
			
			Node node = ((Attr)n).getOwnerElement();
			Node parentNode = node.getParentNode();
			
			return getAttributeValue(parentNode, CLASSNAME_ATTRNAME);
		} finally {
			smw.dispose();
		}
	}
	
	protected String getMethodParams(IRegion region) {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String propertyName = getAttributeValue(getHyperlinkRegion(), PROPERTY_ATTRNAME);
		if (propertyName == null)
			return  MessageFormat.format(Messages.OpenA, Messages.Setter);
		
		return MessageFormat.format(Messages.OpenGetterOrSetterForProperty, Messages.Setter, propertyName);
	}
}
