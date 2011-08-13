package org.jboss.tools.jsf.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.jst.web.kb.refactoring.ELReferencesQueryParticipant;
import org.jboss.tools.jst.web.kb.test.QueryParticipantTestUtils;
import org.jboss.tools.jst.web.kb.test.QueryParticipantTestUtils.MatchStructure;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class ELReferencesQueryParticipantTest extends TestCase{
	static String projectName = "JSF2ComponentsValidator";
	static IProject project;
	
	public ELReferencesQueryParticipantTest(){
		super("ELReferencesQueryParticipantTest");
	}
	
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(projectName);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle(2000);
	}
	
	public void testELReferencesQueryParticipantForType() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/JSF2ComponentsValidator/WebContent/pages/greeting.xhtml", "person"));
		matches.add(new MatchStructure("/JSF2ComponentsValidator/WebContent/resources/demo/input.xhtml", "person"));
		matches.add(new MatchStructure("/JSF2ComponentsValidator/WebContent/resources/demo/input.xhtml", "person"));
		
		QueryParticipantTestUtils.testSearchParticipant(project,
				"JavaSource/demo/Person.java",
				QueryParticipantTestUtils.TYPE_SEARCH,
				"Person",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}
	
	public void testELReferencesQueryParticipantForMethod() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/JSF2ComponentsValidator/WebContent/pages/greeting.xhtml", "name"));
		matches.add(new MatchStructure("/JSF2ComponentsValidator/WebContent/resources/demo/input.xhtml", "name"));
		
		QueryParticipantTestUtils.testSearchParticipant(project,
				"JavaSource/demo/Person.java",
				QueryParticipantTestUtils.METHOD_SEARCH,
				"getName",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}
}
