package org.jboss.tools.seam.ui.test.wizard;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.core.test.project.facet.AbstractSeamFacetTest;

public class Seam20XProjectNamesTest extends SeamProjectNamesTest {

	public Seam20XProjectNamesTest(String name) {
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
		checkProjectNamesCreation("seam12_war_t", "ear_seam12_war_t", "ejb_seam12_war_t", "test_seam12_war_t", SEAM_2_0_0, WAR, true);
	}

	public void testSeamWarProjectWithoutTestProject() throws CoreException{
		checkProjectNamesCreation("seam12_war", "ear_seam12_war", "ejb_seam12_war", "test_seam12_war", SEAM_2_0_0, WAR, false);
	}

	public void testSeamEarProjectWithTestProject() throws CoreException{
		checkProjectNamesCreation("seam12_ear_t", "ear_seam12_ear_t", "ejb_seam12_ear_t", "test_seam12_ear_t", SEAM_2_0_0, EAR, true);
	}

	public void testSeamEarProjectWithoutTestProject() throws CoreException{
		checkProjectNamesCreation("seam12_ear", "ear_seam12_ear", "ejb_seam12_ear", "test_seam12_ear", SEAM_2_0_0, EAR, false);
	}
}
