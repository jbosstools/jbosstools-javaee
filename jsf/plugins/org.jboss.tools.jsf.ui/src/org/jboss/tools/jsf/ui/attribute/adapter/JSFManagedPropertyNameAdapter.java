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
package org.jboss.tools.jsf.ui.attribute.adapter;

import org.jboss.tools.common.model.ui.*;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultValueAdapter;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.jsf.ui.Messages;

public class JSFManagedPropertyNameAdapter extends DefaultValueAdapter implements IActionHelper, IActionHelperExtension {

	public String getCommand() {
		return Messages.JSFManagedPropertyNameAdapter_Rename;
	}

	public String invoke(Control control) {
		return invoke0(control);
	}

	public String invoke0(Control control) {
		XActionInvoker.invoke("EditActions.Rename", modelObject, null); //$NON-NLS-1$
		return modelObject.getAttributeValue("property-name"); //$NON-NLS-1$
	}

	public Object getAdapter(Class adapter) {
		if(adapter == IActionHelper.class) return this;
		return super.getAdapter(adapter);
	}

	public boolean isEditableInline() {
		return false;
	}

}
