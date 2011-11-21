/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.uiutils.actions;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.Wizard;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class NewFileWizardAction extends Action<Wizard> {

	public NewFileWizardAction() {
		super(IDELabel.Menu.FILE, IDELabel.Menu.NEW, IDELabel.Menu.OTHER);
	}

	@Override
	public Wizard run() {
		SWTBot b = performMenu();
		return new Wizard(b.activeShell().widget);
	}
}
