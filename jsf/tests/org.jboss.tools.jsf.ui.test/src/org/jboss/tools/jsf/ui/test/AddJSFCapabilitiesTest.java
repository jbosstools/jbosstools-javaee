package org.jboss.tools.jsf.ui.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.common.model.ui.action.AddNatureActionDelegate;
import org.jboss.tools.common.model.ui.util.ExtensionPointUtils;
import org.jboss.tools.jst.web.ui.wizards.project.ImportWebProjectWizard;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;


public class AddJSFCapabilitiesTest extends TestCase {
	IProject project = null;
	IProject fake_as = null;

	public AddJSFCapabilitiesTest() {
		super("Add JSF Capabilities Test");
	}

	public AddJSFCapabilitiesTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		JobUtils.waitForIdle(3000);
		IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember("test_add_jsf_capabilities");
		if(project == null) {
			ProjectImportTestSetup setup = new ProjectImportTestSetup(
					this,
					"org.jboss.tools.jsf.ui.test",
					"projects/test_add_jsf_capabilities",
					"test_add_jsf_capabilities");
			project = setup.importProject();
		}
		this.project = project.getProject();

		IResource fake_as = ResourcesPlugin.getWorkspace().getRoot().findMember("fake_as");
		if(fake_as == null) {
			ProjectImportTestSetup setup = new ProjectImportTestSetup(
					this,
					"org.jboss.tools.jsf.ui.test",
					"projects/fake_as",
					"fake_as");
			fake_as = setup.importProject();
		}
		this.fake_as = fake_as.getProject();
		JobUtils.waitForIdle();
	}

	public void testAddJSFCapabilities() {
		ImportWebProjectWizard wizard = (ImportWebProjectWizard)new Act().getWizard(project);
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();

		IRuntime r0 = createRuntime();
		((ImportWebProjectWizard)wizard).setRuntimeName(r0.getName());

		assertTrue(wizard.canFinish());
		
		boolean b = wizard.performFinish();

		assertTrue(b);
		
		try {
			assertTrue(project.hasNature("org.jboss.tools.jsf.jsfnature"));
		} catch (CoreException e) {
			fail(e.getMessage());
		}
		
		IFile f = project.getFile(new Path(".settings/org.eclipse.wst.common.project.facet.core.xml"));
		assertNotNull(f);
		assertTrue(f.exists());
	}

	static String RUNTIME = "org.eclipse.jst.server.tomcat.runtime.60";
	
	IRuntime createRuntime() {
		IRuntimeType t = ServerCore.findRuntimeType(RUNTIME);
		
		IPath location = fake_as.getLocation();
		try {
			IRuntimeWorkingCopy r = t.createRuntime(RUNTIME, new NullProgressMonitor());
			r.setName("myRuntime");
			r.setLocation(location);
			return r.save(true, new NullProgressMonitor());
		
		} catch (CoreException e) {
			fail(e.getMessage());
			return null;
		}
	}
	
	private void refreshProject(IProject project){
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			JobUtils.waitForIdle();
			JobUtils.delay(2000);
		} catch (CoreException e) {
			// ignore
		}
	}
	
	class Act extends AddNatureActionDelegate {

		protected IWizard getWizard(IProject project) {
			ImportWebProjectWizard wizard = (ImportWebProjectWizard)ExtensionPointUtils.findImportWizardsItem(
					"org.jboss.tools.jsf",
					"org.jboss.tools.jsf.ui.wizard.project.ImportProjectWizard" //$NON-NLS-1$
			);
			if (wizard == null) throw new IllegalArgumentException("Wizard org.jboss.tools.common.model.ui.wizards.ImportProjectWizard is not found.");	 //$NON-NLS-1$
			wizard.setInitialName(project.getName());
			wizard.setInitialLocation(findWebXML(project.getLocation().toString()));
			wizard.init(ModelUIPlugin.getDefault().getWorkbench(), null);
			wizard.setWindowTitle(WizardKeys.getString("ADD_JSF_NATURE")); //$NON-NLS-1$
			return wizard;
		}

		protected String getNatureID() {
			return null;
		}
		
	}
}
