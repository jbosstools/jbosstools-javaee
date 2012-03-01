package org.jboss.tools.seam.core.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.seam.core.SeamCoreBuilder;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.TestProjectProvider;

public class SeamModelStorageTest extends TestCase {
	IProject project = null;
	TestProjectProvider provider = null;
	boolean makeCopy = true;

	public SeamModelStorageTest() {
		super("Seam Model Storage Test");
	}

	@Override
	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("TestStorage");
		assertNotNull("Can't load TestStorage", project); //$NON-NLS-1$
		if(!project.exists()) {
			provider = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "TestScanner", true);
			project = provider.getProject();
		}
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
	}

	public void testStorage() throws Exception {
		SeamProject seam = (SeamProject)SeamCorePlugin.getSeamProject(project, true);
		assertNotNull(seam);
		int mod = seam.getModificationsSinceLastStore();
		System.out.println("-->" + mod);
		assertTrue("Modification index after load must be greater than 0", mod > 0);

		seam.store();
		mod = seam.getModificationsSinceLastStore();
		System.out.println("-->" + mod);
		assertEquals("Modification index after store must be cleared", 0, mod);

		SeamProject seamProject = (SeamProject)SeamCorePlugin.getSeamProject(project, true);
		seamProject.setStoreDisabledForTesting(true);

		try {
			IFile fromFile = project.getFile(new Path("WebContent/WEB-INF/lib/jboss-seam.1"));
			IFile toFile = project.getFile(new Path("WebContent/WEB-INF/lib/jboss-seam.jar"));
	
			toFile.create(fromFile.getContents(), true, new NullProgressMonitor());
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
	
			System.out.println("-->" + mod);
	//		Thread.sleep(1000);
			project.build(IncrementalProjectBuilder.FULL_BUILD, SeamCoreBuilder.BUILDER_ID, null, new NullProgressMonitor());
			JobUtils.waitForIdle();

			mod = seam.getModificationsSinceLastStore();
			System.out.println("-->" + mod);
			assertTrue("Modification index after adding new library must be greater than 0", mod > 0);
		} finally {
			seamProject.setStoreDisabledForTesting(false);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		if(provider!=null) {
			provider.dispose();
		}
	}
}