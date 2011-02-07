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
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.JSFAbstractValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ValidatorConstants;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2AttrTempComponent extends JSFAbstractValidationComponent {

	private String validationMessage = ""; //$NON-NLS-1$
	private String type = JSF2ValidatorConstants.JSF2_UNFIXABLE_ATTR_TYPE;
	private ElementImpl parentEl;
	private IDOMAttr attr;
	private String componentResLocation;

	public JSF2AttrTempComponent(IDOMAttr attr, ElementImpl parentEl) {
		this.attr = attr;
		this.parentEl = parentEl;
	}

	public void createValidationMessage() {
		this.validationMessage = MessageFormat.format(
				JSFUIMessages.Missing_JSF_2_Component_Attr, attr.getName(),
				getElementName());
	}
	
	public String getElementName(){
		return parentEl.getLocalName();
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return attr.getName();
	}

	public String getComponentResourceLocation() {
		if (componentResLocation == null) {
			String uriString = parentEl.getNamespaceURI();
			String relativeLocation = uriString.replaceFirst(
					JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$
			String nodeName = parentEl.getLocalName();
			componentResLocation = relativeLocation + "/" + nodeName + ".xhtml"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return componentResLocation;
	}

}
