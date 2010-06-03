package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAForUnclosedELTest extends ContentAssistantTestCase{
	TestProjectProvider provider = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "CAForELinStyleTest";
	private static final String PAGE_NAME = "/WebContent/pages/greeting.xhtml";
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
	
	public void testCAForUnclosedELTest(){
		String[] proposals = {
			"person",
		};

		checkProposals(PAGE_NAME, "<ui:define name=\"pageTitle#{  \"", 30, proposals, false);
		checkProposals(PAGE_NAME, "<ui:define name=\"pageHeader\">Greeting Page#{   </ui:define>", 46, proposals, false);
	}
}