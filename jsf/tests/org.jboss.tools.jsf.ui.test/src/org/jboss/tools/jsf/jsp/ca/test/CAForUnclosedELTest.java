package org.jboss.tools.jsf.jsp.ca.test;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
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

	public void testCAInELStartToken() throws BadLocationException {
		openEditor(PAGE_NAME);
		
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0, "<ui:define name=\"pageTitle#{  \"", true, true, false, false);
		List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, reg.getOffset() + 27);
		boolean found = compareProposal("person", null, res.toArray(new ICompletionProposal[0]));
		assertFalse(found);
		res = CATestUtil.collectProposals(contentAssistant, viewer, reg.getOffset() + 28);
		found = compareProposal("person", null, res.toArray(new ICompletionProposal[0]));
		assertTrue(found);
	}
}