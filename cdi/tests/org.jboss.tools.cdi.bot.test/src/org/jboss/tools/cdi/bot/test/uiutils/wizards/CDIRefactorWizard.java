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
package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class CDIRefactorWizard extends Wizard{

	private List<String> affectedFiles;
	private static final String NAMED_TEXT_LABEL = "@Named Bean Name";
	private static final String RENAME_TEXT_LABEL = "Rename @Named Bean";
	
	public CDIRefactorWizard() {		 
		super(new SWTBot().activeShell().widget);
		assert ("Refactoring").equals(getText());
		affectedFiles = new ArrayList<String>();
	}

	public CDIRefactorWizard setName(String name) {				
		setText(NAMED_TEXT_LABEL, name);
		return this;		
	}
	
	public CDIRefactorWizard next() {
		clickButton("Next >");
		return this;
	}

	/**
	 * Method gets all files which will be affected by CDI refactoring
	 * @return
	 */
	public List<String> getAffectedFiles() {		
		for (SWTBotTreeItem ti : bot().tree().getTreeItem(RENAME_TEXT_LABEL).getItems()) {			
			affectedFiles.add(ti.getText().split(" - ")[0]); // remove package offset
		}
		return affectedFiles;
	}
	
	
	
}
