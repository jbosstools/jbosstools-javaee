package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAForCompositeComponentTest extends ContentAssistantTestCase{
	TestProjectProvider provider = null;
	boolean makeCopy = true;
	private static final String PROJECT_NAME = "CAForCompositeComponentTest";
	private static final String PAGE_NAME = "/WebContent/pages/greeting.xhtml";
	private static final String TAG_NAME = "/WebContent/resources/sample/tag2.xhtml";
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
	
	public void testCAForCompositeComponent(){
		String[] proposals = {
			"sample:tag", "sample:tag2", "sample:tag3"
		};

		checkProposals(PAGE_NAME, "<sample:tag />", 8, proposals, false);

		proposals = new String[]{
			"aaa"
		};
		checkProposals(PAGE_NAME, "<sample:tag />", 12, proposals, false);
	}

	/**
	 * JBIDE-5941
	 */
	public void testCAForCCAttrs(){
		String[] proposals = {
			"cc.attrs.a2", "cc.attrs.b3", "cc.attrs.onclick"
		};

		checkProposals(TAG_NAME, "#{cc.attrs.}", 11, proposals, false);

	}

	public void testCAForTypedAttr() {
		String[] proposals = {
			"cc.attrs.b3.toString()"
		};
		checkProposals(TAG_NAME, "#{cc.attrs.b3.}", 14, proposals, false);
	}
}
