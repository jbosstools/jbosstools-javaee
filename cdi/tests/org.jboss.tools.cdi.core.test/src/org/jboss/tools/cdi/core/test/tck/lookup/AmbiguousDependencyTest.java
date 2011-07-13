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
package org.jboss.tools.cdi.core.test.tck.lookup;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.test.tck.TCKTest;

/**
 * @author Alexey Kazakov
 */
public class AmbiguousDependencyTest extends TCKTest {

	/**
	 * section 5.2.1 aa) 
	 * @throws CoreException
	 */
	public void testAmbiguousDependency() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/dependency/resolution/broken/ambiguous/Farm_Broken.java", "animal");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertTrue(beans.size()>1);
	}
}