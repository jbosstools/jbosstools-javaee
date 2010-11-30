/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.common.el.core.ELReferenceList;
import org.jboss.tools.common.resref.core.ResourceReference;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.ComponentContentTest;
import org.jboss.tools.vpe.base.test.TestUtil;

/**
 * @author mareshkau
 *
 */
public class JBIDE3144Test extends ComponentContentTest{

	/**
	 * Test Page
	 */
	private IFile file;
	private static  Map<String, String> elValuesMap;
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.VpeTest#setUp()
	 */

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setException(null);
		this.file = (IFile) TestUtil.getComponentPath("JBIDE/3144/home.xhtml", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		elValuesMap = new HashMap<String, String>();
		elValuesMap.put("request.contextPath", "./"); //$NON-NLS-1$ //$NON-NLS-2$
		elValuesMap.put("test.dataTablecolor", "background-color:red;"); //$NON-NLS-1$ //$NON-NLS-2$
		elValuesMap.put("test.columnColor", "background-color:green;"); //$NON-NLS-1$ //$NON-NLS-2$
		
		elValuesMap.put("test.columnsColor", "background-color:#A020F0;"); //$NON-NLS-1$ //$NON-NLS-2$
		elValuesMap.put("test.scrolable", "background-color:blue;"); //$NON-NLS-1$ //$NON-NLS-2$
		elValuesMap.put("test.richDataGrid", "background-color:pink;"); //$NON-NLS-1$ //$NON-NLS-2$
		elValuesMap.put("test.scope", "Test El expression");  //$NON-NLS-1$//$NON-NLS-2$
		ResourceReference[] entries = new ResourceReference[elValuesMap.size()];
        int i = 0;
        for (Entry<String, String> string : elValuesMap.entrySet()) {
            entries[i] = new ResourceReference(string.getKey(), ResourceReference.PROJECT_SCOPE);
            entries[i].setProperties(string.getValue());
            i++;         
        }
        ELReferenceList.getInstance().setAllResources(this.file,entries);
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

	public JBIDE3144Test(String name) {
		super(name);
	}
	
	public void testJBIDE3144Selection() throws Exception {
		IEditorInput input = new FileEditorInput(this.file);
		JSPMultiPageEditor part = openEditor(input);
		checkSourceSelection(part);
	}
	
	public void testJBIDE3144Test2() throws Throwable {
		performContentTest("JBIDE/3144/test.xhtml"); //$NON-NLS-1$
	}
	
	public void testJBIDE3214() throws Throwable {	
		performContentTest("JBIDE/3144/jbide3214test.xhtml"); //$NON-NLS-1$
	}

	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}
 }
