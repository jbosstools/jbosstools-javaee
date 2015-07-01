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
import org.jboss.tools.batch.ui.editor.internal.model.BatchletOrChunk;
import org.jboss.tools.batch.ui.editor.internal.model.Chunk;
import org.jboss.tools.batch.ui.editor.internal.model.Flow;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.Job;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.batch.ui.editor.internal.model.Step;
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

	public IEditorPart openEditor(String fileName) {
		IFile testfile = project.getFile(fileName);
		assertTrue("Test file doesn't exist: " + project.getName() + "/" + fileName, 
				(testfile.exists() && testfile.isAccessible()));

		IEditorPart editorPart = WorkbenchUtils.openEditor(project.getName()+"/"+ fileName); //$NON-NLS-1$

		assertTrue(editorPart instanceof JobXMLEditor);
		return editorPart;
	}
}
