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
package org.jboss.tools.cdi.seam.solder.core.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.seam.solder.core.validation.SeamSolderValidationMessages;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class DefaultBeanValidationTest extends SeamSolderTest {

	public DefaultBeanValidationTest() {}

	public void testBrokenGenericBean() throws CoreException {
		IFile file = getTestProject().getFile(new Path("src/org/jboss/defaultbean/DefaultFieldProducerBroken.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, SeamSolderValidationMessages.DEFAULT_PRODUCER_FIELD_ON_NORMAL_SCOPED_BEAN, 16);
	}

	public void testIdenticalDefaultBeans() throws CoreException {
		IFile file = getTestProject().getFile(new Path("src/org/jboss/defaultbean/IdenticalDefaultBeans.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, SeamSolderValidationMessages.IDENTICAL_DEFAULT_BEANS.substring(0, 50) + ".*", 7, 12, 17);
	}

}