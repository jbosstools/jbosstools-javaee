package org.jboss.tools.cdi.seam.solder.core.test;

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
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class BeanNamingTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.seam.solder.core.test";
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
		IBean b = findBeanByMemberName(bs, "Dog");
		assertNotNull(b);
		assertEquals("dog", b.getName());

		//2. package@Named; class @Named("little")
		bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/named/Racoon.java"));
		assertFalse(bs.isEmpty());
		b = bs.iterator().next();
		assertEquals("little", b.getName());
	}

	public void testFullyQualifiedPackage() throws CoreException {
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
		b = findBeanByMemberName(bs, "Elephant");
		assertNotNull(b);
		assertEquals("org.jboss.named.elephant", b.getName());
	}

	public void testFullyQualifiedProducers() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		//1. package @FullyQualified
		Set<IBean> bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/fullyqualified/Elephant.java"));
		
		//1.1 producer method @Named
		IBean b = findBeanByMemberName(bs, "getTail");
		assertNotNull(b);
		assertEquals("org.jboss.fullyqualified.tail", b.getName());

		//1.2 producer method @Named and @FullyQualified(Dog.class)
		b = findBeanByMemberName(bs, "getTrunk");
		assertNotNull(b);
		assertEquals("org.jboss.named.trunk", b.getName());

		//1.3 producer field @Named
		b = findBeanByMemberName(bs, "ear");
		assertNotNull(b);
		assertEquals("org.jboss.fullyqualified.ear", b.getName());

		//1.4 producer field @Named and @FullyQualified(Dog.class)
		b = findBeanByMemberName(bs, "eye");
		assertNotNull(b);
		assertEquals("org.jboss.named.eye", b.getName());

		//2. package has not @FullyQualified
		bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/named/Dog.java"));

		//2.1 producer method @Named
		b = findBeanByMemberName(bs, "getHair");
		assertNotNull(b);
		assertEquals("hair", b.getName());

		//2.2 producer method @Named and @FullyQualified(Elephant.class)
		b = findBeanByMemberName(bs, "getNose");
		assertNotNull(b);
		assertEquals("org.jboss.fullyqualified.nose", b.getName());

		//2.3 producer field @Named
		b = findBeanByMemberName(bs, "jaws");
		assertNotNull(b);
		assertEquals("jaws", b.getName());

		//2.4 producer field @Named and @FullyQualified(Elephant.class)
		b = findBeanByMemberName(bs, "eye");
		assertNotNull(b);
		assertEquals("org.jboss.fullyqualified.black-eye", b.getName());
	}

	private IBean findBeanByMemberName(Set<IBean> bs, String memberName) {
		for (IBean b: bs) {
			if(b instanceof IClassBean) {
				if(memberName.equals(((IClassBean)b).getBeanClass().getElementName())) {
					return b;
				}
			} else if(b instanceof IBeanMember) {
				if(memberName.equals(((IBeanMember)b).getSourceMember().getElementName())) {
					return b;
				}
			}
		}
		return null;
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
