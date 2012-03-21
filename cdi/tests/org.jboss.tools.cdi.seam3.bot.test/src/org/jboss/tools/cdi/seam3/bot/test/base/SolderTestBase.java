package org.jboss.tools.cdi.seam3.bot.test.base;

import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibraries;

public class SolderTestBase extends Seam3TestBase {

	@Override
	public void prepareWorkspace() {
		projectImportHelper.importTestProject("/resources/projects/" + getProjectName(), getProjectName());
		addAndCheckLibraryInProject(getProjectName(), SeamLibraries.SOLDER);
	}
	
}
