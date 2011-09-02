/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.tck;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.EclipseJavaUtil;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * 
 * EXECUTE: Create beans in cdi project
 *
 * public class A {
 * 	@Inject B.D a;
 * }
 *
 * public class B extends C {
 * }
 *
 * public class C {
 * 	public static class D {}
 * }
 * ASSERT: Injection point is resolved to C.D.
 * EXECUTE: copy declaration of class D into class B and wait for the incremental build.
 * FAILURE: Injection point is still resolved to C.D
 * ASSERT: Injection point is resolved to B.D
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ResolvedTypesCacheTest extends TCKTest {
	static String PATH_A = "JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/cache/A.java";
	static String PATH_B = "JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/cache/B.java";
	static String PATH_B_CHANGED = "JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/cache/B.changed";
	static String PATH_C = "JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/cache/C.java";

	public void testCache() throws CoreException, IOException {
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(tckProject);
		IType t = EclipseJavaUtil.findType(javaProject, "org.jboss.jsr299.tck.tests.jbt.resolution.cache.A");
		assertEquals("org.jboss.jsr299.tck.tests.jbt.resolution.cache.C.D", EclipseJavaUtil.resolveType(t, "B.D"));

		IInjectionPointField injectionPoint = getInjectionPointField(PATH_A, "a");
		assertNotNull(injectionPoint);
		Set<IBean> bs = cdiProject.getBeans(true, injectionPoint);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertEquals("org.jboss.jsr299.tck.tests.jbt.resolution.cache.C$D", b.getBeanClass().getFullyQualifiedName());
		
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		IFile bFile = tckProject.getFile(new Path(PATH_B));
		IFile changedFile = tckProject.getFile(new Path(PATH_B_CHANGED));
		bFile.setContents(changedFile.getContents(), IFile.FORCE, new NullProgressMonitor());
		tckProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);

		t = EclipseJavaUtil.findType(javaProject, "org.jboss.jsr299.tck.tests.jbt.resolution.cache.A");
		assertEquals("org.jboss.jsr299.tck.tests.jbt.resolution.cache.B.D", EclipseJavaUtil.resolveType(t, "B.D"));
		
		injectionPoint = getInjectionPointField(PATH_A, "a");
		bs = cdiProject.getBeans(true, injectionPoint);
		assertEquals(1, bs.size());
		b = bs.iterator().next();
		assertEquals("org.jboss.jsr299.tck.tests.jbt.resolution.cache.B$D", b.getBeanClass().getFullyQualifiedName());
	}

}
