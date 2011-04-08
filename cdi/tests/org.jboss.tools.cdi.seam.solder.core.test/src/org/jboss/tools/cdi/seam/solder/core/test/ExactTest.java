package org.jboss.tools.cdi.seam.solder.core.test;


import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class ExactTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.seam.solder.core.test";
	IProject project = null;

	public ExactTest() {}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDISolderTest");
		JobUtils.waitForIdle();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}

	public void testVeto() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);

		Set<IBean> bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/exact/FishFactory.java"));
		assertEquals(2, bs.size());
		IClassBean cls = null;
		IProducerMethod mtd = null;
		for (IBean b: bs) {
			if(b instanceof IClassBean) {
				cls = (IClassBean)b;
			} else if(b instanceof IProducerMethod) {
				mtd = (IProducerMethod)b;
			}
		}
		assertNotNull(cls);
		assertNotNull(mtd);
		Set<IInjectionPoint> points = cls.getInjectionPoints();
		int count = 0;
		for (IInjectionPoint p: points) {
			Set<IBean> injected = cdi.getBeans(false, p);
			IMember member = p.getSourceMember();
			if(member.getElementName().equals("peacefulFish")) {
				assertEquals(1, injected.size());
				IBean ib = injected.iterator().next();
				assertEquals("org.jboss.exact.Salmon", ib.getBeanClass().getFullyQualifiedName());
				count++;
			} else if(member.getElementName().equals("dangerousFish")) {
				assertEquals(1, injected.size());
				IBean ib = injected.iterator().next();
				assertEquals("org.jboss.exact.Shark", ib.getBeanClass().getFullyQualifiedName());
				count++;
			} else if(member.getElementName().equals("getTastyFish")) {
				assertEquals(1, injected.size());
				IBean ib = injected.iterator().next();
				assertEquals("org.jboss.exact.Salmon", ib.getBeanClass().getFullyQualifiedName());
				count++;
			} else {
			}
		}
		assertEquals(3, count);
	}

	public void testRequires() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);

		//1. class annotated @Requires that references single non-available class
		TypeDefinition d = cdi.getNature().getDefinitions().getTypeDefinition("org.jboss.requires.Bear");
		assertNotNull(d);            //Though there exists Java type Bear
		IAnnotationDeclaration a = d.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		assertNotNull(a);
		Object name = a.getMemberValue(null);
		assertEquals("bear", name); //...and it is annotated with @Named("bear")
		Set<IBean> bs = cdi.getBeans("bear", false);
		assertTrue(bs.isEmpty());    //...CDI model does not have a bean named "bear"
		bs = cdi.getBeans(d.getResource().getFullPath());
		assertTrue(bs.isEmpty());    //...and does not loaded any beans form its resource

		//2. class annotated @Requires that references array of classes some of which are not available
		d = cdi.getNature().getDefinitions().getTypeDefinition("org.jboss.requires.Bee");
		assertNotNull(d);            //Though there exists Java type Bee
		a = d.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		assertNotNull(a);
		name = a.getMemberValue(null);
		assertEquals("bee", name);  //...and it is annotated with @Named("bee")
		bs = cdi.getBeans("bee", false);
		assertTrue(bs.isEmpty());    //...CDI model does not have a bean named "bee"
		bs = cdi.getBeans(d.getResource().getFullPath());
		assertTrue(bs.isEmpty());    //...and does not loaded any beans form its resource

		//3. class annotated @Requires that references single available class
		bs = cdi.getBeans("fly", false);
		assertEquals(1, bs.size());    //...CDI model has a bean named "fly"

		//4. class annotated @Requires that references array of available classes
		bs = cdi.getBeans("dragonfly", false);
		assertEquals(1, bs.size());    //...CDI model has a bean named "dragonfly"

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
