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

import java.util.Collection;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.batch.internal.core.BatchArtifactType;
import org.jboss.tools.batch.internal.core.BatchProjectFactory;
import org.jboss.tools.batch.internal.core.IBatchArtifact;
import org.jboss.tools.batch.internal.core.IBatchProject;
import org.jboss.tools.batch.internal.core.IBatchProperty;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchModelTest extends TestCase {
	private IProject project;

	@Override
	public void setUp() {
		project =  ResourcesPlugin.getWorkspace().getRoot().getProject("TestProject");
		assertNotNull(project);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testAllArtifacts() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);
		Collection<IBatchArtifact> cs = batchProject.getAllArtifacts();
		assertFalse(cs.isEmpty());
	}

	public void testBatchlet() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.BATCHLET);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.BATCHLET);
		}

		cs = batchProject.getArtifacts("batchlet1");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("batchlet1", b.getName());

		Collection<IBatchProperty> ps = b.getProperties();
		assertEquals(1, ps.size());
		IBatchProperty p = ps.iterator().next();
		assertEquals("worktime", p.getPropertyName());
		assertNotNull(p.getField());
		assertTrue(p.getArtifact() == b);
		assertNotNull(p.getInjectDeclaration());
		assertNotNull(p.getBatchPropertyDeclaration());
	}

	public void testDecider() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.DECIDER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.DECIDER);
		}

		cs = batchProject.getArtifacts("myDecider");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myDecider", b.getName());
	}

	public void testReader() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.ITEM_READER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.ITEM_READER);
		}

		cs = batchProject.getArtifacts("myReader");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myReader", b.getName());
	}

	public void testProcessor() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.ITEM_PROCESSOR);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.ITEM_PROCESSOR);
		}

		cs = batchProject.getArtifacts("myProcessor");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myProcessor", b.getName());
	}

	public void testCheckpointAlgorithm() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.CHECKPOINT_ALGORITHM);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.CHECKPOINT_ALGORITHM);
		}

		cs = batchProject.getArtifacts("myCheckpointAlgorithm");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myCheckpointAlgorithm", b.getName());
	}

	public void testWriter() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.ITEM_WRITER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.ITEM_WRITER);
		}

		cs = batchProject.getArtifacts("myWriter");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myWriter", b.getName());
	}

	public void testAnalyzerInJar() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.PARTITION_ANALYZER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.PARTITION_ANALYZER);
		}

		cs = batchProject.getArtifacts("myAnalyzer");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myAnalyzer", b.getName());
	}

	public void testCollectorInJar() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.PARTITION_COLLECTOR);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.PARTITION_COLLECTOR);
		}

		cs = batchProject.getArtifacts("myCollector");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myCollector", b.getName());
	}

	public void testMapperInJar() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.PARTITION_MAPPER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.PARTITION_MAPPER);
		}

		cs = batchProject.getArtifacts("myMapper");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myMapper", b.getName());
	}

	public void testReducerInJar() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.PARTITION_REDUCER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.PARTITION_REDUCER);
		}

		cs = batchProject.getArtifacts("myReducer");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myReducer", b.getName());
	}

	public void testJobListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.JOB_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.JOB_LISTENER);
		}

		cs = batchProject.getArtifacts("myJobListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myJobListener", b.getName());
	}

	public void testStepListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.STEP_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.STEP_LISTENER);
		}

		cs = batchProject.getArtifacts("myStepListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myStepListener", b.getName());
	}

	public void testChunkListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.CHUNK_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.CHUNK_LISTENER);
		}

		cs = batchProject.getArtifacts("myChunkListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myChunkListener", b.getName());
	}

	public void testItemReadListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.ITEM_READ_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.ITEM_READ_LISTENER);
		}

		cs = batchProject.getArtifacts("myItemReadListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myItemReadListener", b.getName());
	}

	public void testItemProcessListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.ITEM_PROCESS_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.ITEM_PROCESS_LISTENER);
		}

		cs = batchProject.getArtifacts("myItemProcessListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myItemProcessListener", b.getName());
	}

	public void testItemWriteListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.ITEM_WRITE_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.ITEM_WRITE_LISTENER);
		}

		cs = batchProject.getArtifacts("myItemWriteListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myItemWriteListener", b.getName());
	}

	public void testSkipReadListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.SKIP_READ_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.SKIP_READ_LISTENER);
		}

		cs = batchProject.getArtifacts("mySkipReadListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("mySkipReadListener", b.getName());
	}

	public void testSkipProcessListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.SKIP_PROCESS_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.SKIP_PROCESS_LISTENER);
		}

		cs = batchProject.getArtifacts("mySkipProcessListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("mySkipProcessListener", b.getName());
	}

	public void testSkipWriteListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.SKIP_WRITE_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.SKIP_WRITE_LISTENER);
		}

		cs = batchProject.getArtifacts("mySkipWriteListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("mySkipWriteListener", b.getName());
	}

	public void testRetryReadListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.RETRY_READ_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.RETRY_READ_LISTENER);
		}

		cs = batchProject.getArtifacts("myRetryReadListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myRetryReadListener", b.getName());
	}

	public void testRetryProcessListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.RETRY_PROCESS_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.RETRY_PROCESS_LISTENER);
		}

		cs = batchProject.getArtifacts("myRetryProcessListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myRetryProcessListener", b.getName());
	}

	public void testRetryWriteListener() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);

		Collection<IBatchArtifact> cs = batchProject.getArtifacts(BatchArtifactType.RETRY_WRITE_LISTENER);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == BatchArtifactType.RETRY_WRITE_LISTENER);
		}

		cs = batchProject.getArtifacts("myRetryWriteListener");
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		assertNotNull(b.getNamedDeclaration());
		assertEquals("myRetryWriteListener", b.getName());
	}

	public void testBatchJobs() {
		IBatchProject batchProject = BatchProjectFactory.getBatchProject(project, true);
		assertNotNull(batchProject);
		
		Set<IFile> batchJobs = batchProject.getDeclaredBatchJobs();
		assertFalse(batchJobs.isEmpty());
		for (IFile batchJob: batchJobs) {
			assertTrue(batchJob.exists());
		}
		
	}

}
