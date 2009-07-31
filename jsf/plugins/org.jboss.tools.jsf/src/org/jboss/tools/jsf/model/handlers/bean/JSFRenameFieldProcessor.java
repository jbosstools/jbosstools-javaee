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
package org.jboss.tools.jsf.model.handlers.bean;

import org.jboss.tools.jsf.model.handlers.refactoring.JSFRenameProcessor;

public class JSFRenameFieldProcessor extends JSFRenameProcessor {
	public static final String IDENTIFIER = "org.jboss.tools.jsf.renameFieldProcesso"; //$NON-NLS-1$
	private boolean updateReferences = true;

	public JSFRenameFieldProcessor() {}

	public String getIdentifier() {
		return IDENTIFIER;
	}

	protected String getPropertyName() {
		return "property-name"; //$NON-NLS-1$
	}
	
	public boolean canEnableUpdateReferences() {
		return true;
	}

	public void setUpdateReferences(boolean update) {
		updateReferences = update;
	}

	public boolean getUpdateReferences() {
		return updateReferences;
	}

}
