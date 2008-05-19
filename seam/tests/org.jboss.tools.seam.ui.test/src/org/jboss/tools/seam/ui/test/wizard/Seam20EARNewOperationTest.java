package org.jboss.tools.seam.ui.test.wizard;

public class Seam20EARNewOperationTest extends Seam12EARNewOperationTest {
	@Override
	void setUpSeamProjects() {
		setUpSeamProject(earProject, AbstractSeamNewOperationTest.SEAM_1_2);
	}
}
