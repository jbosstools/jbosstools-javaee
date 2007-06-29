/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.wizard;

import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditorFactory;

/**
 * @author eskimo
 *
 */
public class SeamFormWizardPage1 extends WizardPage {

	/**
	 * @param pageName
	 */
	public SeamFormWizardPage1(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public SeamFormWizardPage1(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("test");
		shell.open();
		GridLayout gl = new GridLayout(3,false);
		shell.setLayout(gl);
		IFieldEditor editor = IFieldEditorFactory.INSTANCE.createBrowseFolderEditor("test", "test1", "test2");
		editor.doFillIntoGrid(shell);
		editor = IFieldEditorFactory.INSTANCE.createTextEditor("test", "test1", "test2");
		editor.doFillIntoGrid(shell);
		editor = IFieldEditorFactory.INSTANCE.createComboEditor("test", "test1", Arrays.asList(new String[]{"war","ear"}),"test2");
		editor.doFillIntoGrid(shell);
		shell.update();

		while(!shell.isDisposed()) {
			if(!d.readAndDispatch()) d.sleep();
		}

		d.dispose();
	}
}
