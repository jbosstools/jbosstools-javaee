/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.jboss.tools.vpe.editor.template.VpeTemplateManager;
import org.w3c.dom.Node;

/**
 * @author mareshkau
 *
 */
public class JBIDE1494Test extends VpeTest{

    private static final String TEST_PAGE_NAME = "JBIDE/1494/JBIDE-1494.xhtml"; //$NON-NLS-1$
    
	public JBIDE1494Test(String name) {
		super(name);
	}
	
	public void testJBIDE1494() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file " + TEST_PAGE_NAME, file); //$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();

			styledText.setCaretOffset(424);
			Node h_outputText = (Node) ContentAssistUtils.getNodeAt(part
					.getSourceEditor().getTextViewer(), 424);

			assertNotNull(h_outputText);

			VpeController vpeController = TestUtil.getVpeController(part);
			
			VpeTemplateManager templateManager= vpeController.getPageContext().getVisualBuilder().getTemplateManager();
			assertNotNull(templateManager);
			Set<?>  dependencySet = new HashSet();
			VpeTemplate h_output_template = templateManager.getTemplate(vpeController.getPageContext(),h_outputText, dependencySet);
			
			assertNotNull(h_output_template.getTextFormattingData());
			//text formating for h:output
			assertEquals(8, h_output_template.getTextFormattingData().getAllFormatData().length);
	
			Node h_dataTable = (Node) ContentAssistUtils.getNodeAt(part
					.getSourceEditor().getTextViewer(), 473);
			
			assertNotNull(h_dataTable);
			
			dependencySet=new HashSet();
			
			VpeTemplate h_data_Table = templateManager.getTemplate(vpeController.getPageContext(),h_dataTable , dependencySet);
	
			assertNotNull(h_data_Table.getTextFormattingData());
			
			assertEquals(9, h_data_Table.getTextFormattingData().getAllFormatData().length);

			Node span =(Node) ContentAssistUtils.getNodeAt(part
					.getSourceEditor().getTextViewer(), 615);
			
			dependencySet=new HashSet();
			
			VpeTemplate spanTemplate = templateManager.getTemplate(vpeController.getPageContext(),span, dependencySet);
		
			assertNotNull(spanTemplate);
			assertEquals(11,spanTemplate.getTextFormattingData().getAllFormatData().length);
	}
	

}
