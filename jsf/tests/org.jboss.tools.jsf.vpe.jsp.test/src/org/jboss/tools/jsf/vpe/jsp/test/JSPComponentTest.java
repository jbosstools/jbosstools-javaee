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
package org.jboss.tools.jsf.vpe.jsp.test;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * 
 * Class for testing all jsp components
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class JSPComponentTest extends VpeTest {

    // import project name
    public static final String IMPORT_PROJECT_NAME = "jspTest"; //$NON-NLS-1$

    public JSPComponentTest(String name) {
	super(name);
	setCheckWarning(false);
    }

    /**
     * Test for jsp:declaration
     * 
     * @throws Throwable
     */
    public void testDeclaration() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/declaration.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    /**
     * Test for jsp:expression
     * 
     * @throws Throwable
     */
    public void testExpression() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/expression.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    /**
     * Test for jsp:scriptlet
     * 
     * @throws Throwable
     */
    public void testScriptlet() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/scriptlet.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    /**
     * Test for jsp:directive.attribute
     * 
     * @throws Throwable
     */
    public void testDirectiveAttribute() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"../WEB-INF/tags/catalog.tag", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    /**
     * Test for jsp:directive.include
     * 
     * @throws Throwable
     */
    public void testDirectiveInclude() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/directive_include_absolute.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$

	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/directive_include_relative.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$

    }

    /**
     * Test for jsp:include
     * 
     * @throws Throwable
     */
    public void testInclude() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/include_absolute.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$

	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/include_relative.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$

    }
    
    /**
     * Test for jsp:directive.page
     * 
     * @throws Throwable
     */
    public void testDirectivePage() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/directive_page.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:directive.tag
     * 
     * @throws Throwable
     */
    public void testDirectiveTag() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/directive_tag.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:directive.taglib
     * 
     * @throws Throwable
     */
    public void testDirectiveTaglib() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/directive_taglib.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:directive.variable
     * 
     * @throws Throwable
     */
    public void testDirectiveVariable() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"../WEB-INF/tags/catalog.tag", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:attribute
     * 
     * @throws Throwable
     */
    public void testAttribute() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/attribute.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:body
     * 
     * @throws Throwable
     */
    public void testBody() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/body.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:element
     * 
     * @throws Throwable
     */
    public void testElement() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/element.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:doBody
     * 
     * @throws Throwable
     */
    public void testDoBody() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"../WEB-INF/tags/double.tag", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:forward
     * 
     * @throws Throwable
     */
    public void testForward() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/forward.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:getProperty
     * 
     * @throws Throwable
     */
    public void testGetProperty() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/get_property.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:invoke
     * 
     * @throws Throwable
     */
    public void testInvoke() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"../WEB-INF/tags/catalog.tag", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:output
     * 
     * @throws Throwable
     */
    public void testOutput() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/output.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:plugin
     * 
     * @throws Throwable
     */
    public void testPlugin() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/plugin.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:root
     * 
     * @throws Throwable
     */
    public void testRoot() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/root.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:setProperty
     * 
     * @throws Throwable
     */
    public void testSetProperty() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/set_property.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:text
     * 
     * @throws Throwable
     */
    public void testText() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/text.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }
    
    /**
     * Test for jsp:useBean
     * 
     * @throws Throwable
     */
    public void testUseBean() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/useBean.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	
    }

}
