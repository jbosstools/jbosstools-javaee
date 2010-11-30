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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.el.core.ELReferenceList;
import org.jboss.tools.common.resref.core.ResourceReference;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;

/**
 * Test case for JBIDE-2979
 * @author mareshkau
 * 
 */
public class JBIDE2979Test extends VpeTest {

	/**
	 * Test Page
	 */
	private IFile file;
	
	
	private static  Map<String, String> elValuesMap;
	
	public JBIDE2979Test(String name) {
		super(name);
	}

	public void testJBIDE2979() {
		ResourceReference[] entries = ELReferenceList.getInstance().getAllResources(file);
		for (ResourceReference resourceReference : entries) {
			assertEquals("Value from Map Should be equal value from Resource", //$NON-NLS-1$
					elValuesMap.get(resourceReference.getLocation()),resourceReference.getProperties());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.VpeTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setException(null);
		this.file = (IFile) TestUtil.getComponentPath("JBIDE/2979/test.xhtml", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		elValuesMap = new HashMap<String, String>();
		elValuesMap.put("bean.name", "Test Test"); //$NON-NLS-1$ //$NON-NLS-2$
		elValuesMap.put("bean.value", "Test;. Test"); //$NON-NLS-1$ //$NON-NLS-2$
		elValuesMap.put("bean.value2", "%Test,.; ddfdf %ED Test");  //$NON-NLS-1$//$NON-NLS-2$
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
}
