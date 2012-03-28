package org.jboss.tools.cdi.seam3.bot.test.base;

import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibraries;

public class SolderTestBase extends Seam3TestBase {

	@Override
	public void prepareWorkspace() {
		importProjectWithLibrary(getProjectName(), SeamLibraries.SOLDER);
	}
	
}
