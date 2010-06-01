package org.jboss.tools.jsf.vpe.richfaces.test.jbide;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.richfaces.test.RichFacesAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

public class JBIDE1613Test extends VpeTest {

	private static final String TEST_PAGE_NAME = "JBIDE/1613/JBIDE-1613.xhtml";

	public JBIDE1613Test(String name) {
		super(name);
	}
	
	public void testJBIDE_1613() throws Throwable{
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(
				TEST_PAGE_NAME, RichFacesAllTests.IMPORT_PROJECT_NAME);
		
		assertNotNull("Could not open specified file. componentPage = " + TEST_PAGE_NAME//$NON-NLS-1$
				+ ";projectName = " + RichFacesAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		nsIDOMElement element = document.getDocumentElement();
		
		//check that element is not null
		assertNotNull(element);
		
		// get root node
		nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();
		
		// find "li" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_LI);
		assertEquals(1, elements.size());
		
	}
	
}
