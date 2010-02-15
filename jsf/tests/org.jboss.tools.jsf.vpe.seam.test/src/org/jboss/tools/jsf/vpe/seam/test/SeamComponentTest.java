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
package org.jboss.tools.jsf.vpe.seam.test;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Class for testing all Seam components
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class SeamComponentTest extends VpeTest {

    public SeamComponentTest(String name) {
	super(name);
	setCheckWarning(false);
    }

    public void testButton() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/button.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testCache() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/cache.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testConversationId() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/conversationId.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testConversationPropagation() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/conversationPropagation.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testConvertDateTime() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/convertDateTime.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testConvertEntity() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/convertEntity.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testConvertEnum() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/convertEnum.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDecorate() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/decorate.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testDefaultAction() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/defaultAction.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDiv() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/div.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testEnumItem() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/enumItem.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testFileUpload() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/fileUpload.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testFormattedText() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/formattedText.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testFragment() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/fragment.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testGraphicImage() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/graphicImage.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testLabel() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/label.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testLink() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/link.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testMessage() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/message.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testRemote() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/remote.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testSelectDate() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/message.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testSelectItems() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/message.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testValidate() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/validate.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testSpan() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/span.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testValidateAll() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/validateAll.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testValidateFormattedText() throws Throwable {
    performTestForVpeComponent((IFile) TestUtil.getComponentPath(
    	"components/validateFormattedText.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testTaskId() throws Throwable {
    performTestForVpeComponent((IFile) TestUtil.getComponentPath(
    	"components/taskId.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testAllComponentsOnSinglePage() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/seamtest.xhtml", SeamAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

}
