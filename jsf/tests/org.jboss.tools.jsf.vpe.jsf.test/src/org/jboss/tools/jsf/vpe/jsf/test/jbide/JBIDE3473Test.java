package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.jboss.tools.vpe.xulrunner.editor.XulRunnerVpeUtils;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;

/**
 * @author yradtsevich
 *
 */
public class JBIDE3473Test extends VpeTest {
	private static final Point INSERTION_POSITION = new Point(15, 12);
	private static final String TABLE_ID = "table-id"; //$NON-NLS-1$
	private static final String TEST_PAGE_NAME = "JBIDE/3473/JBIDE-3473.html"; //$NON-NLS-1$
	private static final String INSERTING_TEXT = " id=\"td-id\""; //$NON-NLS-1$	

	public JBIDE3473Test(String name) {
		super(name);
	}

	/**
	 * Adds 'id' attribute to TD tag inside a table and checks if
	 * there are not changes in the VPE view. 
	 * 
	 * @throws Throwable
	 */
	public void testNodeUpdate() throws Throwable {
		TestUtil.waitForJobs();
		setException(null);
		
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file " + TEST_PAGE_NAME, file); //$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		// open and get editor
		JSPMultiPageEditor jspEditor = openEditor(input);

		final StructuredTextViewer textViewer = jspEditor.getSourceEditor().getTextViewer();
		StyledText styledText = textViewer.getTextWidget();

		final int offset = TestUtil.getLinePositionOffcet(textViewer,
				INSERTION_POSITION.x, INSERTION_POSITION.y);
		styledText.setCaretOffset(offset);
		IndexedRegion indexedRegion = ContentAssistUtils.getNodeAt(textViewer, offset);
		Node tdTextEditorNode = (Node) indexedRegion;
		assertNotNull(tdTextEditorNode);
		assertTrue("Text under cursor is not TD node", HTML.TAG_TD.equalsIgnoreCase(tdTextEditorNode.getNodeName())); //$NON-NLS-1$

		VpeController vpeController = TestUtil.getVpeController(jspEditor);

		Rectangle boundsBeforeInsert = getBoundsOfElementById(vpeController.getXulRunnerEditor().getDOMDocument(), TABLE_ID);
		assertNotNull("boundsBeforeInsert should be not null.", boundsBeforeInsert); //$NON-NLS-1$

		styledText.insert(INSERTING_TEXT); //$NON-NLS-1$
		TestUtil.delay(450);
		TestUtil.waitForJobs();

		Rectangle boundsAfterInsert = getBoundsOfElementById(vpeController.getXulRunnerEditor().getDOMDocument(), TABLE_ID);
		assertNotNull("boundsAfterInsert should be not null.", boundsAfterInsert); //$NON-NLS-1$

		assertEquals("Width of the table has been changed.", boundsBeforeInsert.width, boundsAfterInsert.width); //$NON-NLS-1$
		assertEquals("Height of the table has been changed.", boundsBeforeInsert.height, boundsAfterInsert.height); //$NON-NLS-1$

		if(getException()!=null) {
			throw getException();
		}
	}

	/**
	 * Returns bounds of the element with given {@code id}
	 * from the {@code document}
	 * 
	 * @param document the document
	 * @param id ID of the DOM element which bounds is needed
	 * @return bounds of the element with given {@code id}
	 * or {@code null} if the element is not found or not accessible
	 * 
	 * @see XulRunnerVpeUtils#getElementBounds(nsIDOMNode)
	 */
	private Rectangle getBoundsOfElementById(nsIDOMDocument document, String id) {
		Rectangle bounds = null;
		
		nsIDOMElement element = document.getElementById(id);
		if (element != null) {
			//bounds = VisualDomUtil.getBounds(element);
			bounds = XulRunnerVpeUtils.getElementBounds(element);
		}
		
		return bounds;
	}	
}