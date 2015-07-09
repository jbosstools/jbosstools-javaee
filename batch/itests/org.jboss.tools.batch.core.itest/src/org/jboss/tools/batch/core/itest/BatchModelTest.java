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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.util.FileUtil;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchModelTest extends TestCase {
	protected IProject project;
	protected IBatchProject batchProject;

	@Override
	public void setUp() {
		project =  ResourcesPlugin.getWorkspace().getRoot().getProject("BatchTestProject");
		assertNotNull(project);
		batchProject = BatchCorePlugin.getBatchProject(project, true);
		assertNotNull(batchProject);
		assertTrue(((BatchProject)batchProject).exists());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAllArtifacts() {
		assertNotNull(batchProject);
		Collection<IBatchArtifact> cs = batchProject.getAllArtifacts();
		assertFalse(cs.isEmpty());
		int total = cs.size();
		int total2 = 0;
		for (BatchArtifactType t: BatchArtifactType.values()) {
			cs = batchProject.getArtifacts(t);
			total2 += cs.size();
		}
		assertEquals(total, total2);
	}

	IBatchArtifact assertArtifactByNameAndType(String name, BatchArtifactType type, boolean checkNamedDeclaration) {
		Collection<IBatchArtifact> cs = batchProject.getArtifacts(name);
		assertEquals(1, cs.size());

		IBatchArtifact b = cs.iterator().next();
		if(checkNamedDeclaration) {
			assertNotNull(b.getNamedDeclaration());
		}
		assertEquals(name, b.getName());
		assertTrue(b.getArtifactType() == type);
		return b;
	}

	IBatchArtifact assertArtifactByNameAndType(String name, BatchArtifactType type) {
		return assertArtifactByNameAndType(name, type, true);
	}

	Collection<IBatchArtifact> assertArtifactsByType(BatchArtifactType type) {
		Collection<IBatchArtifact> cs = batchProject.getArtifacts(type);
		assertFalse(cs.isEmpty());
		for (IBatchArtifact a: cs) {
			assertTrue(a.getArtifactType() == type);
		}
		return cs;
	}

	public void testBatchlet() {
		assertArtifactsByType(BatchArtifactType.BATCHLET);

		IBatchArtifact b = assertArtifactByNameAndType("batchlet1", BatchArtifactType.BATCHLET);

		Collection<IBatchProperty> ps = b.getProperties();
		assertEquals(1, ps.size());
		IBatchProperty p = ps.iterator().next();
		assertEquals("worktime", p.getPropertyName());
		assertNotNull(p.getField());
		assertTrue(p.getArtifact() == b);
		assertNotNull(p.getInjectDeclaration());
		assertNotNull(p.getBatchPropertyDeclaration());
		
	}

	public void testBatchArtifactReferences() {
		IBatchArtifact b = assertArtifactByNameAndType("batchlet1", BatchArtifactType.BATCHLET);
		assertTextSourceReferences(b.getReferences(), b.getName());
	}

	public void testBatchPropertyReferences() {
		IBatchArtifact b = assertArtifactByNameAndType("batchlet1", BatchArtifactType.BATCHLET);
		IBatchProperty p = b.getProperty("worktime");
		assertTextSourceReferences(p.getReferences(), p.getPropertyName());
	}

	public void testTypeReferences() {
		String typeName = "java.lang.ArrayIndexOutOfBoundsException";
		IType type = ((BatchProject)batchProject).getType(typeName);
		assertNotNull(type);
		assertTextSourceReferences(batchProject.getReferences(type), typeName);
	}

	void assertTextSourceReferences(Collection<ITextSourceReference> references, String expectedContent) {
		assertFalse(references.isEmpty());
		for (ITextSourceReference ref: references) {
			assertTextSourceReference(ref, expectedContent);
		}
	}

	void assertTextSourceReference(ITextSourceReference ref, String expectedContent) {
		try {
			String text = FileUtil.readStream((IFile)ref.getResource());
			String content = text.substring(ref.getStartPosition(), ref.getStartPosition() + ref.getLength());
			assertEquals(expectedContent, content);
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}

	public void testDecider() {
		assertArtifactsByType(BatchArtifactType.DECIDER);
		assertArtifactByNameAndType("myDecider", BatchArtifactType.DECIDER);
	}

	public void testReader() {
		assertArtifactsByType(BatchArtifactType.ITEM_READER);
		assertArtifactByNameAndType("myReader", BatchArtifactType.ITEM_READER);
	}

	public void testProcessor() {
		assertArtifactsByType(BatchArtifactType.ITEM_PROCESSOR);
		assertArtifactByNameAndType("myProcessor", BatchArtifactType.ITEM_PROCESSOR);
	}

	public void testCheckpointAlgorithm() {
		assertArtifactsByType(BatchArtifactType.CHECKPOINT_ALGORITHM);
		assertArtifactByNameAndType("myCheckpointAlgorithm", BatchArtifactType.CHECKPOINT_ALGORITHM);
	}

	public void testWriter() {
		assertArtifactsByType(BatchArtifactType.ITEM_WRITER);
		assertArtifactByNameAndType("myWriter", BatchArtifactType.ITEM_WRITER);
	}

	public void testAnalyzerInJar() {
		assertArtifactsByType(BatchArtifactType.PARTITION_ANALYZER);
		assertArtifactByNameAndType("myAnalyzer", BatchArtifactType.PARTITION_ANALYZER);
	}

	public void testCollectorInJar() {
		assertArtifactsByType(BatchArtifactType.PARTITION_COLLECTOR);
		assertArtifactByNameAndType("myCollector", BatchArtifactType.PARTITION_COLLECTOR);
	}

	public void testMapperInJar() {
		assertArtifactsByType(BatchArtifactType.PARTITION_MAPPER);
		assertArtifactByNameAndType("myMapper", BatchArtifactType.PARTITION_MAPPER);
	}

	public void testReducerInJar() {
		assertArtifactsByType(BatchArtifactType.PARTITION_REDUCER);
		assertArtifactByNameAndType("myReducer", BatchArtifactType.PARTITION_REDUCER);
	}

	public void testJobListener() {
		assertArtifactsByType(BatchArtifactType.JOB_LISTENER);
		assertArtifactByNameAndType("myJobListener", BatchArtifactType.JOB_LISTENER);
	}

	public void testStepListener() {
		assertArtifactsByType(BatchArtifactType.STEP_LISTENER);
		assertArtifactByNameAndType("myStepListener", BatchArtifactType.STEP_LISTENER);
	}

	public void testChunkListener() {
		assertArtifactsByType(BatchArtifactType.CHUNK_LISTENER);
		assertArtifactByNameAndType("myChunkListener", BatchArtifactType.CHUNK_LISTENER);
	}

	public void testItemReadListener() {
		assertArtifactsByType(BatchArtifactType.ITEM_READ_LISTENER);
		assertArtifactByNameAndType("myItemReadListener", BatchArtifactType.ITEM_READ_LISTENER);
	}

	public void testItemProcessListener() {
		assertArtifactsByType(BatchArtifactType.ITEM_PROCESS_LISTENER);
		assertArtifactByNameAndType("myItemProcessListener", BatchArtifactType.ITEM_PROCESS_LISTENER);
	}

	public void testItemWriteListener() {
		assertArtifactsByType(BatchArtifactType.ITEM_WRITE_LISTENER);
		assertArtifactByNameAndType("myItemWriteListener", BatchArtifactType.ITEM_WRITE_LISTENER);
	}

	public void testSkipReadListener() {
		assertArtifactsByType(BatchArtifactType.SKIP_READ_LISTENER);
		assertArtifactByNameAndType("mySkipReadListener", BatchArtifactType.SKIP_READ_LISTENER);
	}

	public void testSkipProcessListener() {
		assertArtifactsByType(BatchArtifactType.SKIP_PROCESS_LISTENER);
		assertArtifactByNameAndType("mySkipProcessListener", BatchArtifactType.SKIP_PROCESS_LISTENER);
	}

	public void testSkipWriteListener() {
		assertArtifactsByType(BatchArtifactType.SKIP_WRITE_LISTENER);
		assertArtifactByNameAndType("mySkipWriteListener", BatchArtifactType.SKIP_WRITE_LISTENER);
	}

	public void testRetryReadListener() {
		assertArtifactsByType(BatchArtifactType.RETRY_READ_LISTENER);
		assertArtifactByNameAndType("myRetryReadListener", BatchArtifactType.RETRY_READ_LISTENER);
	}

	public void testRetryProcessListener() {
		assertArtifactsByType(BatchArtifactType.RETRY_PROCESS_LISTENER);
		assertArtifactByNameAndType("myRetryProcessListener", BatchArtifactType.RETRY_PROCESS_LISTENER);
	}

	public void testRetryWriteListener() {
		assertArtifactsByType(BatchArtifactType.RETRY_WRITE_LISTENER);
		assertArtifactByNameAndType("myRetryWriteListener", BatchArtifactType.RETRY_WRITE_LISTENER);
	}

	public void testBatchJobs() {
		Set<IFile> batchJobs = batchProject.getDeclaredBatchJobs();
		assertFalse(batchJobs.isEmpty());
		for (IFile batchJob: batchJobs) {
			assertTrue(batchJob.exists());
		}		
	}

	public void testArtifactRegisteredInBatchXML() {
		assertArtifactByNameAndType("my_other_job_listener", BatchArtifactType.JOB_LISTENER, false);
	}

}
