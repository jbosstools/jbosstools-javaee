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

import java.text.MessageFormat;
import java.util.Properties;
import org.jboss.tools.common.model.ui.*;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultValueAdapter;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.jsf.ui.JsfUIMessages;
import org.jboss.tools.jst.jsp.contentassist.JSPDialogContentProposalProvider;
import org.jboss.tools.jst.jsp.outline.*;

public class JSFKnowledgeBaseAdapter extends DefaultValueAdapter implements IActionHelper, IActionHelperExtension {
	Properties context = new Properties();

	public void setContext(Properties context) {
		this.context = context;
	}

	public String getCommand() {
		return JsfUIMessages.JSFKnowledgeBaseAdapter_Browse;
	}

	public String invoke(Control control) {
		return invoke0(control);
	}

	public String invoke0(Control control) {
		//ValueHelper h = new ValueHelper();
		//context.put("valueHelper", h);
		String nodeName = "h:" + attribute.getProperty("nodeName"); //$NON-NLS-1$ //$NON-NLS-2$
		String attrName = attribute.getProperty("attrName"); //$NON-NLS-1$
		context.setProperty("nodeName", nodeName); //$NON-NLS-1$
		context.setProperty("attributeName", attrName); //$NON-NLS-1$

		JSPDialogContentProposalProvider pp = new JSPDialogContentProposalProvider();
		pp.setAttrMode();
		pp.setContext(context);

		String query = "/" + nodeName + "@" + attrName; //$NON-NLS-1$ //$NON-NLS-2$
		context.setProperty("query", query); //$NON-NLS-1$
		context.setProperty("help", query); //$NON-NLS-1$
		context.setProperty("title", MessageFormat.format(JsfUIMessages.JSFKnowledgeBaseAdapter_Edit, WizardKeys.toDisplayName(attrName))); //$NON-NLS-1$
		context.setProperty("subtitle", "<" + context.getProperty("nodeName") + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		if(getValue() instanceof String) context.put("value", getValue()); //$NON-NLS-1$
		JSPTreeDialog dialog = new JSPTreeDialog();
		dialog.setObject(context);
		if(dialog.execute() != 0) return null;
		return context.getProperty("value"); //$NON-NLS-1$
	}

	public Object getAdapter(Class adapter) {
		if(adapter == IActionHelper.class) return this;
		return super.getAdapter(adapter);
	}

	public boolean isEditableInline() {
		return true;
	}

}
