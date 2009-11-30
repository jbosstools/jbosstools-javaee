/******************************************************************************* 
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.seam.template;

import java.util.ArrayList;
import java.util.List;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamPdfListTemplate extends SeamPdfAbstractTemplate {

	private static List<String> NUMBERED_VALUES;
	static{
		NUMBERED_VALUES = new ArrayList<String>(0);
		NUMBERED_VALUES.add("numbered");
		NUMBERED_VALUES.add("greek");
		NUMBERED_VALUES.add("roman");
		NUMBERED_VALUES.add("zapfdingbats");
		NUMBERED_VALUES.add("zapfdingbats_number");
	}
	

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		String styleAttr = ((Element)sourceNode).getAttribute(HTML.ATTR_STYLE);
		if (isNumberedAttr(styleAttr)) {
			return new VpeCreationData(visualDocument.createElement(HTML.TAG_OL));
		}
		return new VpeCreationData(visualDocument.createElement(HTML.TAG_UL));
	}
	
	private boolean isNumberedAttr (String styleAttr){
		if (styleAttr!=null) {
			return NUMBERED_VALUES.contains(styleAttr);
		}
		return false;
	}
	
}
