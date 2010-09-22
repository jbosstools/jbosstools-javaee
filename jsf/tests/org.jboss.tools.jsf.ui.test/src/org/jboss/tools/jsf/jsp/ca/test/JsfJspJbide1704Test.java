package org.jboss.tools.jsf.jsp.ca.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.jboss.tools.jst.jsp.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class JsfJspJbide1704Test extends ContentAssistantTestCase {
	TestProjectProvider provider = null;
	
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "JsfJbide1704Test";
	private static final String PAGE_NAME = "/WebContent/pages/greeting";
	
	public static Test suite() {
		return new TestSuite(JsfJspJbide1704Test.class);
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

	public void testJspJbide1704 () {
		assertTrue("Test project \"" + PROJECT_NAME + "\" is not loaded", (project != null));
		doTestJsfJspJbide1704(PAGE_NAME + ".jsp");
	}
	
	public void testXhtmlJbide1704 () {
		assertTrue("Test project \"" + PROJECT_NAME + "\" is not loaded", (project != null));
		doTestJsfJspJbide1704(PAGE_NAME + ".xhtml");
	}
	
	private static final String TEST_RESOURCES_VALUE = "\"resources\"";
	
	private void doTestJsfJspJbide1704(String pageName) {

		openEditor(pageName);
		
		try {
			
			final IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
					" var=\"msg\"", true, true, false, false);
			String text = document.get();
			String errorMessage = null;

			List<ICompletionProposal> res = TestUtil.collectProposals(contentAssistant, viewer, reg.getOffset());
			
			assertTrue("Content Assist returned no proposals: ", (res != null && res.size() > 0));
			
			for (ICompletionProposal proposal : res) {
				// There should not be a proposal of type AutoContentAssistantProposal in the result
				// (the only exclusion is EL-proposals)
				
				if (proposal instanceof AutoContentAssistantProposal) {
					if(((AutoContentAssistantProposal)proposal).getReplacementString().startsWith("#{")) {
						// The only EL template proposal is allowed to be shown here
						continue;
					}
				}
				
				
				if (proposal instanceof CustomCompletionProposal) {
					// There are two cases are allowed to be shown
					// AutoContentAssistantProposal which returns the "resources" string as replacement 
					// CustomCompletionProposal which returns the current value string as replacement
					
					if (!(proposal instanceof AutoContentAssistantProposal)) {
						int equalSignIndex = text.lastIndexOf('=', reg.getOffset());
						if (equalSignIndex != -1) {
							String prevAttrValue = text.substring(equalSignIndex+1, reg.getOffset()).trim();					
							if (((CustomCompletionProposal)proposal).getReplacementString().equals(prevAttrValue)){
								// The old value for the attribute is allowed to be shown here
								continue;
							}
						}
					} else {
						if (((CustomCompletionProposal)proposal).getReplacementString().equals(TEST_RESOURCES_VALUE)){
							// The old value for the attribute is allowed to be shown here
							continue;
						}
						
					}
				}
				
				assertFalse("Content Assistant peturned proposals of type (" + proposal.getClass().getName() + ").", (proposal instanceof AutoContentAssistantProposal));
			}
	
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		
		closeEditor();
	}

}
