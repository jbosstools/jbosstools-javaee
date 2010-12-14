package org.jboss.tools.seam.ui.bot.test.create;

import java.util.Properties;

import org.jboss.tools.seam.ui.bot.test.AbstractSeamTestBase;
import org.jboss.tools.seam.ui.bot.test.EARTests;
import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.seam.ui.bot.test.WARTests;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.test.SWTJBTBot;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class DeleteSeamProjects extends AbstractSeamTestBase {
  
	protected static final String VALIDATION = "Validation";
	protected static final String DEPLOY_SOURCE = "Deploying datasource to server";
	protected static final String REG_IN_SERVER = "Register in server";
	
  public DeleteSeamProjects() {
	}

    private SWTJBTExt swtJbtExt = new SWTJBTExt(bot);
	
    @Test
	@Category(WARTests.class)
	public void testDeleteSeamProjectWar(){
		swtJbtExt.deleteProject(testProjectName + TestControl.TYPE_WAR);
		swtJbtExt.deleteProject(testProjectName + TestControl.TYPE_WAR + "-test");
	}
	
    @Test
	@Category(EARTests.class)
	public void testDeleteSeamProjectEar(){
		swtJbtExt.deleteProject(testProjectName + TestControl.TYPE_EAR);
		swtJbtExt.deleteProject(testProjectName + TestControl.TYPE_EAR + "-ear");
		swtJbtExt.deleteProject(testProjectName + TestControl.TYPE_EAR + "-ejb");
		swtJbtExt.deleteProject(testProjectName + TestControl.TYPE_EAR + "-test");
	}


}