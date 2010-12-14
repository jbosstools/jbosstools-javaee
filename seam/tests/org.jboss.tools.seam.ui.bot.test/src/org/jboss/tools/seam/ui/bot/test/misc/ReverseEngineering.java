package org.jboss.tools.seam.ui.bot.test.misc;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.seam.ui.bot.test.AbstractSeamTestBase;
import org.jboss.tools.seam.ui.bot.test.EARTests;
import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.seam.ui.bot.test.WARTests;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.view.ProjectExplorer;
import org.jboss.tools.ui.bot.test.SWTJBTBot;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class ReverseEngineering extends AbstractSeamTestBase {

	private static final String[] ENTITIES = new String[] {
		"Customers", "Employees", "Offices", "Orderdetails", "Orders", "Payments", "Productlines", "Products"	
	};
	
    @Test
	@Category(WARTests.class)
	public void testRevEngFromDBWar() {
    	testRevEngFromDB(TestControl.TYPE_WAR);		
	}

    @Test
	@Category(EARTests.class)
	public void testRevEngFromDBEar() {
    	testRevEngFromDB(TestControl.TYPE_EAR);		
	}
    
	private void testRevEngFromDB(String type) {
		
		// launch reveng
		
		SWTJBTBot bot = new SWTJBTBot();
		bot.menu("New").menu("Seam Generate Entities").click();
		bot.text().setText(AbstractSeamTestBase.testProjectName + type);
		bot.radio("Reverse engineer from database").click();
		bot.button("Next >").click();
		bot.button("Refresh").click();
		
		SWTBotTree schemaTree = bot.tree();
		schemaTree.select("<Default caalog>", "PUBLIC");
		
		bot.button("Include...").click();
		bot.button("Finish").click();
		
		// wait for reveng to complete
		SWTTestExt.util.waitForNonIgnoredJobs(60000);	

		// check that the entities are present
		projectExplorer.show();		
		String[] path;
		if (type == TestControl.TYPE_EAR)		
			path = new String[] {testProjectName + type + "-ejb", 
					"ejbModule", "org.domain.seamprjear.entity"};
		else
			path = new String[] {testProjectName + type, 
					"src", "main", "org", "domain", "seamprjwar", "session"};

		for (String entity: ENTITIES) {
			projectExplorer.selectTreeItem(entity + ".java", path);
		}
		
		// check that associated web pages are present 
		path = new String[] {testProjectName + type , "Web Resources : WebContent"};
		for (String entity: ENTITIES) {
			projectExplorer.selectTreeItem(entity + ".page.xml", path);
			projectExplorer.selectTreeItem(entity + ".xhtml", path);
			projectExplorer.selectTreeItem(entity + "Edit.page.xml", path);
			projectExplorer.selectTreeItem(entity + "Edit.xhtml", path);
			projectExplorer.selectTreeItem(entity + "List.page.xml", path);
			projectExplorer.selectTreeItem(entity + "List.xhtml", path);
		}
		
	}
	
	
}
