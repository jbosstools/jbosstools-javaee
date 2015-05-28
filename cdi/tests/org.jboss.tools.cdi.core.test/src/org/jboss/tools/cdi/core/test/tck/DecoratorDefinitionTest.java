/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.core.test.tck;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.common.java.IParametedType;

/**
 * @author Alexey Kazakov
 */
public class DecoratorDefinitionTest extends TCKTest {

	/**
	 * section 8.1 b)
	 * section 8.1 c)
	 * section 11.1.1 b)
	 * section 11.3.11 a)
	 * section 11.3.11 b)
	 * 
	 * @throws JavaModelException
	 */
	public void testDecoratedTypes() throws JavaModelException, CoreException {
		IDecorator decorator = getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/FooDecorator.java");
		Collection<IParametedType> types = decorator.getDecoratedTypes();
		assertContainsTypes(types,
				"org.jboss.jsr299.tck.tests.decorators.definition.Foo",
				"org.jboss.jsr299.tck.tests.decorators.definition.Bar",
				"org.jboss.jsr299.tck.tests.decorators.definition.Baz",
				"org.jboss.jsr299.tck.tests.decorators.definition.Boo");
	}

	/**
	 * section 8.1.2 a)
	 * section 11.1.1 c)
	 */
	public void testDelegateInjectionPoint() throws JavaModelException, CoreException {
		IDecorator decorator = getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/TimestampLogger.java");
		assertEquals("Wrong number of injection points.", 1, decorator.getInjectionPoints().size());
		IInjectionPoint injection = decorator.getInjectionPoints().iterator().next();
		assertEquals("Wrong type of the injection point.", "org.jboss.jsr299.tck.tests.decorators.definition.Logger", injection.getType().getType().getFullyQualifiedName());
		assertNotNull("Can't find @Delegate annotation.", injection.getDelegateAnnotation());
	}

	/**
	 * section 8.2 a)
	 */
	public void testNonEnabledDecoratorNotResolved() throws JavaModelException, CoreException {
		IDecorator decorator = getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/FieldDecorator.java");
		assertFalse("Decorator is enabled.", decorator.isEnabled());
	}

	public void testEnabledDecoratorResolved() throws JavaModelException, CoreException {
		IDecorator decorator = getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/resolution/QuxDecorator.java");
		assertFalse("Decorator QuxDecorator is enabled.", decorator.isEnabled());
		
		IFile f = tckProject.getFile("/WebContent/WEB-INF/tests/decorators/resolution/beans.xml");
		assertTrue("File /WebContent/WEB-INF/tests/decorators/resolution/beans.xml not found", f != null && f.exists());

		Set<IPath> paths = new HashSet<IPath>();
		paths.add(f.getFullPath());
		Set<IPath> old = ((CDIProject)cdiProject).replaceBeanXML(paths);
		
		assertTrue("Old beans.xml is not found", old != null);

		try {
			decorator = getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/resolution/QuxDecorator.java");
			assertTrue("Decorator QuxDecorator is not enabled.", decorator.isEnabled());
		} finally {
			old = ((CDIProject)cdiProject).replaceBeanXML(old);
		}
	}

	public void testDecoratorIsNotInjected() throws CoreException {
		IInjectionPointField f = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/decorator/House.java", "decorator");
		/*
		 * Invocation getBeans(false, f) returns all beans that match type and qualifiers.
		 */
		Collection<IBean> bs = cdiProject.getBeans(false, f);
		assertEquals(1, bs.size());
		assertTrue(bs.iterator().next() instanceof IDecorator);
		/*
		 * Invocation getBeans(true, f) filters away all beans that are not available for injection.
		 */
		bs = cdiProject.getBeans(true, f);
		assertTrue(bs.isEmpty());
	}

	public void testCustomDecorator() throws CoreException {
		getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/custom/VehicleDecorator.java");
	}

	/**
	 * class XDecorator extends XDecorator
	 * class YDecorator extends ZDecorator
	 * class ZDecorator extends YDecorator
	 * class WDecorator extends YDecorator
	 * 
	 * When cyclic (erroneous) java hierarchy takes place, cdi should avoid cyclic dependency
	 * in components. Loader, when detects that setting super bean is going to create the cyclic 
	 * dependency, sets null instead.
	 * For the example above that means:
	 * a) XDecorator will have super set to null;
	 * b) Of YDecorator, ZDecorator exactly one will have super set to null, and the other will
	 *    have the correct super, which one being depended on random order or loaded resources;
	 * c) WDecorator will have super set to YDecorator, since it is outside of the loop.
	 * 
	 * @throws CoreException
	 */
	public void testCyclicDependencies() throws CoreException {
		IDecorator xdecorator = getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/cycle/XDecorator.java");
		assertNotNull(xdecorator);
		ClassBean xs = ((ClassBean)xdecorator).getSuperClassBean();
		assertNull(xs);
		IDecorator ydecorator = getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/cycle/YDecorator.java");
		ClassBean ys = ((ClassBean)ydecorator).getSuperClassBean();
		IDecorator zdecorator = getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/cycle/ZDecorator.java");
		ClassBean zs = ((ClassBean)zdecorator).getSuperClassBean();
		IDecorator wdecorator = getDecorator("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/cycle/WDecorator.java");
		ClassBean ws = ((ClassBean)wdecorator).getSuperClassBean();
		assertTrue((ys == null) != (zs == null));
		assertTrue((ys == zdecorator) || (zs == ydecorator));
		assertTrue(ws == ydecorator);
	}

}