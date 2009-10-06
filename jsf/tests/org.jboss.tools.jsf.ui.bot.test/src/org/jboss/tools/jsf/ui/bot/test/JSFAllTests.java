package org.jboss.tools.jsf.ui.bot.test;

import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3148and4441Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3577Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3579Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE3920Test;
import org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide.JBIDE4391Test;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;



/**
 * 
 * This is a sample swtbot testcase for an eclipse application.
 * 
 */
public class JSFAllTests extends SWTBotTestCase{
	public static Test suite(){
		TestSuite suite = new TestSuite("CSS dialog JBIDE tests");
		suite.addTestSuite(JBIDE3148and4441Test.class);
		suite.addTestSuite(JBIDE4391Test.class);
		suite.addTestSuite(JBIDE3577Test.class);
		suite.addTestSuite(JBIDE3579Test.class);
		suite.addTestSuite(JBIDE3920Test.class);
		return new TestSetup(suite);
	}
}