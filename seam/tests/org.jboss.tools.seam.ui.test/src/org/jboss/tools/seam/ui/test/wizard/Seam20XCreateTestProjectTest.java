package org.jboss.tools.seam.ui.test.wizard;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.core.test.project.facet.AbstractSeamFacetTest;

public class Seam20XCreateTestProjectTest extends SeamCreateTestProjectTest {

	public Seam20XCreateTestProjectTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		assertSeamHomeAvailable();
		File folder = getSeamHomeFolder();
		SeamRuntimeManager.getInstance().addRuntime(SEAM_2_0_0, folder.getAbsolutePath(), SeamVersion.SEAM_2_0, true);
	}
	
	@Override
	protected IProjectFacetVersion getSeamFacetVersion() {
		IProjectFacet seamFacet = ProjectFacetsManager.getProjectFacet("jst.seam");
		return seamFacet.getVersion("2.0");
	}

	@Override
	protected String getSystemPropertyName() {
		// TODO Auto-generated method stub
		return AbstractSeamFacetTest.SEAM_2_0_HOME;
	}	
	
	public void testSeamWarProjectWithTestProject() throws CoreException{
		checkTestProjectCreation("test_seam20_war_t", SEAM_2_0_0, WAR, true);
	}

	public void testSeamWarProjectWithoutTestProject() throws CoreException{
		checkTestProjectCreation("test_seam20_war", SEAM_2_0_0, WAR, false);
	}

	public void testSeamEarProjectWithTestProject() throws CoreException{
		checkTestProjectCreation("test_seam20_ear_t", SEAM_2_0_0, EAR, true);
	}

	public void testSeamEarProjectWithoutTestProject() throws CoreException{
		checkTestProjectCreation("test_seam20_ear", SEAM_2_0_0, EAR, false);
	}
}
