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

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class  AbstractRichFacesInputNumberTemplate extends AbstractRichFacesTemplate {
	/** INPUTSIZE_ATTRIBURE */
	final static private String INPUTSIZE_ATTRIBURE = "inputSize";
	
	/** INPUTCLASS_ATTRIBURE */
	final static private String INPUTCLASS_ATTRIBURE = "inputClass";

	/**
	 * 
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#removeAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String)
	 */
	@Override
	public void removeAttribute(VpePageContext pageContext,
			Element sourceElement, Document visualDocument, Node visualNode,
			Object data, String name) {
		super.removeAttribute(pageContext, sourceElement, visualDocument,
				visualNode, data, name);

		setAttribute(pageContext, sourceElement, visualDocument, visualNode,
				data, name, "");

	}
	
	/** 
	 * Return a input size
	 * @param sourceNode a sourceNode
	 * @return a input size
	 */
	protected String getInputSize(Node sourceNode) {
		String returnValue = getDefaultInputSize();
		String tmp = getAttribute(INPUTSIZE_ATTRIBURE, sourceNode); 
		if ( tmp.length() != 0  ) {
			returnValue = tmp;
		}
		return returnValue;
	}	

	/** 
	 * Return a input class
	 * @param sourceNode a sourceNode
	 * @return a input class
	 */
	public String getInputClass(Node sourceNode) {
		String returnValue = getDefaultInputClass();
		String tmp = getAttribute(INPUTCLASS_ATTRIBURE, sourceNode); 
		if ( tmp.length() != 0  ) {
			returnValue = new StringBuffer().append(returnValue).append(" ").append(tmp).toString();
		}
		return returnValue;
	}	

	


	public abstract String getDefaultInputSize();
	
	public abstract String getDefaultInputClass();
	

}
