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
package org.jboss.tools.struts.ui.preferences;

import org.jboss.tools.common.model.ui.objecteditor.XChildrenEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.engines.impl.EnginesLoader;
import org.jboss.tools.common.model.util.AbstractTableHelper;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.helpers.page.LinkRecognizer;

public class LinkRecognizerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage{
	LinkRecognizer linkRecognizer;
	LinksEditor linksEditor = new LinksEditor();
	XModelObject object;
	
	public LinkRecognizerPreferencePage() {
		this.setTitle(StrutsUIMessages.LINK_RECOGNIZER);
	}

	public void dispose() {
		super.dispose();
		if (linksEditor!=null) linksEditor.dispose();
		linksEditor = null;
	}

	public void init(IWorkbench workbench) {}

	protected Control createContents(Composite parent) {
		linkRecognizer = LinkRecognizer.getInstance();
		object = linkRecognizer.getModelObject().copy();
		linksEditor.setObject(object);		
		linksEditor.createControl(parent);
		return linksEditor.getControl();
	}

	public boolean performOk() {
		EnginesLoader.merge(linkRecognizer.getModelObject(), object);
		linkRecognizer.save();
		return true;
	}
	
	protected void performDefaults() {
		linkRecognizer.restoreDefaults(object);
		linksEditor.update();
	}

}

class LinksEditor extends XChildrenEditor {
	
	public LinksEditor() {
		setMnemonicEnabled(true);
	}

	protected AbstractTableHelper createHelper() {
		return new LinksTableHelper();
	}
	
	protected String getAddActionPath() {
		return "CreateActions.CreateLink"; //$NON-NLS-1$
	}

}

class LinksTableHelper extends AbstractTableHelper {
	static String[] header = new String[]{"tag", "attribute", "refer to", "link type"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	static String[] visibleHeader = new String[]{StrutsUIMessages.TAG, StrutsUIMessages.ATTRIBUTE, StrutsUIMessages.REFER_TO, StrutsUIMessages.LINK_TYPE};

	public String[] getHeader() {
		return header;
	}
	
	public String[] getVisibleHeader() {
		return visibleHeader;
	}
}
	