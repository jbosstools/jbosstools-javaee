package org.jboss.tools.seam.ui.test.el;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.common.base.test.kb.QueryParticipantTestUtils;
import org.jboss.tools.common.base.test.kb.QueryParticipantTestUtils.MatchStructure;
import org.jboss.tools.jst.web.kb.refactoring.ELReferencesQueryParticipant;

public class ELReferencesQueryParticipantTest extends TestCase{
	IProject project = null;

	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("numberguess");
	}

	public void testELReferencesQueryParticipantForType() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();

		matches.add(new MatchStructure("/numberguess/web/giveup.jspx", "numberGuess"));
		matches.add(new MatchStructure("/numberguess/web/giveup.jspx", "numberGuess"));
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

	public void testELReferencesQueryParticipantForMethod1() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();

		matches.add(new MatchStructure("/numberguess/web/giveup.jspx", "remainingGuesses"));
		matches.add(new MatchStructure("/numberguess/web/giveup.jspx", "remainingGuesses"));

		QueryParticipantTestUtils.testSearchParticipant(project,
				"src/org/jboss/seam/example/numberguess/NumberGuess.java",
				QueryParticipantTestUtils.METHOD_SEARCH,
				"getRemainingGuesses",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}

	public void testELReferencesQueryParticipantForMethod2() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();

		matches.add(new MatchStructure("/numberguess/web/giveup.jspx", "possibilities"));
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