/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 * Tomas Milata - Added Batch diagram editor (JBIDE-19717).
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.itest;

import junit.framework.TestCase;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
//import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.forms.swt.MasterDetailsEditorPage;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.batch.internal.core.validation.BatchValidationMessages;
import org.jboss.tools.batch.ui.editor.internal.model.Analyzer;
import org.jboss.tools.batch.ui.editor.internal.model.Batchlet;
import org.jboss.tools.batch.ui.editor.internal.model.BatchletOrChunk;
import org.jboss.tools.batch.ui.editor.internal.model.CheckpointAlgorithm;
import org.jboss.tools.batch.ui.editor.internal.model.Chunk;
import org.jboss.tools.batch.ui.editor.internal.model.Collector;
import org.jboss.tools.batch.ui.editor.internal.model.Flow;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.Job;
import org.jboss.tools.batch.ui.editor.internal.model.JobListener;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.batch.ui.editor.internal.model.Mapper;
import org.jboss.tools.batch.ui.editor.internal.model.Partition;
import org.jboss.tools.batch.ui.editor.internal.model.Processor;
import org.jboss.tools.batch.ui.editor.internal.model.Reader;
import org.jboss.tools.batch.ui.editor.internal.model.Reducer;
import org.jboss.tools.batch.ui.editor.internal.model.RefAttributeElement;
import org.jboss.tools.batch.ui.editor.internal.model.Step;
import org.jboss.tools.batch.ui.editor.internal.model.StepListener;
import org.jboss.tools.batch.ui.editor.internal.model.Writer;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author 
 */
public class BatchEditorTest extends TestCase {
	IEditorPart editor;

	private IProject project;

	@Override
	public void setUp() {
		project =  ResourcesPlugin.getWorkspace().getRoot().getProject("BatchTestProject");
		assertNotNull(project);
	}

	@Override
	protected void tearDown() throws Exception {
		if(editor != null) {
			editor.getSite().getPage().closeEditor(editor, false);
			editor = null;
		}
		super.tearDown();
	}

	public void testEditor() {
		 editor = openEditor("src/META-INF/batch-jobs/job.xml");
		 JobXMLEditor jobEditor = (JobXMLEditor)editor;

		 Job job = jobEditor.getSchema();
		 assertNotNull(job);
		 ElementList<FlowElement> es = job.getFlowElements();
		 assertEquals(1, es.size());
		 Step step = (Step)es.get(0);
		 ElementList<BatchletOrChunk> ch = step.getBatchletOrChunk();
		 Chunk chunk = (Chunk) (ch.iterator().next());
		 
		 assertEquals("myReader", chunk.getReader().getRef().content());
		 assertEquals("myItemWriter", chunk.getWriter().getRef().content());
		 assertEquals("myItemProcessor", chunk.getProcessor().content().getRef().content());

		 StructuredTextEditor textEditor = jobEditor.getSourceEditor();
		 assertNotNull(textEditor);

		 MasterDetailsEditorPage formEditor = jobEditor.getFormEditor();
		 assertNotNull(formEditor);
	}

	public void testValidation() {
		editor = openEditor("src/META-INF/batch-jobs/job-validation-1.xml");
		JobXMLEditor jobEditor = (JobXMLEditor)editor;
		Job job = jobEditor.getSchema();
		ElementList<FlowElement> es = job.getFlowElements();
		Step step = (Step)es.get(0);
		Status status = step.getId().validation();
		assertTrue(status.ok());
		
		step = (Step)es.get(1);
		status = step.getId().validation();
		assertFalse(status.ok());
		
		Flow flow = (Flow)es.get(2);
		status = flow.getId().validation();
		assertFalse(status.ok());
	}

	public void testRestartableInvalid() throws Exception {
		editor = openEditor("src/META-INF/batch-jobs/job-restartable-invalid.xml");
		JobXMLEditor jobEditor = (JobXMLEditor)editor;
		Job job = jobEditor.getSchema();
		Value<Boolean> restartable = job.getRestartable();
		assertEquals("xxxx", restartable.text());
		assertEquals(Boolean.TRUE, restartable.content()); //default value
		Status s = restartable.validation();
		assertFalse(s.ok());
	}

