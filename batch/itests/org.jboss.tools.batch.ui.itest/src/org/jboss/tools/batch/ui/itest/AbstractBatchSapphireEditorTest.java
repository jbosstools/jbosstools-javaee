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
import org.eclipse.sapphire.ui.SapphireEditorPagePart;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.test.util.WorkbenchUtils;

import junit.framework.TestCase;

/**
 * @author Tomas Milata
 */
public abstract class AbstractBatchSapphireEditorTest extends TestCase {

	private IProject project;
	protected JobXMLEditor editor;

	@Override
	public void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("BatchTestProject");
		assertNotNull(project);
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

	protected SapphireEditorPagePart getDiagramPage() {
		SapphireEditorPagePart page = editor.getEditorPagePart("Diagram");
		assertNotNull(page);
		return page;
	}
}
