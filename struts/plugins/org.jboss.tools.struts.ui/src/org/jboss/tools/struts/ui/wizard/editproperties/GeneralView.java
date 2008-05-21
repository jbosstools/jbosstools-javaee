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

import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.model.helpers.*;

public class GeneralView extends AbstractView {
	protected FilteredTable objectEditor = new FilteredTable();
	private XModelObject object = null;
	private boolean enabled = false;

	public void dispose() {
		super.dispose();
		if (objectEditor != null) {
			objectEditor.dispose();
		}
	}

	public void setContext(StrutsEditPropertiesContext context) {
		super.setContext(context);
		object = context.getObject();
		objectEditor.setAdvanced(isAdvanced());
		objectEditor.setXEntityData(context.getEntityData());
		objectEditor.setModelObject(object);
		objectEditor.loadAttributes();
		enabled = (objectEditor.getPropertiesLength() > 0);
	}

	public Control createControl(Composite parent) {
		return objectEditor.createControl(parent);
	}
	
	protected boolean isAdvanced() {
		return false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void stopEditing() {
		//objectEditor.stopEditing();
	}
}
