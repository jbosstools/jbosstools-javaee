package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAUnnecessaryElementsForDirAttributeInXHTMLPageJBIDE1813Test extends ContentAssistantTestCase{
	TestProjectProvider provider = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "JsfJspJbide1813Test";
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
	
	public void testThereAreNoUnnecessaryElementsForDirAttributeInXHTMLPageJBIDE1813(){
		
		// JBIDE-4341: the EL proposals are not to be returned (and tested) here anymore.
		//   The EL-proposals are removed from the test-list.
		//
		String[] proposals={
					"ltr",
					"rtl",
					"#{}"
		};

		checkProposals(PAGE_NAME, "dir=\"\"", 5, proposals, true);
	}
}