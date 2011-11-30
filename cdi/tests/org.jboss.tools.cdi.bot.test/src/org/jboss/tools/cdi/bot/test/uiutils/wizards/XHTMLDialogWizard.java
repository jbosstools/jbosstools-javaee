package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;

public class XHTMLDialogWizard extends Wizard {

	public XHTMLDialogWizard() {
		super(new SWTBot().activeShell().widget);
		assert ("New XHTML Page").equals(getText());			
	}
	
	public XHTMLDialogWizard setDestination(String destination) {
		setText("Enter or select the parent folder:", destination);
		return this;
	}
	
	public XHTMLDialogWizard setName(String nameOfPage) {
		setText("File name:", nameOfPage);
		return this;
	}
	
	public XHTMLDialogWizard checkXHTMLTemplate() {
		checkCheckbox("Use XHTML Template");
		return this;
	}
	
	public XHTMLDialogWizard setXHTMLTemplate(SWTBotTableItem template) {
		template.select();
		return this;
	}
	
	public SWTBotTableItem[] getTemplates() {
		SWTBotTable templateTable = bot().table(0);
		SWTBotTableItem[] templates = new SWTBotTableItem[templateTable.rowCount()];
		for (int i = 0; i < templateTable.rowCount(); i++) {
			templates[i] = templateTable.getTableItem(i);
		}
		return templates;
	}

}
