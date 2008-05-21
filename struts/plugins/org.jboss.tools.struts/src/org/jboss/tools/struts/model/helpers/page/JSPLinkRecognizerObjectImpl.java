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
package org.jboss.tools.struts.model.helpers.page;

import org.jboss.tools.common.model.impl.*;

public class JSPLinkRecognizerObjectImpl extends OrderedObjectImpl {
	private static final long serialVersionUID = 5500275229394953948L;
	boolean active = false;
		
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean b) {
		active = b;
	}

	public String getPathPart() {
		return "root:" + super.getPathPart();
	}
	public String getLongPath() {
		return getPathPart();
	}

	public String getPath() {
		return getPathPart();
	}
}
