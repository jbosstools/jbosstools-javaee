package org.jboss.tools.cdi.core.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author V.Kabanovich
 *
 */
public class DependentProjectTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project1 = null;
	IProject project2 = null;

	public DependentProjectTest() {}

	public void setUp() throws Exception {
		project1 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest1");
		project1.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();

		project2 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest2");
		project2.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();		
	}

	public void testDependentProject() throws CoreException, IOException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IBean[] beans = cdi2.getBeans();
		IClassBean cb = null;
		for (IBean b: beans) {
			if(b instanceof IClassBean) {
				cb = (IClassBean)b;
				System.out.println(cb.getBeanClass().getFullyQualifiedName());
			}
		}
	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		project1.delete(true, true, null);
		project2.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

}
