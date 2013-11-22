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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.ComponentContentTest;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.editor.mozilla.MozillaPreview;
import org.mozilla.interfaces.nsIDOMElement;

/**
 * https://jira.jboss.org/jira/browse/JBIDE-5639
 * 
 * @author mareshkau
 * 
 */
public class MessageResolutionInPreviewTabTest extends ComponentContentTest {

	JSPMultiPageEditor part;
	
	public MessageResolutionInPreviewTabTest(String name) {
		super(name);
	}
	
	public void testMessageResolutionInPreviewTab() throws Throwable {

		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/5639/messageResolutionTest.jsp", //$NON-NLS-1$
				JsfAllTests.IMPORT_I18N_PROJECT_NAME);
		IEditorInput input = new FileEditorInput(file);
		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor
		part = openEditor(input);
		part.pageChange(part.getPreviewIndex());
		
		MozillaPreview mozillaPreview = (MozillaPreview) part.getVisualEditor().getPreviewWebBrowser();
		//here we wait for preview initialization, but it's should be less then 1 second 
		long end = System.currentTimeMillis()+1000;
		while(mozillaPreview.getContentArea()==null) {
			if (!Display.getCurrent().readAndDispatch()) {
				Display.getCurrent().sleep();
				assertEquals("The preview initialization to long", true, end>System.currentTimeMillis()); //$NON-NLS-1$
				}
        }
		nsIDOMElement contentArea = mozillaPreview.getContentArea();
		assertEquals("The Message Should be from resource bundles","Guten Tag!",contentArea.getFirstChild().getFirstChild().getNodeValue().trim()); //$NON-NLS-1$ //$NON-NLS-2$
		part.pageChange(part.getVisualSourceIndex());
	}
	/**
	 * test for support web-facesconfig_2_0.xsd
	 * @throws Throwable
	 */
	public void testMessageResolutionForJSF2Config() throws Throwable {
		performContentTest("JBIDE/5639/message-resolution.xhtml"); //$NON-NLS-1$
	}
	

	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}

}
