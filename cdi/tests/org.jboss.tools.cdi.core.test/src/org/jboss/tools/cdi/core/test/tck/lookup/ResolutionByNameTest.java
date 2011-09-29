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

package org.jboss.tools.cdi.core.test.tck.lookup;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IField;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * @author Alexey Kazakov
 */
public class ResolutionByNameTest extends TCKTest {

	/**
	 * section 5.3.1 ca),
	 * section 11.3.5 aa),
	 * section 11.3.5 b)
	 * 
	 * @throws CoreException
	 */
	public void testAmbiguousELNamesResolved() throws CoreException {
	    // Cod, Plaice and AlaskaPlaice are named "whitefishJBT" - Cod is a not-enabled policy, AlaskaPlaice specializes Plaice
		Set<IBean> beans = cdiProject.getBeans("whitefishJBT", true);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.byname.AlaskaPlaice");

		IFile f = tckProject.getFile("/JavaSource/org/jboss/jsr299/tck/tests/lookup/byname/beans.xml");
		assertTrue("File /JavaSource/org/jboss/jsr299/tck/tests/lookup/byname/beans.xml not found", f != null && f.exists());

		IPath old = ((CDIProject)cdiProject).replaceBeanXML(f.getFullPath());

		assertTrue("Old beans.xml is not found", old != null);

		try {
			beans = cdiProject.getBeans("whitefishJBT", true);
			assertEquals("Wrong number of the beans", 1, beans.size());
			assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.byname.AlaskaPlaice");

            // Both Salmon and Sole are named "fishJBT" - Sole is an enabled policy
			beans = cdiProject.getBeans("fishJBT", false);
			assertEquals("Wrong number of the beans", 2, beans.size());
			assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.byname.Salmon", "org.jboss.jsr299.tck.tests.lookup.byname.Sole");

			beans = cdiProject.getBeans("fishJBT", true);
			assertEquals("Wrong number of the beans", 1, beans.size());
			assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.byname.Sole");
		} finally {
			old = ((CDIProject)cdiProject).replaceBeanXML(old);
		}
	}

	/**
	 * Abstract class NotBean is annotated @Named("abstractClass")
	 * There is no bean with bean name "abstractClass" for EL or injections, 
	 * but instance of IClassBean created for NotBean can be obtained through 
	 * its members available in model (e.g. producers)
	 * 
	 * @throws CoreException
	 */
	public void testAbstractClassAnnotatedNamed() throws CoreException {
		String abstractClass = "abstractClass";
		Set<IBean> beans = cdiProject.getBeans(abstractClass, false);
		assertTrue(beans.isEmpty());
		
		beans = cdiProject.getBeans("producerInAbstractClass", false);
		assertEquals(1, beans.size());
		IBean b = beans.iterator().next();
		assertTrue(b instanceof IProducer);

		IClassBean cb = ((IProducer)b).getClassBean();
		assertNotNull(cb);
		IAnnotationDeclaration named = cb.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		assertEquals(abstractClass, named.getMemberValue(null));
		
		Set<IInjectionPoint> injections = cdiProject.getInjections("org.jboss.jsr299.tck.tests.jbt.lookup.NotBean");
		assertEquals(2, injections.size());
		for (IInjectionPoint p: injections) {
			assertTrue(p instanceof IInjectionPointField);
			IField f = ((IInjectionPointField)p).getField();
			Set<IBean> bs = cdiProject.getBeans(false, p);
			if("f1".endsWith(f.getElementName())) {
				assertEquals("Injection field f1 cannot be resolved to abstract class bean.", 0, bs.size());
			} else if("f2".endsWith(f.getElementName())) {
				assertEquals("Injection field f2 should be resolved to producer.", 1, bs.size());
			} else {
				fail("Unexpected field: " + f.getElementName());
			}
		}
	}
}