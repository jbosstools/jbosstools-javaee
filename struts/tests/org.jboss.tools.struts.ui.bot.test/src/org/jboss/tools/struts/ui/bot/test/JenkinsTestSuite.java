package org.jboss.tools.struts.ui.bot.test;

import org.jboss.tools.struts.ui.bot.test.smoke.CreateNewStrutsProjectTest;
import org.jboss.tools.struts.ui.bot.test.smoke.ImportStrutsProjectTest;
import org.jboss.tools.struts.ui.bot.test.smoke.RunStrutsProjectOnServer;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite of tests executed on jenkins slave 
 * @author jjankovi
 *
 */
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({
	CreateNewStrutsProjectTest.class,
	RunStrutsProjectOnServer.class,
	ImportStrutsProjectTest.class,
	
	
	/** Not stable yet
	
	RenameStrutsConfigXmlFile.class,
	RenameTldFile.class,
	AddRemoveStrutsCapabilities.class,
	TutorialTest.class,
	
	**/
	})
public class JenkinsTestSuite {
	
}
