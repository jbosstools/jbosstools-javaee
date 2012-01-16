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

package org.jboss.tools.cdi.bot.test;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.jboss.tools.cdi.bot.test.uiutils.BeansXMLValidationHelper;
import org.jboss.tools.cdi.bot.test.uiutils.CDIProjectHelper;
import org.jboss.tools.cdi.bot.test.uiutils.CDIWizardHelper;
import org.jboss.tools.cdi.bot.test.uiutils.EditorResourceHelper;
import org.jboss.tools.cdi.bot.test.uiutils.LibraryHelper;
import org.jboss.tools.cdi.bot.test.uiutils.OpenOnHelper;
import org.jboss.tools.cdi.bot.test.uiutils.QuickFixHelper;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardBaseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;

public class CDIBase extends SWTTestExt {
	
	private static SWTBotEclipseEditor ed;
	protected static CDIProjectHelper projectHelper = new CDIProjectHelper(); 
	protected static BeansXMLValidationHelper beansHelper = new BeansXMLValidationHelper();
	protected static CDIWizardHelper wizard = new CDIWizardHelper();
	protected static CDIWizardBaseExt wizardExt = new CDIWizardBaseExt();
	protected static OpenOnHelper openOnUtil = new OpenOnHelper();
	protected static LibraryHelper libraryUtil = new LibraryHelper();
	protected static EditorResourceHelper editResourceUtil = new EditorResourceHelper();
	protected static QuickFixHelper quickFixHelper = new QuickFixHelper();
	
	
	public SWTBotEclipseEditor getEd() {
		return ed;
	}

	public void setEd(SWTBotEclipseEditor ed) {
		CDIBase.ed = ed;
	}
}
