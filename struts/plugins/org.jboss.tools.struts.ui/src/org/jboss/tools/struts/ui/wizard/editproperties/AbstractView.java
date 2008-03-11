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
package org.jboss.tools.struts.ui.wizard.editproperties;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.struts.model.helpers.StrutsEditPropertiesContext;

public class AbstractView {
	protected StrutsEditPropertiesContext context = null;

	public void dispose() {
		if (context!=null) context.dispose();
		context = null;
	}

	public void setContext(StrutsEditPropertiesContext context) {
		this.context = context;
	}

	public Control createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		return c;
	}

	public void update() {
	}

	public void stopEditing() {
	}

}
