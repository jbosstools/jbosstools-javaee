package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.jst.web.ui.base.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAForFaceletTaglibInJSFImplJarTest extends ContentAssistantTestCase{
	TestProjectProvider provider = null;
	private static final String PROJECT_NAME = "testJSF2Project";
	private static final String PAGE_NAME = "/WebContent/pages/inputname.xhtml";
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, true); 
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
	
	public void testCAForJSFCore(){
		String[] proposals = {
			"f:ajax"
		};

		checkProposals(PAGE_NAME, "<f:a />", 4, proposals, false);

	}

}
