/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.core.itest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.batch.internal.core.validation.BatchValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

import junit.framework.TestCase;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchValidatorTest extends TestCase {
	public static String PROJECT_NAME = "TestProject"; //$NON-NLS-1$
	private IProject project;

	public BatchValidatorTest() {}

	@Override
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	public void testBatchlet() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job3.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.BATCHLET_IS_NOT_FOUND, new String[]{"batchlet"}), 3); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.BATCHLET_IS_EXPECTED, new String[]{"myDecider"}), 6); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.BATCHLET_IS_NOT_FOUND, new String[]{"batchlet_1"}), 9); //$NON-NLS-1$
	}

	public void testExecutionLoop() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job4.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.LOOP_IS_DETECTED, new String[]{"myBatchletStep1", "myBatchletStep2"}), 2);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.LOOP_IS_DETECTED, new String[]{"myBatchletStep2", "myBatchletStep3"}), 5); //$NON-NLS-1$ //$NON-NLS-2$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.LOOP_IS_DETECTED, new String[]{"myBatchletStep3", "myBatchletStep1"}), 8); //$NON-NLS-1$ //$NON-NLS-2$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.TRANSITION_TO_SELF, new String[]{}), 11);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.LOOP_IS_DETECTED, new String[]{"myBatchletStep5", "myBatchletStep1"}), 14); //$NON-NLS-1$ //$NON-NLS-21$
		
	}

	public void testFilter() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job5.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.EXCEPTION_CLASS_IS_NOT_FOUND, new String[]{"aaa"}), 8, 13); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.EXCEPTION_CLASS_DOES_NOT_EXTEND_JAVA_LANG_EXCEPTION, new String[]{"java.util.ArrayList"}), 10, 15); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.EXCEPTION_CLASS_DOES_NOT_EXTEND_JAVA_LANG_EXCEPTION, new String[]{"java.lang.ArrayIndexOutOfBoundsException"}), 9); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.EXCEPTION_CLASS_DOES_NOT_EXTEND_JAVA_LANG_EXCEPTION, new String[]{"java.lang.ArrayIndexOutOfBoundsException"}), 14); //$NON-NLS-1$
	}

	public void testRestart() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job6.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.TARGET_NOT_FOUND_ON_JOB_LEVEL, new String[]{"myFlow2"}), 8); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.TARGET_NOT_FOUND_ON_JOB_LEVEL, new String[]{"myStep1"}), 9); //$NON-NLS-1$
	}

	public void testReader() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job7.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.READER_IS_NOT_FOUND, new String[]{"myReader"}), 4); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.READER_IS_EXPECTED, new String[]{}), 4);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.READER_IS_NOT_FOUND, new String[]{"myReaderX"}), 12); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.READER_IS_EXPECTED, new String[]{}), 20);
	}

	public void testProcessor() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job7.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.PROCESSOR_IS_NOT_FOUND, new String[]{"myProcessor"}), 5); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.PROCESSOR_IS_EXPECTED, new String[]{}), 5);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.PROCESSOR_IS_NOT_FOUND, new String[]{"myProcessorX"}), 13); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.PROCESSOR_IS_EXPECTED, new String[]{}), 21);
	}

	public void testWriter() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job7.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.WRITER_IS_NOT_FOUND, new String[]{"myWriter"}), 6); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.WRITER_IS_EXPECTED, new String[]{}), 6);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.WRITER_IS_NOT_FOUND, new String[]{"myWriterX"}), 14); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.WRITER_IS_EXPECTED, new String[]{}), 22);
	}

	public void testCheckpointAlgorithm() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job7.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.CHECKPOINT_ALGORITHM_IS_NOT_FOUND, new String[]{"myCheckpointAlgorithm"}), 7); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.CHECKPOINT_ALGORITHM_IS_EXPECTED, new String[]{}), 7);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.CHECKPOINT_ALGORITHM_IS_NOT_FOUND, new String[]{"myCheckpointAlgorithmX"}), 15); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.CHECKPOINT_ALGORITHM_IS_EXPECTED, new String[]{}), 23);
	}

	public void testJobListeners() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job8.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.JOB_LISTENER_IS_NOT_FOUND, new String[]{"myJobListener"}), 7); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.JOB_LISTENER_IS_EXPECTED, new String[]{}), 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.JOB_LISTENER_IS_NOT_FOUND, new String[]{"myJobListenerX"}), 8); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.JOB_LISTENER_IS_EXPECTED, new String[]{}), 9);
	}

	public void testStepListeners() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job9.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.STEP_LISTENER_IS_NOT_FOUND, new String[]{"myStepListener"}), 6); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.STEP_LISTENER_IS_EXPECTED, new String[]{}), 6);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.STEP_LISTENER_IS_NOT_FOUND, new String[]{"myStepListenerX"}), 5); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.STEP_LISTENER_IS_EXPECTED, new String[]{}), 4, 7);
	}

	public void testMapper() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job10.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.MAPPER_IS_NOT_FOUND, new String[]{"myMapper"}), 5); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.MAPPER_IS_EXPECTED, new String[]{}), 5);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.MAPPER_IS_NOT_FOUND, new String[]{"myMapperX"}), 14); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.MAPPER_IS_EXPECTED, new String[]{}), 23);
	}

	public void testCollector() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job10.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.COLLECTOR_IS_NOT_FOUND, new String[]{"myMapper"}), 6); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.COLLECTOR_IS_EXPECTED, new String[]{}), 6);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.COLLECTOR_IS_NOT_FOUND, new String[]{"myCollectorX"}), 15); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.COLLECTOR_IS_EXPECTED, new String[]{}), 24);
	}

	public void testAnalyzer() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job10.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.ANALYZER_IS_NOT_FOUND, new String[]{"myAnalyzer"}), 7); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.ANALYZER_IS_EXPECTED, new String[]{}), 7);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.ANALYZER_IS_NOT_FOUND, new String[]{"myAnalyzerX"}), 16); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.ANALYZER_IS_EXPECTED, new String[]{}), 25);
	}

	public void testReducer() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job10.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.REDUCER_IS_NOT_FOUND, new String[]{"myReducer"}), 8); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(BatchValidationMessages.REDUCER_IS_EXPECTED, new String[]{}), 8);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.REDUCER_IS_NOT_FOUND, new String[]{"myReducerX"}), 17); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(BatchValidationMessages.REDUCER_IS_EXPECTED, new String[]{}), 26);
	}

}
