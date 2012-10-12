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

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.impl.definition.PackageDefinition;
import org.jboss.tools.common.java.impl.AnnotationDeclaration;


/**
 * @author Viacheslav Kabanovich
 */
public class PackageInfoTest extends TCKTest {

	/**

s	 * @throws CoreException
	 */
	public void testDeclaration() throws CoreException {
		PackageDefinition d = cdiProject.getNature().getDefinitions().getPackageDefinition("org.jboss.jsr299.tck.tests.lookup.pack");
		assertNotNull(d);
		AnnotationDeclaration a = d.getAnnotation("java.lang.annotation.Target");
		Object o = a.getMemberValue("value");
		assertEquals("java.lang.annotation.ElementType.FIELD", o);
	}

}