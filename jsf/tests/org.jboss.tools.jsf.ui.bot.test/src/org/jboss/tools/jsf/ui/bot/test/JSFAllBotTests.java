package org.jboss.tools.jsf.ui.bot.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3148and4441Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3577Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3579Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3920Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE4391Test;
import org.jboss.tools.jsf.ui.bot.test.smoke.AddRemoveJSFCapabilitiesTest;
import org.jboss.tools.jsf.ui.bot.test.smoke.CreateNewJSFProjectTest;

/**
 * 
 * This is a sample swtbot testcase for an eclipse application.
 * 
 */
public class JSFAllBotTests{
	public static Test suite(){
		TestSuite suite = new TestSuite("JSF all tests");
		suite.addTestSuite(CreateNewJSFProjectTest.class);		
		suite.addTestSuite(AddRemoveJSFCapabilitiesTest.class);
		suite.addTestSuite(JBIDE3148and4441Test.class);
		suite.addTestSuite(JBIDE4391Test.class);
		suite.addTestSuite(JBIDE3577Test.class);
		suite.addTestSuite(JBIDE3579Test.class);
		suite.addTestSuite(JBIDE3920Test.class);

		return suite;
	}
}