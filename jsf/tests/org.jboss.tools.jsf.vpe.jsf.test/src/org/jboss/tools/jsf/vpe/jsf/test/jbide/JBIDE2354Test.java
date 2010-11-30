package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.ComponentContentTest;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.editor.VpeController;

public class JBIDE2354Test extends ComponentContentTest {

    public JBIDE2354Test(String name) {
	super(name);
    }
    
    public void testJBIDE2354UndoOperation() throws Exception {
	IFile file = (IFile) TestUtil.getComponentPath("JBIDE/2354/jbide2354.xhtml", //$NON-NLS-1$
		JsfAllTests.IMPORT_PROJECT_NAME);
	IEditorInput input = new FileEditorInput(file);
	JSPMultiPageEditor part = openEditor(input);
	VpeController controller = TestUtil.getVpeController(part);
	
	Event keyEvent = new Event();
	keyEvent.widget = controller.getXulRunnerEditor().getBrowser();
	keyEvent.x = 0;
	keyEvent.y = 0;
	keyEvent.type = SWT.KeyDown;
	keyEvent.stateMask = 0;
	/*
	 * send letter 'a' key code
	 */
	keyEvent.keyCode = 97;
	
	controller.getXulRunnerEditor().getBrowser().notifyListeners(SWT.KeyDown, keyEvent);
//	Display.getCurrent().post(keyEvent);
	    
	checkSourceSelection(part);
	
	keyEvent = new Event();
	keyEvent.widget = controller.getXulRunnerEditor().getBrowser();
	keyEvent.x = 0;
	keyEvent.y = 0;
	keyEvent.type = SWT.KeyDown;
	keyEvent.stateMask = SWT.CTRL;
	/*
	 * send letter 'z' key code
	 */
	keyEvent.keyCode = 122;
	
	controller.getXulRunnerEditor().getBrowser().notifyListeners(SWT.KeyDown, keyEvent);
//	Display.getCurrent().post(keyEvent);
	    
	checkSourceSelection(part);
    }
    
    @Override
    protected String getTestProjectName() {
	return JsfAllTests.IMPORT_PROJECT_NAME;
    }

}
