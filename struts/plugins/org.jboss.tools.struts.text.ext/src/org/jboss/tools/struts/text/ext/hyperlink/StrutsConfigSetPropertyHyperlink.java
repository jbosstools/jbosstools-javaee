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

import org.eclipse.jface.text.IRegion;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.ClassMethodHyperlink;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;

public class StrutsConfigSetPropertyHyperlink extends ClassMethodHyperlink {
	private static final String CLASSNAME_ATTRNAME = "className";
	private static final String PROPERTY_ATTRNAME = "property";

	protected String getMethodName(IRegion region) {
		String propertyName = getAttributeValue(region, PROPERTY_ATTRNAME);
		if (propertyName == null || propertyName.length() == 0) return null;
		return ("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
	}
	
	protected String getClassName(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n == null || !(n instanceof Attr)) return null;
			
			Node node = ((Attr)n).getOwnerElement();
			Node parentNode = node.getParentNode();
			
			return getAttributeValue(parentNode, CLASSNAME_ATTRNAME);
		} catch (Exception x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	protected String getMethodParams(IRegion region) {
		return null;
	}
}
