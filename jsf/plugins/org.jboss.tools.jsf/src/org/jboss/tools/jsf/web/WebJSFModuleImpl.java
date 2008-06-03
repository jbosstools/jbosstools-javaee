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
package org.jboss.tools.jsf.web;

import org.jboss.tools.jst.web.project.WebModuleImpl;

public class WebJSFModuleImpl extends WebModuleImpl {
	private static final long serialVersionUID = 4623085241117393676L;

	public String getPathPart() {
		return "jsf:" + super.getPathPart();
	}

}
