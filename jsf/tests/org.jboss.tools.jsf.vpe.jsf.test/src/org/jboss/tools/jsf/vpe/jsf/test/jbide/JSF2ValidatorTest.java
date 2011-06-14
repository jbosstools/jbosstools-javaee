/**
 * 
 */
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;

/**
 * JUnit test class for https://jira.jboss.org/browse/JBIDE-6965
 * 
 * @author mareshkau
 *
 */
public class JSF2ValidatorTest extends VpeTest{

	private static final String MARKER_TYPE="org.jboss.tools.jsf.jsf2problemmarker"; //$NON-NLS-1$
	public JSF2ValidatorTest(String name) {
		super(name);
	}
	
	public void testCAforIncludeTaglibInInenerNodes() throws Throwable {
		// TODO Fix this test. See JBIDE-9088
//        IFile file = (IFile) TestUtil.getComponentPath("JBIDE/6922/jbide6922.xhtml", //$NON-NLS-1$
//        		JsfAllTests.IMPORT_JSF_20_PROJECT_NAME);
//        IEditorInput input = new FileEditorInput(file);
//        JSPMultiPageEditor multiPageEditor = openEditor(input);
//        IMarker[] problemMarkers = file.findMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
//        assertEquals("There shouldn't be any problems on page", 0,problemMarkers.length); //$NON-NLS-1$
//		StyledText styledText = multiPageEditor.getSourceEditor().getTextViewer()
//		.getTextWidget();
//		int caretOffcet = TestUtil.getLinePositionOffcet(multiPageEditor.getSourceEditor().getTextViewer(), 4, 5);
//		styledText.setCaretOffset(caretOffcet);
//		styledText.insert("xmlns:test=\"http://java.sun.com/jsf/composite/test\""); //$NON-NLS-1$
//		TestUtil.delay(1200);
//		TestUtil.waitForJobs();
//		problemMarkers = file.findMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
//		assertEquals("res folder marker is expected", 1, problemMarkers.length); //$NON-NLS-1$
//		String message = (String) problemMarkers[0].getAttribute(IMarker.MESSAGE);
//		assertEquals("Error messages should be","JSF 2 Resources folder \"/resources/test\" is missing in a project web directory",message); //$NON-NLS-1$ //$NON-NLS-2$
//		caretOffcet =  TestUtil.getLinePositionOffcet(multiPageEditor.getSourceEditor().getTextViewer(), 6, 1);
//		styledText.setCaretOffset(caretOffcet);
//		styledText.insert("<test:testElement />"); //$NON-NLS-1$
//		TestUtil.delay(1200);
//		TestUtil.waitForJobs();
//		problemMarkers = file.findMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
//		assertEquals("Number of markers should be",2, problemMarkers.length); //$NON-NLS-1$
	}
	
}
