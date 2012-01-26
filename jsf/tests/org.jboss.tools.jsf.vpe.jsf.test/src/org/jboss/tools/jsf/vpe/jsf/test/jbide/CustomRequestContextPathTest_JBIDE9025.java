/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
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
import org.jboss.tools.common.el.core.ELReferenceList;
import org.jboss.tools.common.resref.core.ResourceReference;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.base.test.ComponentContentTest;
import org.jboss.tools.vpe.base.test.TestUtil;

/**
 * @author Yahor Radtsevich (yradtsevich)
 */
public class CustomRequestContextPathTest_JBIDE9025 extends ComponentContentTest {

	private static final String TEST_FILE_NAME = "JBIDE/9025/requestContextPath.jsp";
	
	/**
	 * Test file
	 */
	private IFile file;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setException(null);
		this.file = (IFile) TestUtil.getComponentPath(TEST_FILE_NAME, //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		ResourceReference[] entries = new ResourceReference[1];
        entries[0] = new ResourceReference("request.contextPath", ResourceReference.FILE_SCOPE);
        entries[0].setProperties("contextPathFolder");
        ELReferenceList.getInstance().setAllResources(this.file, entries);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.VpeTest#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		ELReferenceList.getInstance().setAllResources(this.file, new ResourceReference[0]);
		if(getException()!=null) {
			throw new Exception(getException());
		}
		super.tearDown();
	}

	public CustomRequestContextPathTest_JBIDE9025(String name) {
		super(name);
	}
	
	public void testJBIDE9025CustomRequestContextPath() throws Throwable {
//		IEditorInput input = new FileEditorInput(this.file);
//		JSPMultiPageEditor part = openEditor(input);
//		checkSourceSelection(part);
		performContentTest(TEST_FILE_NAME); //$NON-NLS-1$
	}

	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}
 }
