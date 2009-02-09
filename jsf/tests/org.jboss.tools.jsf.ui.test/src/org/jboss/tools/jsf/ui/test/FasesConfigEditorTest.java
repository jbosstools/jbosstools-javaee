package org.jboss.tools.jsf.ui.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.jsf.ui.editor.FacesConfigEditor;
import org.jboss.tools.test.util.WorkbenchUtils;

public class FasesConfigEditorTest extends TestCase {
	
	IProject testWizards = null;
	
	@Override
	protected void setUp() throws Exception {
		testWizards = new TestWizardsProject().importProject();
	}

	/**
	 * Opens faces config editor on imported project and checks
	 * that it is opened and have three tabs in it
	 */
	public void testFacesConfigEditorIsOpened() {
		IEditorPart facesConfigEditor = WorkbenchUtils.openEditor("/TestWizards/WebContent/WEB-INF/faces-config.xml");
		assertTrue(facesConfigEditor instanceof EditorPartWrapper);
		EditorPartWrapper facesConfigEditorWrap = (EditorPartWrapper)facesConfigEditor;
		assertTrue(facesConfigEditorWrap.getEditor() instanceof FacesConfigEditor);
	}

	@Override
	protected void tearDown() throws Exception {
		WorkbenchUtils.closeAllEditors();
	}
}
