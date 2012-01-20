/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test.uiutils.actions;

import org.jboss.tools.cdi.bot.test.uiutils.wizards.JSFWebProjectWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.Wizard;

public class NewJSFProjectWizard extends NewFileWizardAction {
	
	public NewJSFProjectWizard() {
		super();		
	}

	@Override
	public JSFWebProjectWizard run() {
		Wizard w = super.run();
		w.selectTemplate("JBoss Tools Web", "JSF", "JSF Project");
		w.next();
		return new JSFWebProjectWizard();
	}

}
