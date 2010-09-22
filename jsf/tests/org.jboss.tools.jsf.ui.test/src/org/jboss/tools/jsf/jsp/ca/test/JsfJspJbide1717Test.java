package org.jboss.tools.jsf.jsp.ca.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.TestProjectProvider;


public class JsfJspJbide1717Test extends ContentAssistantTestCase {
	TestProjectProvider provider = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "JsfJbide1704Test";
	private static final String PAGE_NAME = "/WebContent/pages/greeting.jsp";
	private static final String INSERT_BEFORE_STRING = "<h:outputText";
	private static final String INSERTION_BEGIN_STRING = "<h:outputText value=\"";
	private static final String INSERTION_END_STRING = "\"  />";
	private static final String JSF_EXPR_STRING = "#{msg.greeting}";
	
	public static Test suite() {
		return new TestSuite(JsfJspJbide1717Test.class);
	}

	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

	public void testJstJspJbide1717() {
		openEditor(PAGE_NAME);

		// Find start of <h:outputText> tag
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(INSERT_BEFORE_STRING));

		assertTrue("Cannot find the starting point in the test file  \"" + PAGE_NAME + "\"", (start != -1));
		
		// First of all perform the test on a region placed in one space behind empty-valued attribute - 
		// this is to return normal list of attribute names proposal list 
		
		String documentContentModified = documentContent.substring(0, start) +
			INSERTION_BEGIN_STRING + INSERTION_END_STRING + documentContent.substring(start);
		
		int offsetToTest = start + INSERTION_BEGIN_STRING.length() + 2;
		
		jspTextEditor.setText(documentContentModified);
		
		String errorMessage = null;

		List<ICompletionProposal> res = TestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

		List<String> customCompletionProposals = new ArrayList<String>();
		
		if (res != null) {
			for (ICompletionProposal proposal : res) {
				// There should be at least one proposal of type CustomCompletionProposal in the result
				if (proposal instanceof CustomCompletionProposal) {
					customCompletionProposals.add(((CustomCompletionProposal)proposal).getReplacementString());
				}
			}
		}
		assertFalse("Content Assistant returned no proposals of type CustomCompletionProposal.",customCompletionProposals.isEmpty());

		try {
			JobUtils.waitForIdle();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Waiting for the jobs to complete has failed.", false);
		} 

		// Next perform the test on a region placed in one space behind an attribute those value is a container
		// (contains JSF expression) - this has to return the same normal list of attribute names proposal list as
		// we got at the first step (because the tag is the same, but only the attribute value is changed) 
		
		documentContentModified = documentContent.substring(0, start) +
				INSERTION_BEGIN_STRING + JSF_EXPR_STRING + INSERTION_END_STRING + documentContent.substring(start);
	
		offsetToTest = start + INSERTION_BEGIN_STRING.length() + JSF_EXPR_STRING.length() + 2;
	
		String visualizeCursorPosition = documentContentModified.substring(0, offsetToTest) +
			"|" + documentContentModified.substring(offsetToTest);

		jspTextEditor.setText(documentContentModified);

		res = TestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

		if (res != null) {
			for (ICompletionProposal proposal : res) {
				// There should be the same proposals as in the saved result
				if (proposal instanceof CustomCompletionProposal) {
					assertTrue("Content Assistant returned additional proposal (proposal returned doesn't exist in the saved list).",
							customCompletionProposals.contains(((CustomCompletionProposal)proposal).getReplacementString()));
					customCompletionProposals.remove(((CustomCompletionProposal)proposal).getReplacementString());
				}
			}
		}
		assertTrue("Content Assistant didn't returned some proposals.",customCompletionProposals.isEmpty());

		closeEditor();
	}


}
