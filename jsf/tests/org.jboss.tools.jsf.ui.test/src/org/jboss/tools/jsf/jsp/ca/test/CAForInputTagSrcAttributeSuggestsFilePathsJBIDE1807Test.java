package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAForInputTagSrcAttributeSuggestsFilePathsJBIDE1807Test extends ContentAssistantTestCase{
	TestProjectProvider provider = null;
	boolean makeCopy = true;
	private static final String PROJECT_NAME = "JsfJspJbide1807Test";
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
	
	public void testCAForInputTagSrcAttributeSuggestsFilePathsJBIDE1807(){
		
		// JBIDE-4341: the EL proposals are not to be returned (and tested) here anymore.
		//  - The EL-proposals are removed from the test-list.
		//  - The "/pages" proposal is added as the main case to test 
		String[] proposals={
					"/pages/",
					"/templates/",
					"#{}"
		};

		checkProposals(PAGE_NAME, "<input type=\"image\" src=\"", 25, proposals, true, true);
	}
}