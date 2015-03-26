/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.itest;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.ui.internal.wizard.NewJobXMLCreationWizard;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author Viacheslav Kabanovich
 *
 */
public class NewBatchWizardTest extends TestCase {	
	
	static class NewBeansXMLWizardContext {
		NewJobXMLCreationWizard wizard;
		IProject project;
		IJavaProject jp;
		WizardDialog dialog;
		

		public void init(String wizardId) {
			wizard = (NewJobXMLCreationWizard)WorkbenchUtils.findWizardByDefId(wizardId);
			project = ResourcesPlugin.getWorkspace().getRoot().getProject("BatchTestProject");
			jp = EclipseUtil.getJavaProject(project);
			wizard.init(BatchCorePlugin.getDefault().getWorkbench(), new StructuredSelection(jp));
			dialog = new WizardDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					wizard);
			wizard.setOpenEditorAfterFinish(false);
			dialog.setBlockOnOpen(false);
			dialog.open();
		}

		public void close() {
			dialog.close();
		}
	}

	public void testNewBeansXMLWizard() throws CoreException {
		NewBeansXMLWizardContext context = new NewBeansXMLWizardContext();
		context.init(NewJobXMLCreationWizard.WIZARD_ID);
		
		try {
			
			WizardNewFileCreationPage page = (WizardNewFileCreationPage)context.wizard.getPage(NewJobXMLCreationWizard.PAGE_NAME);
			String s = page.getFileName();
			assertEquals("job.xml", s);
			assertFalse(context.wizard.canFinish());
			page.setFileName("job222.xml");
			assertTrue(context.wizard.canFinish());
			String c = page.getContainerFullPath().toString();
			assertEquals("/BatchTestProject/src/META-INF/batch-jobs", c);

			assertEquals("1.0", context.wizard.getVersion());
			
			context.wizard.setID("myNewJob");
			
			context.wizard.performFinish();
		
			IFile f = context.project.getParent().getFile(page.getContainerFullPath().append(page.getFileName()));
			assertTrue(f.exists());
			
			String text = FileUtil.readStream(f.getContents());
			assertTrue(text.indexOf("http://xmlns.jcp.org/xml/ns/javaee") > 0);
			assertTrue(text.indexOf("id=\"myNewJob\"") > 0);

		} finally {
			context.close();
		}
	}

}