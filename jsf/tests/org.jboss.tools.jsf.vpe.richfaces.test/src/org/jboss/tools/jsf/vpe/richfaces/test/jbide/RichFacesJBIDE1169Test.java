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
package org.jboss.tools.jsf.vpe.richfaces.test.jbide;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.el.core.ELReferenceList;
import org.jboss.tools.common.resref.core.ResourceReference;
import org.jboss.tools.jsf.vpe.richfaces.test.RichFacesAllTests;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.util.ElServiceUtil;

/**
 * @author mareshkau
 * Test Case For JBIDE-1169
 */
public class RichFacesJBIDE1169Test extends VpeTest{

	private String RICH_FACES_SKIN_KEY = "org.richfaces.SKIN"; //$NON-NLS-1$
	
	private String SKIN_VALUE = "ruby"; //$NON-NLS-1$
	
	private IFile testFile;
	
	public RichFacesJBIDE1169Test(String name) {
		super(name);
	}
	
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	ResourceReference[] entries = new ResourceReference[1];
    	testFile = (IFile) TestUtil.getComponentPath(
    			"JBIDE/1169/test.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME); //$NON-NLS-1$
    	entries[0] = new ResourceReference(RICH_FACES_SKIN_KEY,ResourceReference.PROJECT_SCOPE);
    	entries[0].setProperties(SKIN_VALUE);
    	ELReferenceList.getInstance().setAllResources(testFile, entries);
    }
    
    public void testJBIDE1169() {  	
        String replacedValue = ElServiceUtil.replaceEl(testFile,"#{"+RICH_FACES_SKIN_KEY+'}'); //$NON-NLS-1$
        assertEquals("Skin value should be equals",SKIN_VALUE, replacedValue); //$NON-NLS-1$
    }
    /**
     * Tear down.
     * 
     * @throws Exception the exception
     */
    @Override
    protected void tearDown() throws Exception {
        if(getException()!=null) {
        	throw new Exception(getException());
        }
        ELReferenceList.getInstance().setAllResources(testFile, new ResourceReference[0]);
        testFile = null;
        super.tearDown();
    }
}
