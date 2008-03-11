package org.jboss.tools.seam.ui.test.wizard;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.ui.wizard.OpenSeamComponentDialog;
import org.jboss.tools.seam.ui.wizard.OpenSeamComponentDialog.SeamComponentWrapper;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.xpl.EditorTestHelper;

/**
 * @author Daniel Azarov
 * 
 */
public class OpenSeamComponentDialogTest extends TestCase{
	private IProject project;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("TestComponentView");
		if(project == null) {
			ProjectImportTestSetup setup = new ProjectImportTestSetup(
					this,
					"org.jboss.tools.seam.ui.test",
					"projects/TestComponentView",
					"TestComponentView");
			project = setup.importProject();
		}
		this.project = project.getProject();
		this.project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		
		EditorTestHelper.joinBackgroundActivities();
	}
	
	@Override
	protected void tearDown() throws Exception {
		ResourcesUtils.deleteProject("TestComponentView");
		EditorTestHelper.joinBackgroundActivities();
	}
	
	public void testOpenSeamComponentDialogSearch() {
		
		find("m", "mockSecureEntity");
		find("o", "org.jboss.seam.captcha.captcha");
		find("p", "package1.package2.package3.package4.myComponent");
		
	}
	
	
	//JBIDE-1879
	public void testFindShortHand() {
		
		find("o*jbpm", "org.jboss.seam.core.jbpm");
		find("jbpm", "jbpm");
		
		
	}
	
	private void find(String pattern, String componentName){
		OpenSeamComponentDialog dialog = new OpenSeamComponentDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		
		dialog.setInitialPattern(pattern);
		dialog.beginTest();
		/*try{
			Thread.sleep(30000);
		}catch(Exception ex){
			ex.printStackTrace();
		}*/
		dialog.endTest();
		Object[] objects = dialog.getResult();
		
		
		assertNotNull("Search dialog returned null when searching for " + pattern, objects);
		
		assertTrue("Component "+componentName+" not found", objects.length != 0);
		
		SeamComponentWrapper wrapper = (SeamComponentWrapper)objects[0];
		
		assertEquals("Component "+componentName+" not found with " + pattern, wrapper.getComponentName(), componentName);
	}
	
}
