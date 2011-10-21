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
		String messageMask = SeamSolderValidationMessages.IDENTICAL_DEFAULT_BEANS.substring(0, 50) + ".*";
		IFile file = getTestProject().getFile(new Path("src/org/jboss/defaultbean/IdenticalDefaultBeans.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, messageMask, 7, 12, 17);
		
		file = getTestProject().getFile(new Path("src/org/jboss/defaultbean/validation/Test3.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, messageMask, 7, 13, 17);
	}

	public void testIncrementalValidationForIdenticalDefaultBeans() throws CoreException {
		String messageMask = SeamSolderValidationMessages.IDENTICAL_DEFAULT_BEANS.substring(0, 50) + ".*";

		String path1 = "src/org/jboss/defaultbean/validation/Test1.java";
		String path2 = "src/org/jboss/defaultbean/validation/Test2.java";
		String path2modified = "src/org/jboss/defaultbean/validation/Test2.modified";
		String path2original = "src/org/jboss/defaultbean/validation/Test2.original";

		IFile file1 = getTestProject().getFile(path1);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file1, messageMask, 10);
		IFile file2 = getTestProject().getFile(path2);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file2, messageMask, 9);
		
		GenericBeanValidationTest.writeFile(getTestProject(), path2modified, path2);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file1, messageMask);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file2, messageMask);

		GenericBeanValidationTest.writeFile(getTestProject(), path2original, path2);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file1, messageMask, 10);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file2, messageMask, 9);
	}

}