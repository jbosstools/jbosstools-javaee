package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAVarAttributeForDataTableTagJBIDE2016 extends ContentAssistantTestCase{
	TestProjectProvider provider = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "JsfJbide2016Test";
	private static final String PAGE_NAME = "/WebContent/pages/greeting.jsp";
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
	
	public void testThereIsVarAttributeForDataTableTagProposalsJBIDE2016(){
		String[] proposals = {
			"user.name",
		};

		checkProposals(PAGE_NAME, "value=\"#{user.}\"", 14, proposals, false);
	}
}