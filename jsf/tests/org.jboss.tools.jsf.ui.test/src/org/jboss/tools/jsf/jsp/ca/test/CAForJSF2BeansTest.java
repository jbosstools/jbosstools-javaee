package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAForJSF2BeansTest extends ContentAssistantTestCase {
	TestProjectProvider provider = null;
	boolean makeCopy = true;
	private static final String PROJECT_NAME = "JSF2Beans";
	private static final String PAGE_NAME = "/src/test/beans/inputname.xhtml";

	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.base.test",
				null, PROJECT_NAME, makeCopy);
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if (provider != null) {
			provider.dispose();
		}
	}

	/**
	 * JBIDE-5941
	 */
	public void testCAForJSF2Beans() {
		String[] proposals = { "mybean1", "mybean2" };

		checkProposals(PAGE_NAME, "#{myb}", 5, proposals, false);

	}

	public void testCAForMethodParameters() {
		String[] beans = {"testA", "testB"};
		String[] methodA = {"testB.addA()"};
		String[] methodB = {"testA.addB()"};

		String text = "#{testA.addB(test";
		checkProposals(PAGE_NAME, text, text.length(), beans, false);

		text = "#{testA.addB(testB.add";
		checkProposals(PAGE_NAME, text, text.length(), methodA, false);

		text = "testA.addB(testB.addA(test";
		checkProposals(PAGE_NAME, text, text.length(), beans, false);

		text = "testA.addB(testB.addA(testA.add";
		checkProposals(PAGE_NAME, text, text.length(), methodB, false);

		text = "testA.addB(testB.addA(testA.addB(test";
		checkProposals(PAGE_NAME, text, text.length(), beans, false);

	}

	public void testCAForPropertiesInBrakets() {
		String[] properties = {"mybean2['100", "mybean2['101"};

		String text1 = "#{mybean2['10']}";
		checkProposals(PAGE_NAME, text1, 13, properties, false);

		String text2 = "#{mybean2[]}";
		checkProposals(PAGE_NAME, text2, 10, properties, false);
	}
}
