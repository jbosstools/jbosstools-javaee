/**
 * 
 */
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * @author dmaliarevich
 * 
 */
public class JBIDE1744Test extends VpeTest {

	private static final String IMPORT_PROJECT_NAME = "jsfTest";

	private static final String TEST_PAGE_NAME = "JBIDE/1744/JBIDE-1744.jsp";

	/**
	 * @param name
	 */
	public JBIDE1744Test(String name) {
		super(name);
	}

	// test method for JBIDE 1615
	public void testJBIDE_1744() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
				IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file " + file.getFullPath(),
				file);

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = getVpeVisualDocument(part);
		nsIDOMElement element = document.getDocumentElement();

		// check that element is not null
		assertNotNull(element);

		// get root node
		nsIDOMNode node = (nsIDOMNode) element
				.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		// find "table" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);
		// method does not look for nested tables
		// thus only one element is found
		assertEquals(1, elements.size());

		if (getException() != null) {
			throw getException();
		}
	}

}
