/**
 * 
 */
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.ui.test.ComponentContentTest;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Junit test class for https://jira.jboss.org/browse/JBIDE-6600
 * @author mareshkau
 *
 */
public class DocBookTemplatesTest extends ComponentContentTest{

	private static String VPE_EDITOR_ID=VpeTest.EDITOR_ID;
	
	private static String DOC_BOOK_EDITOR_ID="org.jboss.tools.jst.jsp.jspeditor.DocBookEditor";
	
	private String activeEditorID;
	public DocBookTemplatesTest(String name) {
		super(name);
	}

	public void testCheckHtmlTitle() throws Throwable {
		setActiveEditorID(VPE_EDITOR_ID);
		performInvisibleTagTest("JBIDE/6600/jbide6600.html","title");
	}

	public void testCheckDocBookTitle() throws Throwable {
		setActiveEditorID(DOC_BOOK_EDITOR_ID);
		performContentTest("JBIDE/6600/jbide6600.xml");
	}

	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}
	
	private void setActiveEditorID(String editorID){
		this.activeEditorID = editorID;
	}
	
	protected String getEditorID(){
		return this.activeEditorID;
	}
}
