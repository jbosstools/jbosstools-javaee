package org.jboss.tools.seam.core.test.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.j2ee.internal.common.classpath.J2EEComponentClasspathUpdater;
import org.jboss.tools.common.base.test.AbstractRefactorTest;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class SeamRefactoringTest  extends AbstractRefactorTest {
	static String warProjectName = "Test1";
	static String earProjectName = "Test1-ear";
	static String ejbProjectName = "Test1-ejb";
	static IProject warProject;
	static IProject earProject;
	static IProject ejbProject;
	static ISeamProject seamWarProject;
	static ISeamProject seamEjbProject;
	
	public SeamRefactoringTest(String name){
		super(name);
	}
	
	protected void setUp() throws Exception {
		loadProjects();
		List<IProject> projectList = new ArrayList<IProject>();
		projectList.add(ejbProject);
		projectList.add(warProject);
		J2EEComponentClasspathUpdater.getInstance().forceUpdate(projectList);
		loadProjects();
	}

	private void loadProjects() throws Exception {
		earProject = ProjectImportTestSetup.loadProject(earProjectName);
		earProject.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		ejbProject = ProjectImportTestSetup.loadProject(ejbProjectName);
		warProject = ProjectImportTestSetup.loadProject(warProjectName);
		seamEjbProject = loadSeamProject(ejbProject);
		seamWarProject = loadSeamProject(warProject);
	}

	private ISeamProject loadSeamProject(IProject project) throws CoreException {
		JobUtils.waitForIdle();

		//System.out.println("Project - "+project);
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		assertNotNull("Seam project for " + project.getName() + " is null", seamProject);

		return seamProject;
	}
	
	
}
