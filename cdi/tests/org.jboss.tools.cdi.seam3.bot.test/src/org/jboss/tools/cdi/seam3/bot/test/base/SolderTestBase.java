package org.jboss.tools.cdi.seam3.bot.test.base;

import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibraries;

public class SolderTestBase extends Seam3TestBase {

	@Override
	public void prepareWorkspace() {
		importProjectWithLibrary(getProjectName(), SeamLibraries.SOLDER);
	}
	
	public void importProjectWithLibrary(String projectName, SeamLibraries library) {
		projectImportHelper.importTestProject("/resources/projects/" + projectName, projectName);
		addAndCheckLibraryInProject(projectName, library);
		eclipse.cleanAllProjects();
	}
	
}
