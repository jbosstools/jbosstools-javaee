/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.test;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
//import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.SapphireEditorPagePart;
import org.eclipse.sapphire.ui.forms.MasterDetailsEditorPagePart;
import org.eclipse.sapphire.ui.forms.swt.MasterDetailsEditorPage;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.batch.ui.editor.internal.model.BatchletOrChunk;
import org.jboss.tools.batch.ui.editor.internal.model.Chunk;
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

	public void setUp() {
		project =  ResourcesPlugin.getWorkspace().getRoot().getProject("TestProject");
		assertNotNull(project);
	}

	protected void tearDown() throws Exception {
		if(editor != null) {
			editor.getSite().getPage().closeEditor(editor, false);
			editor = null;
		}
		super.tearDown();
	}

	public void testEditor() {
		 editor = openEditor("job.xml");
		 JobXMLEditor jobEditor = (JobXMLEditor)editor;

		 Job job = jobEditor.getSchema();
		 assertNotNull(job);
		 ElementList<FlowElement> es = job.getFlowElements();
		 assertEquals(1, es.size());
		 Step step = (Step)es.get(0);
		 ElementHandle<BatchletOrChunk> ch = step.getBatchletOrChunk();
		 Chunk chunk = (Chunk)ch.content();
		 
		 assertEquals("myReader", chunk.getReader().getRef().content());
		 assertEquals("myItemWriter", chunk.getWriter().getRef().content());
		 assertEquals("myItemProcessor", chunk.getProcessor().content().getRef().content());

		 StructuredTextEditor textEditor = jobEditor.getSourceEditor();
		 assertNotNull(textEditor);

		 MasterDetailsEditorPage formEditor = jobEditor.getFormEditor();
		 assertNotNull(formEditor);
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
