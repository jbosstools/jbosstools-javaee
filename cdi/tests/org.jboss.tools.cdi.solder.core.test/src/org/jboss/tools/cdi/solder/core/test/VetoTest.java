package org.jboss.tools.cdi.solder.core.test;


import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class VetoTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.solder.core.test";
	IProject project = null;

	public VetoTest() {}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDISolderTest");
		JobUtils.waitForIdle();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}

	public void testVeto() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);

		//1. package annotated @Veto; class is not annotated with it
		TypeDefinition d = cdi.getNature().getDefinitions().getTypeDefinition("org.jboss.vetoed.Tiger");
		assertNotNull(d);            //Though there exists Java type Tiger
		IAnnotationDeclaration a = d.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		assertNotNull(a);
		Object name = a.getMemberValue(null);
		assertEquals("tiger", name); //...and it is annotated with @Named("tiger")
		Set<IBean> bs = cdi.getBeans("tiger", false);
		assertTrue(bs.isEmpty());    //...CDI model does not have bean named "tiger"
		bs = cdi.getBeans(d.getResource().getFullPath());
		assertTrue(bs.isEmpty());    //...and does not loaded any beans form its resource

		//2. class annotated @Veto
		d = cdi.getNature().getDefinitions().getTypeDefinition("org.jboss.somevetoed.Lion");
		assertNotNull(d);            //Though there exists Java type Lion
		a = d.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		assertNotNull(a);
		name = a.getMemberValue(null);
		assertEquals("lion", name);  //...and it is annotated with @Named("lion")
		bs = cdi.getBeans("lion", false);
		assertTrue(bs.isEmpty());    //...CDI model does not have bean named "lion"
		bs = cdi.getBeans(d.getResource().getFullPath());
		assertTrue(bs.isEmpty());    //...and does not loaded any beans form its resource
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
		assertTrue(bs.isEmpty());    //...CDI model does not have bean named "bear"
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
		assertTrue(bs.isEmpty());    //...CDI model does not have bean named "bee"
		bs = cdi.getBeans(d.getResource().getFullPath());
		assertTrue(bs.isEmpty());    //...and does not loaded any beans form its resource

		//3. class annotated @Requires that references single available class
		bs = cdi.getBeans("fly", false);
		assertTrue(!bs.isEmpty());    //...CDI model have a bean named "fly"

		//4. class annotated @Requires that references array of available classes
		bs = cdi.getBeans("dragonfly", false);
		assertTrue(!bs.isEmpty());    //...CDI model have a bean named "dragonfly"

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
