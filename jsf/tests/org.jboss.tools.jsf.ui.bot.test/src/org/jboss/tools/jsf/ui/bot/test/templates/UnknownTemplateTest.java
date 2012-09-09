package org.jboss.tools.jsf.ui.bot.test.templates;

import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.vpe.ui.bot.test.tools.SWTBotWebBrowser;

public class UnknownTemplateTest extends JSFAutoTestCase {
	
	public void testUnknownTemplate() throws Throwable{
		
		openTestPage();
		
		setEditor(bot.editorByTitle(TEST_PAGE).toTextEditor());
		setEditorText(getEditor().getText());
		
		getEditor().navigateTo(13, 0);
		
		final String unknownTag = "h:unknowntag";
		
		getEditor().insertText("<" + unknownTag + "></" + unknownTag + "h:unknowntag>"); //$NON-NLS-1$
		getEditor().save();
		waitForBlockingJobsAcomplished(VISUAL_UPDATE);
    
		assertVisualEditorContainsNodeWithValue(new SWTBotWebBrowser(TEST_PAGE, new SWTBotExt()),
		    unknownTag,
		    TEST_PAGE);
		
	}
	
}
