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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class GenericBeanValidationTest extends SeamSolderTest {

	public GenericBeanValidationTest() {}

	public void testRemovingGenericPointConfiguration() throws CoreException {
		/*
		 * Injection point: in class MessageManager
		 *     @Inject @Generic MessageQueue queue;
		 * There are 3 generic configuration points, two of them have same qualifier
		 * Assert that MessageLogger has error marker.
		 */
		IFile file = project.getFile(new Path("src/org/jboss/generic2/MessageLogger.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS + ".*", 15);


		/*
		 * Remove DurableQueueConfiguration.java with vetoed version.
		 * After that there are only 2 configurations, with different qualifiers.
		 */
		removeFile("src/org/jboss/generic2/DurableQueueConfiguration.java");

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS + ".*");

		/*
		 * Set original DurableQueueConfiguration.java back.
		 * After that there are only 2 configurations, with different qualifiers.
		 */
		writeFile("src/org/jboss/generic2/DurableQueueConfiguration.original",
				"src/org/jboss/generic2/DurableQueueConfiguration.java");

		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS + ".*", 15);
	}

	public void testDisablingGenericPointConfiguration() throws CoreException {
		/*
		 * Injection point: in class MessageManager
		 *     @Inject @Generic MessageQueue queue;
		 * There are 3 generic configuration points, two of them have same qualifier
		 * Assert that MessageLogger has error marker.
		 * in all cases bean is produced by MyGenericBean.createMyFirstBean()
		 */
		IFile file = project.getFile(new Path("src/org/jboss/generic2/MessageLogger.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS + ".*", 15);


		/*
		 * Replace DurableQueueConfiguration.java with not generic version.
		 * After that there are only 2 configurations.
		 */
		writeFile("src/org/jboss/generic2/DurableQueueConfiguration.notgeneric",
				"src/org/jboss/generic2/DurableQueueConfiguration.java");

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS + ".*");

		/*
		 * Set original DurableQueueConfiguration.java back.
		 * Assert that MessageLogger again has error marker.
		 */
		writeFile("src/org/jboss/generic2/DurableQueueConfiguration.original",
				"src/org/jboss/generic2/DurableQueueConfiguration.java");

		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS + ".*", 15);
	}

	void writeFile(String sourcePath, String targetPath) throws CoreException {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		try {
			IFile target = project.getFile(new Path(targetPath));
			IFile source = project.getFile(new Path(sourcePath));
			assertTrue(source.exists());
			if(!target.exists()) {
				target.create(source.getContents(), true, new NullProgressMonitor());
			} else {
				target.setContents(source.getContents(), true, false, new NullProgressMonitor());
			}
			JobUtils.waitForIdle();
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
			JobUtils.waitForIdle();
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
			JobUtils.waitForIdle();
		}
	}

	void removeFile(String targetPath) throws CoreException {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		try {
			IFile target = project.getFile(new Path(targetPath));
			assertTrue(target.exists());		
			target.delete(true, new NullProgressMonitor());
			JobUtils.waitForIdle();
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
			JobUtils.waitForIdle();
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
			JobUtils.waitForIdle();
		}
	}

}
