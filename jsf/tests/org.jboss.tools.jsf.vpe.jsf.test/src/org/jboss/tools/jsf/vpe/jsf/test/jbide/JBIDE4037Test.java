package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMElement;

/**
 * Test of <a href="https://jira.jboss.org/jira/browse/JBIDE-4037">JBIDE-4037</a>:
 * Visual part of VPE doesn't display/select 
 * automatically currently selected item from search results.
 * 
 * @author yradtsevich
 *
 */
public class JBIDE4037Test extends VpeTest {

	private static final String ROOT_ELEMENT_ID = "rootElement"; //$NON-NLS-1$
	private static final String FILE_PATH = "JBIDE/4037/4037.html"; //$NON-NLS-1$

	public JBIDE4037Test(String name) {
		super(name);
	}
	
	/**
	 * Checks if the first element in the test file is selected in the VPE
	 * when the VPE is just loaded.
	 * 
	 * @throws Throwable
	 */
	public void testJBIDE4037Test() throws Throwable {
		setException(null);
        IFile ifile = (IFile) TestUtil.getComponentPath(FILE_PATH, 
        		JsfAllTests.IMPORT_PROJECT_NAME);
        IEditorInput input = new FileEditorInput(ifile);
        JSPMultiPageEditor part = openEditor(input);
        
        TestUtil.waitForJobs();
        nsIDOMElement rootElement = TestUtil.getVpeController(part)
        		.getXulRunnerEditor()
        		.getLastSelectedElement();

        //check if something selected
        assertNotNull(rootElement);
        //check if the selected element is the first element on the page (we know its ID)
        assertEquals(ROOT_ELEMENT_ID, rootElement.getAttribute(HTML.ATTR_ID));

		if(getException()!=null) {
			throw getException();
		}
	}
}
