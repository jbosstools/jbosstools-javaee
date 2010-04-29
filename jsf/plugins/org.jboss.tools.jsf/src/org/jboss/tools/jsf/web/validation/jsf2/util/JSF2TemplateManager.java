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

package org.jboss.tools.jsf.web.validation.jsf2.util;

import java.io.InputStream;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSF2TemplateManager {

	public static final String COMPOSITE_COMPONENT_TEMPLATE_NAME = "composite.xhtml"; //$NON-NLS-1$

	private static JSF2TemplateManager instance = new JSF2TemplateManager();

	private JSF2TemplateManager() {

	}

	public static JSF2TemplateManager getManager() {
		return instance;
	}

	public InputStream createStreamFromTemplate(String templateName) {
		InputStream stream = null;
		stream = JSF2TemplateManager.class
				.getResourceAsStream("/resources/templates/" + templateName); //$NON-NLS-1$
		return stream;
	}

}
