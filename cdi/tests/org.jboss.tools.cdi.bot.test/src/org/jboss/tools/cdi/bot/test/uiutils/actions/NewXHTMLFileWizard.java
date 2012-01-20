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

import org.jboss.tools.cdi.bot.test.uiutils.wizards.Wizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.XHTMLDialogWizard;

public class NewXHTMLFileWizard extends NewFileWizardAction{

	public NewXHTMLFileWizard() {
		super();		
	}

	@Override
	public XHTMLDialogWizard run() {
		Wizard w = super.run();
		w.selectTemplate("JBoss Tools Web", "XHTML Page");
		w.next();
		return new XHTMLDialogWizard();
	}
	
}
