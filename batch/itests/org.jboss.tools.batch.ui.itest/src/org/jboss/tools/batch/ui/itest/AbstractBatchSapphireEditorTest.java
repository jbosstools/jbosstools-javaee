/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.itest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.SapphireEditorPagePart;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.batch.ui.editor.internal.model.Analyzer;
import org.jboss.tools.batch.ui.editor.internal.model.Batchlet;
import org.jboss.tools.batch.ui.editor.internal.model.CheckpointAlgorithm;
import org.jboss.tools.batch.ui.editor.internal.model.Chunk;
import org.jboss.tools.batch.ui.editor.internal.model.Collector;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.Job;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.batch.ui.editor.internal.model.Mapper;
import org.jboss.tools.batch.ui.editor.internal.model.Partition;
import org.jboss.tools.batch.ui.editor.internal.model.Processor;
import org.jboss.tools.batch.ui.editor.internal.model.Reader;
import org.jboss.tools.batch.ui.editor.internal.model.Reducer;
import org.jboss.tools.batch.ui.editor.internal.model.Step;
import org.jboss.tools.batch.ui.editor.internal.model.Writer;
import org.jboss.tools.test.util.WorkbenchUtils;

import junit.framework.TestCase;

/**
 * @author Tomas Milata
 */
public abstract class AbstractBatchSapphireEditorTest extends TestCase {
	protected IProject project;
	protected JobXMLEditor editor;

	@Override
	public void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
		assertNotNull(project);
	}

	protected String getProjectName() {
		return "BatchTestProject";
	}

	@Override
	protected void tearDown() throws Exception {
		if (editor != null) {
			editor.getSite().getPage().closeEditor(editor, false);
			editor = null;
		}
		super.tearDown();
	}

	protected JobXMLEditor openEditor(String fileName) {
		IFile testfile = project.getFile(fileName);
		assertTrue("Test file doesn't exist: " + project.getName() + "/" + fileName,
				(testfile.exists() && testfile.isAccessible()));

		IEditorPart editorPart = WorkbenchUtils.openEditor(project.getName() + "/" + fileName); //$NON-NLS-1$
		assertNotNull(editorPart);
		assertTrue(editorPart instanceof JobXMLEditor);
		
		return (JobXMLEditor) editorPart;
	}

	public Job openJob(String fileName) {
		editor = openEditor(fileName);
		return editor.getSchema();		
	}

	protected SapphireEditorPagePart getDiagramPage() {
		SapphireEditorPagePart page = editor.getEditorPagePart("Diagram");
		assertNotNull(page);
		return page;
	}

	protected Batchlet findBatchlet(ElementList<FlowElement> es, int i) {
		Step step = findStep(es, i);
		if(step != null && !step.getBatchletOrChunk().isEmpty() && step.getBatchletOrChunk().get(0) instanceof Batchlet) {
			return (Batchlet)step.getBatchletOrChunk().get(0);
		}
		return null;
	}

	protected Step findStep(ElementList<FlowElement> es, int i) {
		if(es.get(i) instanceof Step) {
			return (Step)es.get(i);
		}
		return null;
	}

	protected Partition findPartition(ElementList<FlowElement> es, int i) {
		Step step = findStep(es, i);
		if(step != null && step.getPartition() != null) {
			return step.getPartition().content();
		}
		return null;
	}

	protected Chunk findChunk(ElementList<FlowElement> es, int i) {
		Step step = findStep(es, i);
		if(step != null && !step.getBatchletOrChunk().isEmpty() && step.getBatchletOrChunk().get(0) instanceof Chunk) {
			return (Chunk)step.getBatchletOrChunk().get(0);
		}
		return null;
	}

	protected Reader findReader(ElementList<FlowElement> es, int i) {
		Chunk chunk = findChunk(es, i);
		return (chunk != null) ? chunk.getReader() : null;
	}

	protected Processor findProcessor(ElementList<FlowElement> es, int i) {
		Chunk chunk = findChunk(es, i);
		return (chunk != null) ? chunk.getProcessor().content() : null;
	}

	protected Writer findWriter(ElementList<FlowElement> es, int i) {
		Chunk chunk = findChunk(es, i);
		return (chunk != null) ? chunk.getWriter() : null;
	}

	protected CheckpointAlgorithm findCheckpointAlgorithm(ElementList<FlowElement> es, int i) {
		Chunk chunk = findChunk(es, i);
		return (chunk != null) ? chunk.getCheckpointAlgorithm().content() : null;
	}

	protected Mapper findMapper(ElementList<FlowElement> es, int i) {
		Partition partition = findPartition(es, i);
		return (partition != null) ? partition.getMapper().content() : null;
	}

	protected Collector findCollector(ElementList<FlowElement> es, int i) {
		Partition partition = findPartition(es, i);
		return (partition != null) ? partition.getCollector().content() : null;
	}

	protected Analyzer findAnalyzer(ElementList<FlowElement> es, int i) {
		Partition partition = findPartition(es, i);
		return (partition != null) ? partition.getAnalyzer().content() : null;
	}

	protected Reducer findReducer(ElementList<FlowElement> es, int i) {
		Partition partition = findPartition(es, i);
		return (partition != null) ? partition.getReducer().content() : null;
	}


}
