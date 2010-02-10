package org.jboss.tools.jsf.ui.bot.test.templates;

import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;

public class UnknownTemplateTest extends JSFAutoTestCase {
	
	public void testUnknownTemplate() throws Throwable{
		
		openTestPage();
		
		setEditor(bot.editorByTitle(TEST_PAGE).toTextEditor());
		setEditorText(getEditor().getText());
		
		getEditor().navigateTo(13, 0);
		
		getEditor().insertText("<h:unknowntag></h:unknowntag>"); //$NON-NLS-1$
		
		checkVPE("templates/UnknownTemplate.xml"); //$NON-NLS-1$
		
	}
	
}
