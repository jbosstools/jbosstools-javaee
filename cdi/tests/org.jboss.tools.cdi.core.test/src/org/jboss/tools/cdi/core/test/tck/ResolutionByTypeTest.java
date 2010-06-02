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
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IQualifierDeclaration;

/**
 * @author Alexey Kazakov
 */
public class ResolutionByTypeTest extends TCKTest {

	/**
	 * Section 5.2 - Typesafe resolution
	 *   ld) Test with matching beans with matching qualifier with same annotation member value for each member which is not annotated @javax.enterprise.util.NonBinding.
	 *   
	 * @throws CoreException 
	 */
	public void testResolveByTypeWithNonBindingMembers() throws CoreException {
		IQualifierDeclaration expensiveQualifier = getQualifierDeclarationFromBeanClass("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/RoundWhitefish.java", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Expensive");
		IQualifierDeclaration whitefishQualifier = getQualifierDeclarationFromBeanClass("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/RoundWhitefish.java", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Whitefish");
		IParametedType type = getType("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Animal");
		Set<IBean> beans = cdiProject.getBeans(true, type, new IQualifierDeclaration[]{expensiveQualifier, whitefishQualifier});
		assertContainsBeanClasses(beans, new String[]{"org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.RoundWhitefish", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Halibut"});
	}
}