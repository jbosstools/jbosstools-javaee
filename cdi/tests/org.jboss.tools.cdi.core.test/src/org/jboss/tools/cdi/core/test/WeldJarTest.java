package org.jboss.tools.cdi.core.test;

import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author V.Kabanovich
 *
 */
public class WeldJarTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project1 = null;

	public WeldJarTest() {}

	public void setUp() throws Exception {
		project1 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest1");
		JobUtils.waitForIdle();
		project1.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}

	public void testWeldJar() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project1, true);
		Set<IBean> bs = cdi.getBeans(new Path("/CDITest1/src/cdi/test/MyShellImpl.java"));
		assertFalse(bs.isEmpty());
		IBean b = bs.iterator().next();
		Set<IInjectionPoint> ps = b.getInjectionPoints();
		IInjectionPoint p = ps.iterator().next();
		Set<IBean> inbs = cdi.getBeans(false, p);
		assertFalse(inbs.isEmpty());

		bs = cdi.getBeans(new Path("/CDITest1/src/cdi/test/MyBeanManager.java"));
		assertFalse(bs.isEmpty());
		b = bs.iterator().next();
		ps = b.getInjectionPoints();
		p = ps.iterator().next();
		inbs = cdi.getBeans(false, p);
		assertFalse(inbs.isEmpty());

		//Find bean defined in some-weld.jar
		bs = cdi.getBeans("beanInWeldJar", false);
		assertEquals(1, bs.size());
	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		project1.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		JobUtils.waitForIdle();
	}
}