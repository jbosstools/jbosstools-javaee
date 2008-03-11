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

import java.util.StringTokenizer;

import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class AbstractRichFacesTemplate extends VpeAbstractTemplate {
	/**
	 * Getting value attribute by name
	 * 
	 * @param nameAttr
	 *            name of attribute
	 * @return value of attribute
	 */
	public String getAttribute(String nameAttr, Node sourceNode) {
		String returnValue = "";
		NamedNodeMap sourceAttrbutes = sourceNode.getAttributes();

		for (int i = 0; i < sourceAttrbutes.getLength(); i++) {
			if (sourceAttrbutes.item(i).getNodeName() 
					.equalsIgnoreCase(nameAttr)) {
				returnValue = sourceAttrbutes.item(i).getNodeValue();
			}
		}
		return returnValue;
	}
	
	/**
	 * 
	 * @param nameAttr
	 *            name of attribute
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @return True if value of attribute is empty or attribute dont exist
	 */
	public boolean isEmptyAttribute(String nameAttr, Node sourceNode) {

		return getAttribute(nameAttr, sourceNode).equalsIgnoreCase("");

	}
	
	/**
	 * 
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @return
	 */
	public String parseStyle(Node sourceNode) {
		String style = getAttribute("style", sourceNode);
		String str="";
		StringTokenizer st = new StringTokenizer(style,";:");
		
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			if(token.trim().equalsIgnoreCase("width") || token.trim().equalsIgnoreCase("height")) {
				st.nextToken();
				continue;
			}
			str+=token + ":" + st.nextToken() + "; ";
		}
		
		return str;
	}

	/**
	 * 
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @return width value
	 */
	public String parseStyleWidth(Node sourceNode) {

		String style = getAttribute("style", sourceNode);
		String widthValue = "";
		StringTokenizer st = new StringTokenizer(style, ":;");

		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			if (str.trim().equalsIgnoreCase("width")) {
				widthValue = st.nextToken();
			}
		}
		return widthValue;

	}

}
