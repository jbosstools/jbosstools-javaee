package org.jboss.tools.seam.ui.test.el;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.QualifiedName;
import org.jboss.tools.jst.web.kb.refactoring.ELReferencesQueryParticipant;
import org.jboss.tools.jst.web.kb.test.QueryParticipantTestUtils;
import org.jboss.tools.jst.web.kb.test.QueryParticipantTestUtils.MatchStructure;
import org.jboss.tools.test.util.TestProjectProvider;

public class ELReferencesQueryParticipantTest extends TestCase{
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "numberguess";
	
	public static final QualifiedName IS_KB_NATURES_CHECK_NEED = new QualifiedName(
			"", "Is KB natures check"); //$NON-NLS-1$
	public static final QualifiedName IS_JSF_NATURES_CHECK_NEED = new QualifiedName(
			"", "Is JSF natures check"); //$NON-NLS-1$
	public static final QualifiedName IS_JSF_CHECK_NEED = new QualifiedName(
			"", "Is JSF check"); //$NON-NLS-1$
	private String isKbNatureCheck = null;
	private String isJsfNatureCheck = null;
	private String isJsfCheck = null;
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", "projects/" + PROJECT_NAME, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
		Throwable exception = null;
		
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);
		if (project != null) {
			isKbNatureCheck = project.getPersistentProperty(IS_KB_NATURES_CHECK_NEED);
			project.setPersistentProperty(IS_KB_NATURES_CHECK_NEED, //$NON-NLS-1$
					Boolean.toString(false));
			isJsfNatureCheck = project.getPersistentProperty(IS_JSF_NATURES_CHECK_NEED);
			project.setPersistentProperty(IS_JSF_NATURES_CHECK_NEED, //$NON-NLS-1$
					Boolean.toString(false));
			isJsfCheck = project.getPersistentProperty(IS_JSF_CHECK_NEED);
			project.setPersistentProperty(IS_JSF_CHECK_NEED, //$NON-NLS-1$
					Boolean.toString(false));
		}
	}

	protected void tearDown() throws Exception {
		if (project != null) {
			project.setPersistentProperty(IS_KB_NATURES_CHECK_NEED, //$NON-NLS-1$
					isKbNatureCheck);
			project.setPersistentProperty(IS_JSF_NATURES_CHECK_NEED, //$NON-NLS-1$
					isJsfNatureCheck);
			project.setPersistentProperty(IS_JSF_CHECK_NEED, //$NON-NLS-1$
					isJsfCheck);
		}
		if(provider != null) {
			provider.dispose();
		}
	}
	
	public void testELReferencesQueryParticipantForType(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/numberguess/web/giveup.jspx", "numberGuess"));
		matches.add(new MatchStructure("/numberguess/web/giveup.jspx", "numberGuess"));
		
		QueryParticipantTestUtils.testSearchParticipant(project,
				"src/org/jboss/seam/example/numberguess/NumberGuess.java",
				QueryParticipantTestUtils.TYPE_SEARCH,
				"NumberGuess",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}
	
	public void testELReferencesQueryParticipantForMethod1(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/numberguess/web/giveup.jspx", "remainingGuesses"));
		
		QueryParticipantTestUtils.testSearchParticipant(project,
				"src/org/jboss/seam/example/numberguess/NumberGuess.java",
				QueryParticipantTestUtils.METHOD_SEARCH,
				"getRemainingGuesses",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}

	public void testELReferencesQueryParticipantForMethod2(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/numberguess/web/giveup.jspx", "possibilities"));
		
		QueryParticipantTestUtils.testSearchParticipant(project,
				"src/org/jboss/seam/example/numberguess/NumberGuess.java",
				QueryParticipantTestUtils.METHOD_SEARCH,
				"getPossibilities",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}

}
