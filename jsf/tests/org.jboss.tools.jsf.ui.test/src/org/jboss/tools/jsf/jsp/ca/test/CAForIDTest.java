package org.jboss.tools.jsf.jsp.ca.test;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAForIDTest extends ContentAssistantTestCase{
	TestProjectProvider provider = null;
	boolean makeCopy = true;
	private static final String PROJECT_NAME = "CAForIDTest";
	private static final String PAGE_NAME = "/WebContent/pages/inputUserName.jsp";
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
	
	public void _testCAForIDTest(){
		String[] proposals = {
			"greetingForm",
		};

		checkProposals(PAGE_NAME, "<a4j:commandButton focus=\"\"/>", 26, proposals, false, false);
	}

	public void testCAForConverterIDTest(){
		String[] proposals = {
			"converter1",
		};

		checkProposals(PAGE_NAME, "<h:inputText value=\"#{user.name}\" required=\"true\" converter=\"\">", 61, proposals, false, false);
	}

	public void testCAForValidatorIDTest(){
		String[] proposals = {
			"validator1",
		};

		checkProposals(PAGE_NAME, "<f:validator validatorId=\"\" />", 26, proposals, false, false);
	}
}