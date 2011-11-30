package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class CDIRefactorWizard extends Wizard{

	private List<String> affectedFiles;
	
	public CDIRefactorWizard() {		 
		super(new SWTBot().activeShell().widget);
		assert ("Refactoring").equals(getText());
		affectedFiles = new ArrayList<String>();
	}

	public CDIRefactorWizard setName(String name) {				
		setText("@Named Bean Name", name);
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
		String temp = null;
		for (SWTBotTreeItem ti : bot().tree().getTreeItem("Rename @Named Bean").getItems()) {
			temp = ti.getText().split("-")[0];
			affectedFiles.add(temp.substring(0, temp.length() - 1));
		}
		return affectedFiles;
	}
	
	
	
}
