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
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The Class AbstractRichFacesTemplate.
 */
public abstract class AbstractRichFacesTemplate extends VpeAbstractTemplate {
    
    /** The source style. */
    protected String sourceStyle;
    
    /** The source style class. */
    protected String sourceStyleClass;
    
    /** The source width. */
    protected String sourceWidth;
	
	/**
	 * Getting value attribute by name.
	 * 
	 * @param sourceNode the source node
	 * @param nameAttr name of attribute
	 * 
	 * @return value of attribute
	 */
	public String getAttribute(String nameAttr, Node sourceNode) {
		String returnValue = ""; //$NON-NLS-1$
		NamedNodeMap sourceAttrbutes = sourceNode.getAttributes();
		int length = sourceAttrbutes.getLength();
		for (int i = 0; i < length; i++) {
			Node item = sourceAttrbutes.item(i);
			if (item.getNodeName().equalsIgnoreCase(nameAttr)) {
				returnValue = item.getNodeValue();
				break;
			}
		}
		return returnValue;
	}
}
