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

import java.util.Properties;
import org.jboss.tools.common.model.ui.*;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultValueAdapter;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.jst.jsp.outline.*;

public class JSFKnowledgeBaseAdapter extends DefaultValueAdapter implements IActionHelper, IActionHelperExtension {
	Properties context = new Properties();

	public String getCommand() {
		return "Browse...";
	}

	public String invoke(Control control) {
		return invoke0(control);
	}

	public String invoke0(Control control) {
		ValueHelper h = new ValueHelper();
		context.put("valueHelper", h);
		String nodeName = "h:" + attribute.getProperty("nodeName");
		String attrName = attribute.getProperty("attrName");
		context.setProperty("nodeName", nodeName);
		context.setProperty("attributeName", attrName);
		String query = "/" + nodeName + "@" + attrName;
		context.setProperty("query", query);
		context.setProperty("help", query);
		context.setProperty("title", "Edit " + WizardKeys.toDisplayName(attrName));
		context.setProperty("subtitle", "<" + context.getProperty("nodeName") + ">");
		if(getValue() instanceof String) context.put("value", getValue());
		JSPTreeDialog dialog = new JSPTreeDialog();
		dialog.setObject(context);
		if(dialog.execute() != 0) return null;
		return context.getProperty("value");
	}

	public Object getAdapter(Class adapter) {
		if(adapter == IActionHelper.class) return this;
		return super.getAdapter(adapter);
	}

	public boolean isEditableInline() {
		return true;
	}

}
