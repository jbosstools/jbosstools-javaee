package org.jboss.tools.seam.ui.test.wizard;

public class Seam20WARNewOperationTest extends Seam12WARNewOperationTest {
	@Override
	void setUpSeamProjects() {
		setUpSeamProject(warProject, AbstractSeamNewOperationTest.SEAM_2_0);
	}
}
