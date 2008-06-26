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
package org.jboss.tools.struts.validator.ui.wizard.key;

import java.util.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class ResourcePathView extends AbstractResourcePathView {

	static Vector<String> history = new Vector<String>();

	public ResourcePathView() {}

	protected String[] getRequiredValues() {
		if(object != null) {
			Set<String> set = WebModulesHelper.getInstance(model).getResourceMapping().getResources(object);
			String[] s = set.toArray(new String[0]);
			return s;
		} else {
			return new String[0];
		}
	}
	protected Vector<String> history() {
		return history;
	}

	protected String getDisplayName() {
		return StrutsUIMessages.PATH_TO_RESOURCE;
	}

	public void action(String name) {
		XModelObject f = getSelectedFile();
		XModelObject o = model.createModelObject("PropertyFileSelector", null); //$NON-NLS-1$
		if(f != null)
		  o.setAttributeValue("property file", "" + combo.getItem(combo.getSelectionIndex())); //$NON-NLS-1$ //$NON-NLS-2$
		Properties p = new Properties();
		p.put("shell", combo.getShell()); //$NON-NLS-1$
		XActionInvoker.invoke("Edit", o, p); //$NON-NLS-1$
		String sp = o.getAttributeValue("property file"); //$NON-NLS-1$
		XModelObject fn = getFileObject(sp);
		if(fn == null || f == fn) return;
		addValue(sp);
	}

	protected XModelObject getSelectedFile() {
		int i = combo.getSelectionIndex();		
		return (i < 0) ? null : getFileObject(combo.getItem(i));
	}
	
	protected XModelObject getFileObject(String s) {
		return (s == null || s.length() == 0) ? null : model.getByPath("/" + s.replace('.', '/') + ".properties"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
