/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.text.ext.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.jboss.tools.cdi.seam.config.core.test.SeamConfigTest;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtPlugin;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil;
import org.jboss.tools.common.util.FileUtil;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class SeamConfigTagNameHyperlinkTest extends SeamConfigTest {
	private static final String FILE_NAME = "src/META-INF/beans.xml";
	
	public SeamConfigTagNameHyperlinkTest() {}

	public void testSeamConfigTagNameHyperlink() throws Exception {
		IFile file = getTestProject().getFile(FILE_NAME);
		String content = FileUtil.getContentFromEditorOrFile(file);
		
		//int offset = 802; // <|test602:Report>
		int offset = content.indexOf("test602:Report");
		IHyperlink hyperlink = CDIHyperlinkTestUtil.checkHyperLinkInXml(FILE_NAME, project, offset, "org.jboss.tools.cdi.seam.text.ext.hyperlink.SeamConfigTagNameHyperlink");
		hyperlink.open();
		
		IEditorPart editor = CDISeamExtPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
		IFile f = input.getFile();
		assertEquals("Report.java", f.getName());		
	}

	public void testSeamConfigTagAttributeHyperlink() throws Exception {
		IFile file = getTestProject().getFile(FILE_NAME);
		String content = FileUtil.getContentFromEditorOrFile(file);
		//int offset = 1088; // <test603:OtherQualifier va|lue1="AA"
		int offset = content.indexOf("test603");
		offset = content.indexOf("value1", offset);
		
		IHyperlink hyperlink = CDIHyperlinkTestUtil.checkHyperLinkInXml(FILE_NAME, project, offset, "org.jboss.tools.cdi.seam.text.ext.hyperlink.SeamConfigTagNameHyperlink");
		hyperlink.open();
		
		IEditorPart editor = CDISeamExtPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
		IFile f = input.getFile();
		assertEquals("OtherQualifier.java", f.getName());
		ISelection selection =  editor.getEditorSite().getSelectionProvider().getSelection();
		assertTrue(selection instanceof TextSelection);
		TextSelection textSelection = (TextSelection)selection;
		assertEquals("value1", textSelection.getText());
	}

}
