package org.jboss.tools.batch.ui.itest.ca;

public class DeciderArtifactRefContentAssist extends ContentAssistantTestCase {
	
	private static final String FILE_NAME = "/src/META-INF/batch-jobs/decider-ca.xml";
	
	private static final String TEXT_TO_FIND_DECIDER = "<decision ref=\"my";
	
	private static final String[] PROPOSALS_DECIDER = {
			"mydecider", "myDecider1", "myDecider2", "myNamedDecider3", "batch.MyDecider4"
		};
	
	public void testJobListener() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_DECIDER, 
				TEXT_TO_FIND_DECIDER.length() - 2, 
				PROPOSALS_DECIDER, 
				true);
	}
	
	

}
