package org.jboss.tools.cdi.bot.test.fix;

import java.util.logging.Logger;

import junit.framework.Assert;

import org.eclipse.swtbot.eclipse.finder.SWTEclipseBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.jboss.tools.cdi.bot.test.editor.BeansEditorTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/*
 * Test operates on quick fixes of CDI components
 * 
 * @author Jaroslav Jankovic
 */

@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ BeansEditorTest.class })
public class QuickFixTest extends SWTBotExt{
	
	private static final Logger LOGGER = Logger.getLogger(QuickFixTest.class.getName());
	
	
	@Test
	public void testQuickFix(){
		
	}
	

}
