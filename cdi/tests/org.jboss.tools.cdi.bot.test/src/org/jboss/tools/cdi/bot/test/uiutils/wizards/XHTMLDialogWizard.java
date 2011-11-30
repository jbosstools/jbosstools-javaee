package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;

public class XHTMLDialogWizard extends Wizard {

	private static final String XHTML_NEW_PAGE_TITLE = "New XHTML Page";
	private static final String PARENT_FOLDER_LABEL = "Enter or select the parent folder:";
	private static final String FILE_NAME_LABEL = "File name:";
	private static final String XHTML_TEMPL_CHECK_BOX = "Use XHTML Template";
	
	public XHTMLDialogWizard() {
		super(new SWTBot().activeShell().widget);
		assert (XHTML_NEW_PAGE_TITLE).equals(getText());			
	}
	
	public XHTMLDialogWizard setDestination(String destination) {
		setText(PARENT_FOLDER_LABEL, destination);
		return this;
	}
	
	public XHTMLDialogWizard setName(String nameOfPage) {
		setText(FILE_NAME_LABEL, nameOfPage);
		return this;
	}
	
	public XHTMLDialogWizard checkXHTMLTemplate() {
		checkCheckbox(XHTML_TEMPL_CHECK_BOX);
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
