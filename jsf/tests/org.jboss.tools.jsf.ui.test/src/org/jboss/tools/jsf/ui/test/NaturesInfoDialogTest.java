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
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jst.web.ui.WebUiPlugin;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditorPart;
import org.jboss.tools.jst.web.ui.internal.editor.preferences.IVpePreferencesPage;
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
		JsfUiPlugin.getDefault();
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
		System.out.println("NaturesInfoDialogTest.startCheckerThread");
        final Shell[] shell = new Shell[1];
        final ResultObject resultObject = new ResultObject();
        Thread thread = new Thread(new Runnable() {
            public void run() {
            	System.out.println("NaturesInfoDialogTest.77_waitForIdle");
                TestUtil.waitForIdle();
                while (shell[0] == null && isCheckNeed) {
                	System.out.println("NaturesInfoDialogTest.80_syncExec");
                	Display.getDefault().syncExec(new Runnable() {
                		public void run() {
                            Shell[] shells = Display.getCurrent().getShells();
                            System.out.println("NaturesInfoDialogTest.84_syncExec shells.size=" + shells.length);
                            shell[0] = findShellWithText(shells, TEST_SHELL_NAME);
                            if (shell[0] != null) {
                            	System.out.println("NaturesInfoDialogTest shell[0] != null");
                                resultObject.setShellName(TEST_SHELL_NAME);
                                Label label = (Label)shell[0].getChildren()[1];
                                resultObject.setTextLabel(label.getText());
                                System.out.println("NaturesInfoDialogTest closing shell[0]");
                                shell[0].close();
                            }
                            System.out.println("NaturesInfoDialogTest shell[0] == null");
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
		System.out.println("NaturesInfoDialogTest file found");
		assertNotNull("Could not open specified file. componentPage = " //$NON-NLS-1$
						+ pagePath
						+ ";projectName = " + projectName, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);
		
		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor
		System.out.println("NaturesInfoDialogTest input not null");
		JSPMultiPageEditorPart part = TestUtil.openEditor(input);
		System.out.println("NaturesInfoDialogTest editor opened");
		isCheckNeed = false;
		
		assertNotNull("Editor is not opened", part); //$NON-NLS-1$
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("org.jboss.tools.vpe.ENABLE_PROJECT_NATURES_CHECKER", "true");  //$NON-NLS-1$ //$NON-NLS-2$
	    WebUiPlugin.getDefault().getPreferenceStore().setValue(IVpePreferencesPage.INFORM_WHEN_PROJECT_MIGHT_NOT_BE_CONFIGURED_PROPERLY_FOR_VPE, true);
		testProject = ProjectImportTestSetup.loadProject(getTestProjectName());
		testProject.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
	
	@Override
	protected void tearDown() throws Exception {
		WebUiPlugin.getDefault().getPreferenceStore().setValue(IVpePreferencesPage.INFORM_WHEN_PROJECT_MIGHT_NOT_BE_CONFIGURED_PROPERLY_FOR_VPE, false);
		System.setProperty("org.jboss.tools.vpe.ENABLE_PROJECT_NATURES_CHECKER", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		super.tearDown();
	}
	
	protected abstract String getTestProjectName();
	
	protected abstract String getDialogMessage();
	
}
