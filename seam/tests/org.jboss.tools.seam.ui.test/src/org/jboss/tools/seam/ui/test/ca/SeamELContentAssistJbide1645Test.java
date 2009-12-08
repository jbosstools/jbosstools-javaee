package org.jboss.tools.seam.ui.test.ca;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.jboss.tools.common.el.ui.ca.ELProposalProcessor;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;

public class SeamELContentAssistJbide1645Test extends ContentAssistantTestCase {
	TestProjectProvider provider = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "TestSeamELContentAssist";
	private static final String PAGE_NAME = "/WebContent/home.xhtml";
	private static final String PREFIX_STRING = "<h:commandButton action=\"#{actor\" value=\"\">";
	private static final String POSTFIX_STRING = " </h:commandButton>";
	private static final String INSERT_BEFORE_STRING = "<rich:panel";
	private static final String INSERTION_STRING = PREFIX_STRING + POSTFIX_STRING;

	public static Test suite() {
		return new TestSuite(SeamELContentAssistJbide1645Test.class);
	}

	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
		Throwable exception = null;
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception x) {
			exception = x;
			x.printStackTrace();
		}
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

	public void testSeamELContentAssistJbide1645() {
		openEditor(PAGE_NAME);

		// Find start of <rich:panel> tag
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(INSERT_BEFORE_STRING));
		int offsetToTest = start + PREFIX_STRING.length();

		assertTrue("Cannot find the starting point in the test file  \"" + PAGE_NAME + "\"", (start != -1));

		String documentContentModified = documentContent.substring(0, start) +
			INSERTION_STRING + documentContent.substring(start);

		jspTextEditor.setText(documentContentModified);

		ICompletionProposal[] result= null;
		String errorMessage = null;

		IContentAssistProcessor p= TestUtil.getProcessor(viewer, offsetToTest, contentAssistant);
		if (p != null) {
			try {
				result= p.computeCompletionProposals(viewer, offsetToTest);
			} catch (Throwable x) {
				x.printStackTrace();
			}
			errorMessage= p.getErrorMessage();
		}

		assertTrue("Content Assistant peturned no proposals", (result != null && result.length > 0));

		for (int i = 0; i < result.length; i++) {
			// There should not be a proposal of type SeamELProposalProcessor.Proposal in the result
			assertFalse("Content Assistant peturned proposals of type (" + result[i].getClass().getName() + ").", (result[i] instanceof ELProposalProcessor.Proposal));
		}

		try {
			JobUtils.waitForIdle();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Waiting for the jobs to complete has failed.", false);
		} 

		closeEditor();
	}
}