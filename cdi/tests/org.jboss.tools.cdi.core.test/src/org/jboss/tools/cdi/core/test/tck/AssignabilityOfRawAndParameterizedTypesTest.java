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

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

/**
 * @author Alexey Kazakov
 */
public class AssignabilityOfRawAndParameterizedTypesTest extends TCKTest {

	/**
	 * Section 5.2 - Typesafe resolution
	 *   kb) Test with a raw type.
	 *   
	 * @throws CoreException 
	 */
	public void testAssignabilityToRawType() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.Dao");
		assertEquals("Wrong number of the beans", 4, beans.size());
	}

	/**
	 * Section 5.2.3 - Assignability of raw and parameterized types
	 *   ba) Check the required type parameter and the bean type parameter are actual types with identical raw type
	 *   
	 * @throws CoreException 
	 */
	public void testAssignabilityOfParameterizedTypeWithActualTypesToParameterizedTypeWithActualTypes() throws CoreException {
		IType type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.MapProducer");
		IParametedType parametedType = cdiProject.getNature().getTypeFactory().getParametedType(type, "QMap<QInteger;>;");
		Set<IBean> beans = cdiProject.getBeans(true, parametedType, new IQualifierDeclaration[0]);
		assertEquals("Wrong number of the beans", 2, beans.size());

		type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.IntegerHashMap");
		parametedType = cdiProject.getNature().getTypeFactory().getParametedType(type, "QHashMap<QInteger;>;");
		beans = cdiProject.getBeans(true, parametedType, new IQualifierDeclaration[0]);
		assertFalse("Wrong number of the beans", beans.isEmpty());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.IntegerHashMap");
	}

	/**
	 * Section 5.2.3 - Assignability of raw and parameterized types
	 *   c) Check the required type parameter is a wildcard, the bean type parameter is an actual type and the actual type is assignable to the upper bound of the wildcard and assignable from the lower bound of the wildcard
	 *   
	 * @throws CoreException 
	 */
	public void testAssignabilityOfParameterizedTypeWithActualTypesToParameterizedTypeWithWildcards() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/parameterized/InjectedBean.java", "map");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.IntegerHashMap");
	}

//	public void testAssignabilityOfParameterizedTypeWithActualTypesToParameterizedTypeWithWildcardsAtInjectionPoint() throws CoreException {
//		// The same as testAssignabilityOfParameterizedTypeWithActualTypesToParameterizedTypeWithWildcards()
//	}

	/**
	 * Section 5.2.3 - Assignability of raw and parameterized types
	 *   da) Check the required type parameter is a wildcard, the bean type parameter is a type variable and the upper bound of the type variable is assignable to the upper bound of the wildcard and assignable from the lower bound of the wildcard
	 *   
	 * @throws CoreException 
	 */
	public void testAssignabilityOfParameterizedTypeWithTypeVariablesToParameterizedTypeWithWildcards() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/AssignabilityOfRawAndParameterizedTypes.java", "injection");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.Result", "java.lang.Object");
	}

	/**
	 * Section 5.2.3 - Assignability of raw and parameterized types
	 *   db) Check the required type parameter is a wildcard, the bean type parameter is a type variable and the upper bound of the type variable is assignable from the upper bound of the wildcard and assignable from the lower bound of the wildcard
	 *   
	 * @throws CoreException 
	 */
	public void testAssignabilityOfParameterizedTypeWithTypeVariablesToParameterizedTypeWithWildcards2() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/AssignabilityOfRawAndParameterizedTypes.java", "injection2");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.Result", "java.lang.Object");
	}

	/**
	 * Section 5.2.3 - Assignability of raw and parameterized types
	 *   e) Check the required type parameter is an actual type, the bean type parameter is a type variable and the actual type is assignable to the upper bound of the type variable
	 *   
	 * @throws CoreException 
	 */
	public void testAssignabilityOfParameterizedTypeWithTypeVariablesToParameterizedTypeWithActualTypes() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/AssignabilityOfRawAndParameterizedTypes.java", "injection3");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.Result", "java.lang.Object");
	}

	/**
	 * Section 5.2.3 - Assignability of raw and parameterized types
	 *   f) Check the required type parameter and the bean type parameter are both type variables and the upper bound of the required type parameter is assignable to the upper bound of the bean type parameter.
	 *   
	 * @throws CoreException 
	 */
	public void testAssignabilityOfParameterizedTypeWithTypeVariablesToParameterizedTypeTypeVariable() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/AssignabilityOfRawAndParameterizedTypes.java", "injection4");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.Result", "java.lang.Object");
	}
}