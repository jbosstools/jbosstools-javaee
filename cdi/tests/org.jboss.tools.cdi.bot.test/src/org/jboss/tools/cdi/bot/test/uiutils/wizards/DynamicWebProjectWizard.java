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

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.Timing;

public class DynamicWebProjectWizard extends Wizard {

	private final String PROJECT_NAME = "Project name:";
	private final String CDI_PRESET = "Dynamic Web Project " +
			"with CDI (Context and Dependency Injection)";
	private final String CDI_FACET = "CDI (Contexts and Dependency Injection)";
	private final String CONFIGURATION = "Configuration";
	private final String PROJECT_FACETS = "Project Facets";
	
	public DynamicWebProjectWizard() {
		super(new SWTBot().activeShell().widget);
		assert "New Dynamic Web Project".equals(getText());
	}

	public DynamicWebProjectWizard setProjectName(String name) {
		setText(PROJECT_NAME, name);
		return this;
	}
	
	public DynamicWebProjectWizard setCDIPreset() {
		bot().comboBoxInGroup(CONFIGURATION, 0).
			setSelection(CDI_PRESET);			
		return this;
	}
	
	public DynamicWebProjectWizard setCDIFacet() {
		clickButton("Modify...");		
		setCDIFacetInFacets(bot());
		bot().sleep(Timing.time1S());
		return this;
	}
	
	private void setCDIFacetInFacets(SWTBot bot) {
		assertTrue(bot.activeShell().getText().equals(PROJECT_FACETS));
		SWTBot facetsBot = bot.activeShell().bot();
		SWTBotTree tree= facetsBot.tree();
		for (SWTBotTreeItem ti: tree.getAllItems())  {							
			if (ti.cell(0).contains(CDI_FACET)) {				
				ti.check();
				break;
			}
		}
		facetsBot.sleep(Timing.time1S());
		facetsBot.button("OK").click();
	}

}
