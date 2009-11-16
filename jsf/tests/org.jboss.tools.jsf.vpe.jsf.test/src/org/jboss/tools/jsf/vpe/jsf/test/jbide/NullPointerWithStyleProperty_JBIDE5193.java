package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

public class NullPointerWithStyleProperty_JBIDE5193 extends VpeTest {

	private static final String TEST_PAGE_NAME = "JBIDE/5193/JBIDE5193.jsp"; //$NON-NLS-1$

	public NullPointerWithStyleProperty_JBIDE5193(String name) {
		super(name);
	}
	
	public void testNullPointerWithStyleProperty_JBIDE5193() throws Throwable {
		TestUtil.waitForJobs();
		setException(null);
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file " + TEST_PAGE_NAME, file); //$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		final JSPMultiPageEditor parts = openEditor(input);
		TestUtil.waitForIdle();
		assertNotNull(parts);
		StyledText styledText = parts.getSourceEditor().getTextViewer()
				.getTextWidget();
		String delimiter = styledText.getLineDelimiter();
		int offset = styledText.getOffsetAtLine(10);
		styledText.setCaretOffset(offset-delimiter.length()-"\">Text</li>".length());
		styledText.insert(":");
		
		VpeController controller = TestUtil.getVpeController(parts);
		controller.selectionChanged(new SelectionChangedEvent(parts.getSelectionProvider(), parts.getSelectionProvider().getSelection()));
	}

}
