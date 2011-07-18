package org.jboss.tools.seam.ui.bot.test.examples;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotBrowser;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.ExampleTest;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class SeamExample extends ExampleTest {

	@Override
	public String getExampleCategory() {
		return "Seam";
	}
	protected void runExample() {
		util.waitForNonIgnoredJobs(Timing.time100S());// for project build
		packageExplorer.runOnServer(getProjectNames()[0]);
		util.waitForNonIgnoredJobs();//wait for publishing
		bot.sleep(Timing.time20S());//wait for deployment
	}
	protected void checkDeployment(String url, String searchString) {
		long delay = SWTBotPreferences.TIMEOUT;
		SWTBotPreferences.TIMEOUT = delay * 4;
		SWTBotBrowser b = bot.browser();		
		b.setUrl(url);
		String page = b.getText();
		SWTBotPreferences.TIMEOUT = delay;
		assertTrue("Example was not successfully deployed, server returned :"+page,page.contains(searchString));
	}
	
	protected void execSeamTestNG(String project, String launchFile, String launchName){
		runTestNG(project, launchFile, launchName);
		SWTTestExt.util.waitForNonIgnoredJobs(Timing.time100S());
		bot.sleep(Timing.time(30*1000));		
		SWTBot ngBot = bot.viewByTitle("Results of running suite").bot();
		int k = 0;
		SWTBotCLabel l = ngBot.clabel(k);
				
		int passed = -1;
		int failed = -1;
		int skipped = -1;
		
		while ( (passed < 0) || (failed < 0) || (skipped < 0) ) {
			String txt = l.getText();
			if (txt.startsWith("Passed")) passed = Integer.valueOf(txt.split(":")[1].trim()); 
			if (txt.startsWith("Failed")) failed = Integer.valueOf(txt.split(":")[1].trim()); 
			if (txt.startsWith("Skipped")) skipped = Integer.valueOf(txt.split(":")[1].trim()); 
			k++;
			l = ngBot.clabel(k);
		}
		assertTrue("Failed test(s).", failed == 0);
		assertTrue("Skipped test(s).", skipped == 0);
		assertTrue("No passing tests.", passed > 0);
	}
	
	private void runTestNG(String project, String launchFile, final String launchName) {
		SWTBot viewBot = packageExplorer.bot();
		packageExplorer.selectProject(project);		
		SWTBotTreeItem item = packageExplorer.selectTreeItem(launchFile, new String[] {project});
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), item);
		   final SWTBotMenu menuRunAs = viewBot.menu(IDELabel.Menu.RUN).menu(IDELabel.Menu.RUN_AS);
		    final MenuItem menuItem = UIThreadRunnable
		      .syncExec(new WidgetResult<MenuItem>() {
		        public MenuItem run() {
		          int menuItemIndex = 0;
		          MenuItem menuItem = null;
		          final MenuItem[] menuItems = menuRunAs.widget.getMenu().getItems();
		          while (menuItem == null && menuItemIndex < menuItems.length){
		        	  log.info("Found item" +menuItems[menuItemIndex].getText());
		            if (menuItems[menuItemIndex].getText().indexOf(launchName) > - 1){
		              menuItem = menuItems[menuItemIndex];
		            }
		            else{
		              menuItemIndex++;
		            }
		          }
		        return menuItem;
		        }
		      });
		    if (menuItem != null){
		      new SWTBotMenu(menuItem).click();
		    }
		    else{
		      throw new WidgetNotFoundException(String.format("Unable to find Menu Item with Label '%s'",launchName));
		    }
		
	}
}
