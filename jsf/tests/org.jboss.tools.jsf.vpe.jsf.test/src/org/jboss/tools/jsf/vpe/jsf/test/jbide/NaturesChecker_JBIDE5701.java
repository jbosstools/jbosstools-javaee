package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

public class NaturesChecker_JBIDE5701 extends VpeTest {

	private static final String FIRST_TEST_PAGE_NAME = "inputUserName.jsp"; //$NON-NLS-1$
	private static final String TEST_SHELL_NAME = "Missing Natures"; //$NON-NLS-1$
	private static final String TEST_STRING = "JBoss Tools Visual Editor might not fully work in project \""  //$NON-NLS-1$
		+ JsfAllTests.IMPORT_NATURES_CHECKER_PROJECT + 
		"\" because it does not have JSF and code completion enabled completely.\n\n" + //$NON-NLS-1$
		"Please use the Configure menu on the project to enable JSF if " + //$NON-NLS-1$
		"you want all features of the editor working."; //$NON-NLS-1$
	private static final String SECOND_TEST_PAGE_NAME = "components/commandButton.jsp"; //$NON-NLS-1$
	
	public NaturesChecker_JBIDE5701(String name) {
		super(name);
	}

	public void testNaturesChecker() throws Throwable {
		
		ResultObject resultObject = startCheckerThread();
		
		openPage(JsfAllTests.IMPORT_NATURES_CHECKER_PROJECT, FIRST_TEST_PAGE_NAME);
		
		assertEquals(TEST_SHELL_NAME, resultObject.getShellName());
		assertEquals(TEST_STRING, resultObject.getTextLabel());

		resultObject = startCheckerThread();
		
		openPage(JsfAllTests.IMPORT_PROJECT_NAME, SECOND_TEST_PAGE_NAME);
		
		assertEquals("", resultObject.getShellName()); //$NON-NLS-1$
		assertEquals("", resultObject.getTextLabel()); //$NON-NLS-1$

	}

	private class ResultObject {
		private String shellName = ""; //$NON-NLS-1$
		private String textLabel = ""; //$NON-NLS-1$

		public String getShellName() {
			return shellName;
		}

		public void setShellName(String shellName) {
			this.shellName = shellName;
		}

		public String getTextLabel() {
			return textLabel;
		}

		public void setTextLabel(String textLabel) {
			this.textLabel = textLabel;
		}

	}

	private ResultObject startCheckerThread() {
		final ResultObject resultObject = new ResultObject();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						Shell[] shells = null;
						while (shells == null) {
							shells = Display.getCurrent().getShells();
						}
						Shell shell = findShellWithText(shells, TEST_SHELL_NAME);
						if (shell == null) {
							TestUtil.delay(5000);
							shells = Display.getCurrent().getShells();
							shell = findShellWithText(shells, TEST_SHELL_NAME);
						}
						if (shell != null) {
							resultObject.setShellName(TEST_SHELL_NAME);
							Label label = (Label)shell.getChildren()[1];
							resultObject.setTextLabel(label.getText());
							shell.close();
						}
					}
				});
			}
		});
		thread.start();
		return resultObject;
	}
	
	private static Shell findShellWithText (Shell[] shells, String text){
		for (int i = 0; i < shells.length; i++) {
			if (text.equals(shells[i].getText())) {
				return shells[i];
			}
		}
		return null;
	}
	
	private void openPage(String projectName, String pagePath) throws Throwable{
		IFile file = (IFile) TestUtil.getComponentPath(pagePath, projectName);

		assertNotNull("Could not open specified file. componentPage = " //$NON-NLS-1$
						+ pagePath
						+ ";projectName = " + projectName, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor

		JSPMultiPageEditor part = openEditor(input);

		assertNotNull("Editor is not opened", part); //$NON-NLS-1$

		TestUtil.delay(3000);
		
	}
	
}
