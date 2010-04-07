/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.ui.test;

import junit.framework.TestCase;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.jsp.JspEditorPlugin;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditorPart;
import org.jboss.tools.jst.jsp.preferences.IVpePreferencesPage;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * 
 * @author yzhishko
 *
 */

public abstract class NaturesInfoDialogTest extends TestCase{
	
	protected static final String TEST_PAGE_NAME = "inputUserName.jsp"; //$NON-NLS-1$
	protected static final String TEST_SHELL_NAME = "Missing Natures"; //$NON-NLS-1$
	private volatile boolean isCheckNeed = true;
	private static IProject testProject;
	
	protected NaturesInfoDialogTest(String name) {
		super(name);
	}

	protected final class ResultObject {
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

	protected final ResultObject startCheckerThread() {
        final Shell[] shell = new Shell[1];
        final ResultObject resultObject = new ResultObject();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                TestUtil.waitForIdle();
                while (shell[0] == null && isCheckNeed) {
                	Display.getDefault().syncExec(new Runnable() {
                		public void run() {
                            Shell[] shells = Display.getCurrent().getShells();
                            shell[0] = findShellWithText(shells, TEST_SHELL_NAME);
                            if (shell[0] != null) {
                                resultObject.setShellName(TEST_SHELL_NAME);
                                Label label = (Label)shell[0].getChildren()[1];
                                resultObject.setTextLabel(label.getText());
                                shell[0].close();
                            }
                		}
                	});
                }
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
	
	protected final void openPage(String projectName, String pagePath) throws Throwable{
		IFile file = (IFile) testProject.getFolder("WebContent/pages").findMember(pagePath); //$NON-NLS-1$

		assertNotNull("Could not open specified file. componentPage = " //$NON-NLS-1$
						+ pagePath
						+ ";projectName = " + projectName, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor

		JSPMultiPageEditorPart part = TestUtil.openEditor(input);

		isCheckNeed = false;
		
		assertNotNull("Editor is not opened", part); //$NON-NLS-1$

		TestUtil.delay(3000);
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("org.jboss.tools.vpe.ENABLE_PROJECT_NATURES_CHECKER", "true");  //$NON-NLS-1$ //$NON-NLS-2$
	    JspEditorPlugin.getDefault().getPreferenceStore().setValue(IVpePreferencesPage.INFORM_WHEN_PROJECT_MIGHT_NOT_BE_CONFIGURED_PROPERLY_FOR_VPE, true);
		testProject = ProjectImportTestSetup.loadProject(getTestProjectName());
		testProject.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
	
	@Override
	protected void tearDown() throws Exception {
		JspEditorPlugin.getDefault().getPreferenceStore().setValue(IVpePreferencesPage.INFORM_WHEN_PROJECT_MIGHT_NOT_BE_CONFIGURED_PROPERLY_FOR_VPE, false);
		System.setProperty("org.jboss.tools.vpe.ENABLE_PROJECT_NATURES_CHECKER", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		super.tearDown();
	}
	
	protected abstract String getTestProjectName();
	
	protected abstract String getDialogMessage();
	
}
