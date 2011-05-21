package org.jboss.tools.cdi.seam.config.ui.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.test.util.ResourcesUtils;

public class SeamConfigContentAssistTest extends ContentAssistantTestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.seam.config.core.test";
	protected static String PROJECT_NAME = "CDIConfigTest";
	protected static String PROJECT_PATH = "/projects/CDIConfigTest";
	
	protected static String FILE_PATH = "src/META-INF/seam-beans.xml";

	protected ICDIProject cdiProject;

	public SeamConfigContentAssistTest() {
		project = getTestProject();
		cdiProject = CDICorePlugin.getCDIProject(project, false);
	}

	public IProject getTestProject() {
		if(project==null) {
			try {
				project = findTestProject();
				if(project==null || !project.exists()) {
					project = ResourcesUtils.importProject(PLUGIN_ID, PROJECT_PATH);
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Can't import CDI test project: " + e.getMessage());
			}
		}
		return project;
	}

	public static IProject findTestProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public void testCustomClasses() {
		String[] proposals = {"test01:MyBean1", "test01:MyBean2", "test01:MyBean3", "test01:MyBean4"};
		checkProposals(FILE_PATH, "<test01:", 8, proposals, true);
	}

	public void testEEClassesAndKeyWords() {
		String[] proposals = {"s:modifies"};
		checkProposals(FILE_PATH, "<s:mo", 5, proposals, true);
		proposals = new String[]{"s:modifies", "s:replaces", "s:parameters",
				"s:Inject", "s:Alternative", "s:Delegate", "s:Dependent", "s:Disposes",
				"s:Named", "s:New", "s:NormalScope"};
		checkProposals(FILE_PATH, "<s:mo", 3, proposals, false);
	}

	public void testPackageInNamespace() {
		String[] proposals = {
				"urn:java:org.jboss.beans.test01",
				"urn:java:org.jboss.beans.test02",
				"urn:java:org.jboss.beans.test03",
				"urn:java:org.jboss.beans.test04",
				"urn:java:org.jboss.beans.test05",
				"urn:java:org.jboss.beans.test06"};
		String text = "urn:java:org.jboss.beans.";
		checkProposals(FILE_PATH, text, text.length(), proposals, false);
	}


}
