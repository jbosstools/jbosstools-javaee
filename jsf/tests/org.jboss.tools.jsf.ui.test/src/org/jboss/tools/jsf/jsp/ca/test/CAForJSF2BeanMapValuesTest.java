package org.jboss.tools.jsf.jsp.ca.test;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.web.ui.internal.editor.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAForJSF2BeanMapValuesTest  extends ContentAssistantTestCase {
	TestProjectProvider provider = null;
	boolean makeCopy = true;
	private static final String PROJECT_NAME = "JSF2Beans";
	private static final String PAGE_NAME = "/src/test/beans/inputname.xhtml";
	private static final String EL_TO_FIND = "#{myBean.myMap['100'].si";
	private static final String PROPOSAL_TO_TEST = "myBean.myMap['100'].size()";
	private static final String PROPOSAL_TO_APPLY = "size()";
	
	private static final String CURSOR_SIGNATURE = "<The cursor point>";


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
	 * JBIDE-6135
	 */
	public void testForJSF2BeanMapValues() {
		
		String[] proposals = { PROPOSAL_TO_TEST };

		checkProposals(PAGE_NAME, EL_TO_FIND, 24, proposals, false);
	}

	/**
	 * JBIDE-12441
	 */
	public void testCursorPositionAfterApplyMethodProposalOnELWithSquareBrackets() {

		openEditor(PAGE_NAME);
		try {

			assertNotNull("Text Viewer not found", getViewer());
			IDocument document = getViewer().getDocument();
			assertNotNull("Can't obtain a test Document.", document);

			String documentContent = document.get();
			int start = (documentContent == null ? -1 : documentContent
					.indexOf(EL_TO_FIND));
			assertFalse("Required text '" + EL_TO_FIND
					+ "' not found in document", (start == -1));

			int end = (documentContent == null ? -1 : documentContent
					.indexOf('}', start));
			assertFalse("Required text '}' not found in document", (end == -1));

			int offsetToTest = start + EL_TO_FIND.length();

			List<ICompletionProposal> res = CATestUtil.collectProposals(
					getContentAssistant(), getViewer(), offsetToTest);

			assertTrue("Content Assistant returned no proposals",
					(res != null && res.size() > 0));

			boolean bPropoosalToApplyFound = false;
			for (ICompletionProposal p : res) {
				if (!(p instanceof AutoContentAssistantProposal))
					continue;
				AutoContentAssistantProposal proposal = (AutoContentAssistantProposal) p;
				String proposalString = proposal.getDisplayString();

				if (proposalString.startsWith(PROPOSAL_TO_APPLY)) {
					bPropoosalToApplyFound = true;
					proposal.apply(document);

					// The following is copied from CompletionProposalPopup
					// class that actually applies the proposal.
					// Node that fContentAssistSubjectControlAdapter object
					// is replaced by the Viewer object (which is equivalent
					// in many cases).
					// So, after the proposal is applied a new selection is
					// set in the Editor:
					Point selection = p.getSelection(document);
					if (selection != null) {
						getViewer().setSelectedRange(selection.x,
								selection.y);
						getViewer().revealRange(selection.x, selection.y);
					}
					// End of code from CompletionProposalPopup

					break;
				}
			}
			assertTrue("The proposal to apply not found.",
					bPropoosalToApplyFound);

			try {
				JobUtils.waitForIdle();
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue("Waiting for the jobs to complete has failed.",
						false);
			}

			Point s = getViewer().getSelectedRange();
			assertNotNull("Selection can't be obtained from the editor!", s);

			String documentUpdatedContent = document.get();
			String testUpdatedContent = documentUpdatedContent.substring(0,
					s.x)
					+ CURSOR_SIGNATURE
					+ documentUpdatedContent.substring(s.x);
			String testString = EL_TO_FIND.substring(0,
					EL_TO_FIND.lastIndexOf('.') + 1)
					+ PROPOSAL_TO_APPLY
					+ CURSOR_SIGNATURE;
			
//			System.out.println("testString: [" + testString + "]");
//			System.out.println("testUpdatedContent: [" + testUpdatedContent.substring(start,
//					s.x + CURSOR_SIGNATURE.length()) + "]");
			
			assertTrue(
					"The proposal replacement is failed.",
					testUpdatedContent.substring(start,
							s.x + CURSOR_SIGNATURE.length()).equals(
							testString));
		} finally {
			closeEditor();
		}
	}

}
