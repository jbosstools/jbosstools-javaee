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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.core.builder.JavaBuilder;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.cdi.seam.solder.core.validation.SeamSolderValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.Libs;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
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

	public void testBrokenGenericType() throws CoreException {
		/*
		 * BrokenGenericType is annotated @GenericType(MyGenericBean.class)
		 * Generic configuration types may not be generic beans.
		 */
		IFile file = getTestProject().getFile(new Path("src/org/jboss/generic/BrokenGenericType.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, SeamSolderValidationMessages.GENERIC_CONFIGURATION_TYPE_IS_A_GENERIC_BEAN, 5);
	}

	public void testBrokenGenericBean() throws CoreException {
		/*
		 * BrokenGenericBean is annotated @GenericConfiguration(Override.class)
		 * Annotation type mismatch: 'Override' is not a generic configuration annotation.
		 */
		IFile file = getTestProject().getFile(new Path("src/org/jboss/generic/BrokenGenericBean.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, SeamSolderValidationMessages.WRONG_GENERIC_CONFIGURATION_ANNOTATION_REFERENCE.substring(0, 25) + ".*", 8);
	}

	public void testRemovingGenericPointConfiguration() throws CoreException {
		/*
		 * Injection point: in class MessageManager
		 *     @Inject @Generic MessageQueue queue;
		 * There are 3 generic configuration points, two of them have same qualifier
		 * Assert that MessageLogger has error marker.
		 */
		IFile file = getTestProject().getFile(new Path("src/org/jboss/generic2/MessageLogger.java"));
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
		writeFile(getTestProject(), "src/org/jboss/generic2/DurableQueueConfiguration.original",
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
		IFile file = getTestProject().getFile(new Path("src/org/jboss/generic2/MessageLogger.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS + ".*", 15);


		/*
		 * Replace DurableQueueConfiguration.java with not generic version.
		 * After that there are only 2 configurations.
		 */
		writeFile(getTestProject(), "src/org/jboss/generic2/DurableQueueConfiguration.notgeneric",
				"src/org/jboss/generic2/DurableQueueConfiguration.java");

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS + ".*");

		/*
		 * Set original DurableQueueConfiguration.java back.
		 * Assert that MessageLogger again has error marker.
		 */
		writeFile(getTestProject(), "src/org/jboss/generic2/DurableQueueConfiguration.original",
				"src/org/jboss/generic2/DurableQueueConfiguration.java");

		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS + ".*", 15);
	}

	public void testWrongTypeOfGenericPointConfiguration() throws CoreException {
		/*
		 * Generic configuration point DurableQueueConfiguration has correct type.
		 */
		IFile file = getTestProject().getFile(new Path("src/org/jboss/generic2/DurableQueueConfiguration.java"));
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, SeamSolderValidationMessages.WRONG_TYPE_OF_GENERIC_CONFIGURATION_POINT + ".*");


		/*
		 * Remove DurableQueueConfiguration.java with vetoed version.
		 * Generic configuration point DurableQueueConfiguration has incorrect type.
		 */
		writeFile(getTestProject(), "src/org/jboss/generic2/DurableQueueConfiguration.wrongtype",
				"src/org/jboss/generic2/DurableQueueConfiguration.java");

		AbstractResourceMarkerTest.assertMarkerIsCreated(file, SeamSolderValidationMessages.WRONG_TYPE_OF_GENERIC_CONFIGURATION_POINT + ".*", 11);

		/*
		 * Set original DurableQueueConfiguration.java back.
		 * Generic configuration point DurableQueueConfiguration has correct type.
		 */
		writeFile(getTestProject(), "src/org/jboss/generic2/DurableQueueConfiguration.original",
				"src/org/jboss/generic2/DurableQueueConfiguration.java");

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, SeamSolderValidationMessages.WRONG_TYPE_OF_GENERIC_CONFIGURATION_POINT + ".*");
	}

	public void testDuplicateGenericPointConfiguration() throws CoreException {
		/*
		 * ConfigurationPointProducer has no duplicate generic configuration points,
		 * because one of them has an additional qualifier.
		 */
		IFile file = getTestProject().getFile(new Path("src/org/jboss/generic3/ConfigurationPointProducer.java"));
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, SeamSolderValidationMessages.AMBIGUOUS_GENERIC_CONFIGURATION_POINT.substring(0, 35) + ".*");


		/*
		 * Replace ConfigurationPointProducer with version where configuration points have same qualifiers.
		 * It has duplicate generic configuration points.
		 */
		writeFile(getTestProject(), "src/org/jboss/generic3/ConfigurationPointProducer.duplicates",
				"src/org/jboss/generic3/ConfigurationPointProducer.java");

		AbstractResourceMarkerTest.assertMarkerIsCreated(file, SeamSolderValidationMessages.AMBIGUOUS_GENERIC_CONFIGURATION_POINT.substring(0, 35) + ".*", 19, 25);

		/*
		 * Set original ConfigurationPointProducer.java back.
		 * ConfigurationPointProducer has no duplicate generic configuration points.
		 */
		writeFile(getTestProject(), "src/org/jboss/generic3/ConfigurationPointProducer.original",
				"src/org/jboss/generic3/ConfigurationPointProducer.java");

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, SeamSolderValidationMessages.AMBIGUOUS_GENERIC_CONFIGURATION_POINT.substring(0, 35) + ".*");
	}

	// https://issues.jboss.org/browse/JBIDE-9255
	public void testDuplicateNamedBeans() throws CoreException {
		IFile file = getTestProject().getFile(new Path("src/org/jboss/generic4/MyMessageQueues.java"));
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.DUPLCICATE_EL_NAME.substring(0, 11) + ".*", 10, 17);
	}

	public static void writeFile(IProject project, String sourcePath, String targetPath) throws CoreException {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile target = project.getFile(new Path(targetPath));
			IFile source = project.getFile(new Path(sourcePath));
			assertTrue(source.exists());
			if(!target.exists()) {
				target.create(source.getContents(), true, new NullProgressMonitor());
			} else {
				target.setContents(source.getContents(), true, false, new NullProgressMonitor());
			}
			if(targetPath.endsWith(".jar")) {
				kickJava(project);
			}
			TestUtil.validate(target);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
			JobUtils.waitForIdle();
		}
	}

	public void removeFile(String targetPath) throws CoreException {
		removeFile(getTestProject(), targetPath);
	}

	public static void removeFile(IProject project, String targetPath) throws CoreException {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile target = project.getFile(new Path(targetPath));
			assertTrue(target.exists());
			target.delete(true, new NullProgressMonitor());
			if(targetPath.endsWith(".jar")) {
				kickJava(project);
			}
			TestUtil.validate(target);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
			JobUtils.waitForIdle();
		}
	}

	static void kickJava(IProject project) throws CoreException {
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, "org.eclipse.jdt.core.javabuilder", null, new NullProgressMonitor());
		Libs libs = FileSystemsHelper.getLibs(EclipseResourceUtil.createObjectForResource(project));
		if(libs != null) {
			libs.requestForUpdate();
			libs.update();
		}
	}
}