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
package org.jboss.tools.jsf.project.capabilities;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.jst.web.project.helpers.LibrarySet;

public class JarPerformer extends PerformerItem {
	XModel model;
	LibrarySet set;
	String jar;
	
	public void init(XModel model, LibrarySet set, String jar) {
		this.model = model;
		this.set = set;
		this.jar = jar;
	}
	
	public String getDisplayName() {
		return jar;
	}

}
