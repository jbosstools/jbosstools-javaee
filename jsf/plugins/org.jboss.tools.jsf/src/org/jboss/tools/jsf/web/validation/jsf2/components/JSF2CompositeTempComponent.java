/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.web.validation.jsf2.components;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.JSFAbstractValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ValidatorConstants;
import org.w3c.dom.NamedNodeMap;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2CompositeTempComponent extends JSFAbstractValidationComponent {

	private List<String> attrNames = new ArrayList<String>(0);
	private ElementImpl element;
	private String componentResLoc;

	public JSF2CompositeTempComponent(ElementImpl element) {
		this.element = element;
	}

	public void createValidationMessage() {
		String nodeName = element.getLocalName();
		setValidationMessage(MessageFormat.format(
				JSFUIMessages.Missing_JSF_2_Composite_Component, nodeName));
	}

	@Override
	public void createMessageParams() {
		NamedNodeMap attrsMap = element.getAttributes();
		if (attrsMap != null && attrsMap.getLength() != 0) {
			for (int i = 0; i < attrsMap.getLength(); i++) {
				IDOMAttr attr = (IDOMAttr) attrsMap.item(i);
				attrNames.add(attr.getName());
			}
		}
		super.createMessageParams();
	}

	public String[] getAttrNames() {
		return attrNames.toArray(new String[0]);
	}

	public String getType() {
		return JSF2ValidatorConstants.JSF2_COMPOSITE_COMPONENT_TYPE;
	}

	public String getComponentResourceLocation() {
		if (componentResLoc == null) {
			String uriString = element.getNamespaceURI();
			String relativeLocation = uriString.replaceFirst(
					JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$
			String nodeName = element.getLocalName();
			componentResLoc = relativeLocation + "/" + nodeName + ".xhtml"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return componentResLoc;
	}

	/**
	 * @return the element
	 */
	public ElementImpl getElement() {
		return element;
	}

}
