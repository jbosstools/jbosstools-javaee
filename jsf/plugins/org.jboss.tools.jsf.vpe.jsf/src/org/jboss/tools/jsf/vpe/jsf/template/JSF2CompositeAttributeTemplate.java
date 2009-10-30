/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.jsf.vpe.jsf.template.util.JSF;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for composite:attribute tag
 * 
 * @author mareshkau
 *
 */
public class JSF2CompositeAttributeTemplate extends VpeAbstractTemplate{
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;
		String name = sourceElement.getAttribute(JSF.ATTR_NAME);
		String defaultValue = sourceElement.getAttribute(JSF.ATTR_DEFAULT);
		//we should register attributes only if we process this as custom component, but not when we open component definition page
		if(!pageContext.getVisualBuilder().isCurrentMainDocument()){
			String compositionCustomElementAttributeKey = Jsf2CustomComponentTemplate.JSF2_CUSTOM_COMPONENT_PARAMETR_KEY +name;
			if(pageContext.getCustomElementsAttributes().containsKey(compositionCustomElementAttributeKey)){				
				pageContext.addAttributeInCustomElementsMap(JSF.CUSTOM_COMPONENT_ATTR_PREFIX+name,
						pageContext.getCustomElementsAttributes().get(compositionCustomElementAttributeKey));
			}else if(defaultValue!=null) {
				pageContext.addAttributeInCustomElementsMap(JSF.CUSTOM_COMPONENT_ATTR_PREFIX+name, defaultValue);
			}
		}
		//it's invisible component
		return new VpeCreationData(null);
	}



}
