package org.jboss.tools.cdi.solder.core.test;

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
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class BeanNamingTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.solder.core.test";
	IProject project = null;

	public BeanNamingTest() {}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDISolderTest");
		JobUtils.waitForIdle();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}

	public void testNamedPackage() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);

		//1. package @Named; class not annotated
		Set<IBean> bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/named/Dog.java"));
		assertFalse(bs.isEmpty());
		IBean b = bs.iterator().next();
		assertEquals("dog", b.getName());

		//2. package@Named; class @Named("little")
		bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/named/Racoon.java"));
		assertFalse(bs.isEmpty());
		b = bs.iterator().next();
		assertEquals("little", b.getName());
	}

	public void testFullyQualifiedPackage() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		
		//1. package @FullyQualified and @Named; class not annotated
		Set<IBean> bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/fullyqualified/Cat.java"));
		assertFalse(bs.isEmpty());
		IBean b = bs.iterator().next();
		assertEquals("org.jboss.fullyqualified.cat", b.getName());

		//2. package @FullyQualified and @Named; class @Named("rodent")
		bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/fullyqualified/Mouse.java"));
		assertFalse(bs.isEmpty());
		b = bs.iterator().next();
		assertEquals("org.jboss.fullyqualified.rodent", b.getName());
		
		//3. package @FullyQualified and @Named; class @FullyQualified(Dog.class)
		bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/fullyqualified/Elephant.java"));
		assertFalse(bs.isEmpty());
		b = bs.iterator().next();
		assertEquals("org.jboss.named.elephant", b.getName());

	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		JobUtils.waitForIdle();
	}
}
