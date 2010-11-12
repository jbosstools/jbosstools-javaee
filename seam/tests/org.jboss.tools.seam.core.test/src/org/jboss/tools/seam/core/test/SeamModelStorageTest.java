package org.jboss.tools.seam.core.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.test.util.JobUtils;

public class SeamModelStorageTest extends TestCase {
	IProject project = null;

	public SeamModelStorageTest() {
		super("Seam Model Storage Test");
	}

	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("TestStorage");
		assertNotNull("Can't load TestStorage", project); //$NON-NLS-1$
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
		
		IFile fromFile = project.getFile(new Path("WebContent/WEB-INF/lib/jboss-seam.1"));
		IFile toFile = project.getFile(new Path("WebContent/WEB-INF/lib/jboss-seam.jar"));
		
		toFile.create(fromFile.getContents(), true, new NullProgressMonitor());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		JobUtils.waitForIdle();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		JobUtils.waitForIdle();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
		
		mod = seam.getModificationsSinceLastStore();
		System.out.println("-->" + mod);
		assertTrue("Modification index after adding new library must be greater than 0", mod > 0);
	}

}
