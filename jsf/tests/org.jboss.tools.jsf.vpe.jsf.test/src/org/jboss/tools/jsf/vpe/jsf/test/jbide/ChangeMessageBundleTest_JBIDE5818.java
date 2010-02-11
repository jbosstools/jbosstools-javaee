/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author yradtsevich
 *
 */
public class ChangeMessageBundleTest_JBIDE5818 extends VpeTest {
	private static final String TEST_PAGE_NAME = "JBIDE/5818/JBIDE-5818.xhtml";
	private static final String PROPERTIES_FILE_NAME = "JavaSource/jbide5818/MessagesJBIDE5818.properties";
	private static final String PROPERTIES_EDITOR_ID = "org.eclipse.ui.DefaultTextEditor";

	public ChangeMessageBundleTest_JBIDE5818(String name) {
		super(name);
	}

	/**
	 * Test for <a href="http://jira.jboss.org/jira/browse/JBIDE-5818">JBIDE-5818</a>.
	 */
	public void testRemoveAllBundleMessages() throws Throwable {
		setException(null);

		/* Initialize the message bundle with three messages
		 * which are used on the test page */ 
		replaceFileContent(PROPERTIES_FILE_NAME,
				"header=Hello Demo Application\n"
				+ "prompt_message=Name:\n"
				+ "hello_message=Hello\n");

		// Open the test page first time
		openInVpe(JsfAllTests.IMPORT_PROJECT_NAME, TEST_PAGE_NAME);
		TestUtil.waitForIdle();

		// Remove all messages from the message bundle
		replaceFileContent(PROPERTIES_FILE_NAME, "");

		/* Open the test page second time.
		 * The java.util.ConcurrentModificationException may be thrown here.*/
		openInVpe(JsfAllTests.IMPORT_PROJECT_NAME, TEST_PAGE_NAME);
		TestUtil.waitForIdle();

		if (getException() != null) {
			throw new RuntimeException(getException());
		}
	}

	/**
	 * Opens the specified file by text editor, replaces its content
	 * with {@code newContent} and saves it.
	 */
	private TextEditor replaceFileContent(final String path,
			final String newContent) throws CoreException {
		IFile elementPageFile = (IFile) TestUtil.getResource(path, JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput input = new FileEditorInput(elementPageFile);
		TextEditor editor = (TextEditor)
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().openEditor(input, PROPERTIES_EDITOR_ID, true);
		assertNotNull(editor);
		StyledText propertiesStyledText = (StyledText) editor.getAdapter(Control.class);
		propertiesStyledText.setText(newContent);
		editor.doSave(new NullProgressMonitor());
		return editor;
	}
}
