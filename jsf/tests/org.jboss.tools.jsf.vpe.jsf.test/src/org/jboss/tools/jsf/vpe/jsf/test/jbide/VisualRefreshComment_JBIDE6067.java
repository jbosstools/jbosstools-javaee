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

package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditorPart;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;

/**
 * 
 * @author yzhishko
 *
 */

public class VisualRefreshComment_JBIDE6067 extends VpeTest {

	public VisualRefreshComment_JBIDE6067(String name) {
		super(name);
	}
	
	public void testVisualRefreshComment() throws Throwable{
		setException(null);
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/6067/JBIDE-6067.jsp", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput editorInput = new FileEditorInput(file);
		JSPMultiPageEditor part = openEditor(editorInput);
		TestUtil.delay(3000);
		TestUtil.waitForIdle();
		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();
		String delimiter = styledText.getLineDelimiter();
		int offset = styledText.getOffsetAtLine(23);
		offset = offset - delimiter.length() - "-->  <f:selectItem id=\"it1_2\" itemLabel=\"Sports\" itemValue=\"2\" />".length(); //$NON-NLS-1$
		styledText.setCaretOffset(offset);
		styledText.setSelection(offset, offset + 3);
		styledText.insert(""); //$NON-NLS-1$
		TestUtil.delay(1000);
		TestUtil.waitForIdle();
		offset = styledText.getOffsetAtLine(23)-delimiter.length();
		styledText.setCaretOffset(offset);
		styledText.insert(">"); //$NON-NLS-1$
		TestUtil.delay(1000);
		TestUtil.waitForIdle();
		styledText.insert("-"); //$NON-NLS-1$
		TestUtil.delay(1000);
		TestUtil.waitForIdle();
		styledText.insert("-"); //$NON-NLS-1$
		TestUtil.delay(1000);
		TestUtil.waitForIdle();
		checkVisualPart(part);
	}

	private void checkVisualPart (JSPMultiPageEditorPart editorPart){
		nsIDOMDocument visualDoc = TestUtil.getVpeVisualDocument((JSPMultiPageEditor)editorPart);
		nsIDOMNodeList nodeList = visualDoc.getElementsByTagName("SELECT"); //$NON-NLS-1$
		assertNotNull("There are no SELECT elements in a visual DOM", nodeList); //$NON-NLS-1$
		assertTrue("There are no SELECT elements in a visual DOM", nodeList.getLength() > 0); //$NON-NLS-1$
		nsIDOMNode commentNode = nodeList.item(0).getChildNodes().item(1);
		String nodeValue = commentNode.getNodeValue();
		assertEquals("SELECT element contents incorrect comment node", nsIDOMNode.COMMENT_NODE, commentNode.getNodeType()); //$NON-NLS-1$
		assertEquals("    <f:selectItem id=\"it1_2\" itemLabel=\"Sports\" itemValue=\"2\" />", nodeValue); //$NON-NLS-1$
	}

}
