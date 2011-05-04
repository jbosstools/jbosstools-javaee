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
import org.jboss.tools.cdi.text.ext.test.HyperlinkDetectorTest;
import org.jboss.tools.common.util.FileUtil;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class SeamConfigTagNameHyperlinkTest extends SeamConfigTest {
	public SeamConfigTagNameHyperlinkTest() {}

	public void testSeamConfigTagNameHyperlink() throws Exception {
		int offset = 802; // <|test602:Report>
		IHyperlink hyperlink = HyperlinkDetectorTest.checkHyperLinkInXml("src/META-INF/beans.xml", project, offset, "org.jboss.tools.cdi.seam.text.ext.hyperlink.SeamConfigTagNameHyperlink");
		hyperlink.open();
		
		IEditorPart editor = CDISeamExtPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
		IFile f = input.getFile();
		assertEquals("Report.java", f.getName());		
	}

	public void testSeamConfigTagAttributeHyperlink() throws Exception {
		int offset = 1088; // <test603:OtherQualifier va|lue1="AA"
		IHyperlink hyperlink = HyperlinkDetectorTest.checkHyperLinkInXml("src/META-INF/beans.xml", project, offset, "org.jboss.tools.cdi.seam.text.ext.hyperlink.SeamConfigTagNameHyperlink");
		hyperlink.open();
		
		IEditorPart editor = CDISeamExtPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
		IFile f = input.getFile();
		assertEquals("OtherQualifier.java", f.getName());
		ISelection selection =  editor.getEditorSite().getSelectionProvider().getSelection();
		System.out.println(selection);
		assertTrue(selection instanceof TextSelection);
		TextSelection textSelection = (TextSelection)selection;
		assertEquals("value1", textSelection.getText());
	}

}
