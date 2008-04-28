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

import java.util.ResourceBundle;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @see PreferencePage
 */
public class AutomationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	public AutomationPreferencePage() {
	}

	/**
	 * @see PreferencePage#init
	 */
	public void init(IWorkbench workbench)  {
	}

	/**
	 * @see PreferencePage#createContents
	 */
	protected Control createContents(Composite parent)  {
		noDefaultAndApplyButton();
		StyledText newControl = new StyledText(parent,SWT.WRAP);
		newControl.setText(ResourceBundle.getBundle(this.getClass().getPackage().getName()+".preferences").getString("AUTOMATION_PD"));
		newControl.setBackground(parent.getBackground());
		newControl.setEditable(false);
		return newControl;


	}
}
