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

import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSF2URITempComponent extends JSF2AbstractValidationComponent {

	private String validationMessage = ""; //$NON-NLS-1$
	private String URI;

	public JSF2URITempComponent(String URI) {
		this.URI = URI;
	}

	public void createValidationMessage() {
		this.validationMessage = MessageFormat.format(
				JSFUIMessages.Missing_JSF_2_Resources_Folder, getResourcesFolder());
	}
	
	public String getResourcesFolder(){
		return "/resources" + URI.replaceAll(JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public String getType() {
		return JSF2_URI_TYPE;
	}

	public String getComponentResourceLocation() {
		return ""; //$NON-NLS-1$
	}

	public String getURI() {
		return URI;
	}

}