	public void testRestartableValid() throws Exception {
		editor = openEditor("src/META-INF/batch-jobs/job-restartable-valid.xml");
		JobXMLEditor jobEditor = (JobXMLEditor)editor;
		Job job = jobEditor.getSchema();
		Value<Boolean> restartable = job.getRestartable();
		assertEquals("false", restartable.text());
		assertEquals(Boolean.FALSE, restartable.content());
		Status s = restartable.validation();
		assertTrue(s.ok());
	}

	public void testBatchletRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job3.xml");
		ElementList<FlowElement> es = job.getFlowElements();

		assertRefValidation(findBatchlet(es, 0), null);
		assertRefValidation(findBatchlet(es, 2), BatchValidationMessages.BATCHLET_IS_NOT_FOUND);
		assertRefValidation(findBatchlet(es, 1), BatchValidationMessages.BATCHLET_IS_EXPECTED);
	}

	Batchlet findBatchlet(ElementList<FlowElement> es, int i) {
		Step step = findStep(es, i);
		if(step != null && !step.getBatchletOrChunk().isEmpty() && step.getBatchletOrChunk().get(0) instanceof Batchlet) {
			return (Batchlet)step.getBatchletOrChunk().get(0);
		}
		return null;
	}

	public void testReaderRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job7.xml");
		ElementList<FlowElement> es = job.getFlowElements();

		assertRefValidation(findReader(es, 0), null);
		assertRefValidation(findReader(es, 1), BatchValidationMessages.READER_IS_NOT_FOUND);
		assertRefValidation(findReader(es, 2), BatchValidationMessages.READER_IS_EXPECTED);
	}

	Step findStep(ElementList<FlowElement> es, int i) {
		if(es.get(i) instanceof Step) {
			return (Step)es.get(i);
		}
		return null;
	}

	Partition findPartition(ElementList<FlowElement> es, int i) {
		Step step = findStep(es, i);
		if(step != null && step.getPartition() != null) {
			return step.getPartition().content();
		}
		return null;
	}

	Chunk findChunk(ElementList<FlowElement> es, int i) {
		Step step = findStep(es, i);
		if(step != null && !step.getBatchletOrChunk().isEmpty() && step.getBatchletOrChunk().get(0) instanceof Chunk) {
			return (Chunk)step.getBatchletOrChunk().get(0);
		}
		return null;
	}

	Reader findReader(ElementList<FlowElement> es, int i) {
		Chunk chunk = findChunk(es, i);
		return (chunk != null) ? chunk.getReader() : null;
	}

	public void testProcessorRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job7.xml");
		ElementList<FlowElement> es = job.getFlowElements();

		assertRefValidation(findProcessor(es, 0), null);
		assertRefValidation(findProcessor(es, 1), BatchValidationMessages.PROCESSOR_IS_NOT_FOUND);
		assertRefValidation(findProcessor(es, 2), BatchValidationMessages.PROCESSOR_IS_EXPECTED);
	}

	Processor findProcessor(ElementList<FlowElement> es, int i) {
		Chunk chunk = findChunk(es, i);
		return (chunk != null) ? chunk.getProcessor().content() : null;
	}

	public void testWriterRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job7.xml");
		ElementList<FlowElement> es = job.getFlowElements();

		assertRefValidation(findWriter(es, 0), null);
		assertRefValidation(findWriter(es, 1), BatchValidationMessages.WRITER_IS_NOT_FOUND);
		assertRefValidation(findWriter(es, 2), BatchValidationMessages.WRITER_IS_EXPECTED);
	}

	Writer findWriter(ElementList<FlowElement> es, int i) {
		Chunk chunk = findChunk(es, i);
		return (chunk != null) ? chunk.getWriter() : null;
	}

	public void testCheckpointAlgorithmRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job7.xml");
		ElementList<FlowElement> es = job.getFlowElements();

		assertRefValidation(findCheckpointAlgorithm(es, 0), null);
		assertRefValidation(findCheckpointAlgorithm(es, 1), BatchValidationMessages.CHECKPOINT_ALGORITHM_IS_NOT_FOUND);
		assertRefValidation(findCheckpointAlgorithm(es, 2), BatchValidationMessages.CHECKPOINT_ALGORITHM_IS_EXPECTED);
	}

	CheckpointAlgorithm findCheckpointAlgorithm(ElementList<FlowElement> es, int i) {
		Chunk chunk = findChunk(es, i);
		return (chunk != null) ? chunk.getCheckpointAlgorithm().content() : null;
	}

	public void testJobListenerRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job8.xml");
		ElementList<JobListener> es = job.getListeners();

		assertRefValidation(es.get(0), null);
		assertRefValidation(es.get(1), BatchValidationMessages.JOB_LISTENER_IS_NOT_FOUND);
		assertRefValidation(es.get(2), BatchValidationMessages.JOB_LISTENER_IS_EXPECTED);
	}

	public void testStepListenerRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job9.xml");
		ElementList<StepListener> es = findStep(job.getFlowElements(), 0).getListeners();

		assertRefValidation(es.get(2), null);
		assertRefValidation(es.get(1), BatchValidationMessages.STEP_LISTENER_IS_NOT_FOUND);
		assertRefValidation(es.get(0), BatchValidationMessages.STEP_LISTENER_IS_EXPECTED);
	}

	public void testMapperRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job10.xml");
		ElementList<FlowElement> es = job.getFlowElements();

		assertRefValidation(findMapper(es, 0), null);
		assertRefValidation(findMapper(es, 1), BatchValidationMessages.MAPPER_IS_NOT_FOUND);
		assertRefValidation(findMapper(es, 2), BatchValidationMessages.MAPPER_IS_EXPECTED);
	}

	Mapper findMapper(ElementList<FlowElement> es, int i) {
		Partition partition = findPartition(es, i);
		return (partition != null) ? partition.getMapper().content() : null;
	}

	public void testCollectorRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job10.xml");
		ElementList<FlowElement> es = job.getFlowElements();

		assertRefValidation(findCollector(es, 0), null);
		assertRefValidation(findCollector(es, 1), BatchValidationMessages.COLLECTOR_IS_NOT_FOUND);
		assertRefValidation(findCollector(es, 2), BatchValidationMessages.COLLECTOR_IS_EXPECTED);
	}

	Collector findCollector(ElementList<FlowElement> es, int i) {
		Partition partition = findPartition(es, i);
		return (partition != null) ? partition.getCollector().content() : null;
	}

	public void testAnalyzerRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job10.xml");
		ElementList<FlowElement> es = job.getFlowElements();

		assertRefValidation(findAnalyzer(es, 0), null);
		assertRefValidation(findAnalyzer(es, 1), BatchValidationMessages.ANALYZER_IS_NOT_FOUND);
		assertRefValidation(findAnalyzer(es, 2), BatchValidationMessages.ANALYZER_IS_EXPECTED);
	}

	Analyzer findAnalyzer(ElementList<FlowElement> es, int i) {
		Partition partition = findPartition(es, i);
		return (partition != null) ? partition.getAnalyzer().content() : null;
	}

	public void testReducerRefValidation() throws Exception {
		Job job = openJob("src/META-INF/batch-jobs/job10.xml");
		ElementList<FlowElement> es = job.getFlowElements();

		assertRefValidation(findReducer(es, 0), null);
		assertRefValidation(findReducer(es, 1), BatchValidationMessages.REDUCER_IS_NOT_FOUND);
		assertRefValidation(findReducer(es, 2), BatchValidationMessages.REDUCER_IS_EXPECTED);
	}

	Reducer findReducer(ElementList<FlowElement> es, int i) {
		Partition partition = findPartition(es, i);
		return (partition != null) ? partition.getReducer().content() : null;
	}

	void assertRefValidation(RefAttributeElement artifact, String messagePattern) {
		assertNotNull(artifact);
		Status status = artifact.getRef().validation();
		if(messagePattern == null) {
			assertTrue(status.ok());
		} else {
			assertFalse(status.ok());
			String message = MessageFormat.format(messagePattern, artifact.getRef().content());
			assertEquals(message,  status.message());
		}
	}

	public Job openJob(String fileName) {
		editor = openEditor(fileName);
		JobXMLEditor jobEditor = (JobXMLEditor)editor;
		return jobEditor.getSchema();		
	}

	public IEditorPart openEditor(String fileName) {
		IFile testfile = project.getFile(fileName);
		assertTrue("Test file doesn't exist: " + project.getName() + "/" + fileName, 
				(testfile.exists() && testfile.isAccessible()));

		IEditorPart editorPart = WorkbenchUtils.openEditor(project.getName()+"/"+ fileName); //$NON-NLS-1$

		assertTrue(editorPart instanceof JobXMLEditor);
		return editorPart;
	}
}
