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
import org.jboss.tools.cdi.bot.test.uiutils.BeansXMLHelper;
import org.jboss.tools.cdi.bot.test.uiutils.CDIProjectHelper;
import org.jboss.tools.cdi.bot.test.uiutils.CDIWizardHelper;
import org.jboss.tools.cdi.bot.test.uiutils.EditorResourceHelper;
import org.jboss.tools.cdi.bot.test.uiutils.OpenOnHelper;
import org.jboss.tools.cdi.bot.test.uiutils.ProjectImportHelper;
import org.jboss.tools.cdi.bot.test.uiutils.QuickFixHelper;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardBaseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;

public class CDIBase extends SWTTestExt {

	private static SWTBotEclipseEditor ed;
	public static final CDIProjectHelper projectHelper = new CDIProjectHelper(); 
	public static final BeansXMLHelper beansHelper = new BeansXMLHelper();
	public static final CDIWizardHelper wizard = new CDIWizardHelper();
	public static final CDIWizardBaseExt wizardExt = new CDIWizardBaseExt();
	public static final OpenOnHelper openOnUtil = new OpenOnHelper();
	public static final EditorResourceHelper editResourceUtil = new EditorResourceHelper();
	public static final QuickFixHelper quickFixHelper = new QuickFixHelper();
	public static final ProjectImportHelper projectImportHelper = new ProjectImportHelper();
	
	
	public SWTBotEclipseEditor getEd() {
		return ed;
	}

	public void setEd(SWTBotEclipseEditor ed) {
		CDIBase.ed = ed;
	}
}
