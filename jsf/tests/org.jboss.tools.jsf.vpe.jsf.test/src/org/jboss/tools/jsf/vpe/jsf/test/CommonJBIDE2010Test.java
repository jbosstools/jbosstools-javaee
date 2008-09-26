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

package org.jboss.tools.jsf.vpe.jsf.test;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.el.core.ELReferenceList;
import org.jboss.tools.common.resref.core.ResourceReference;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;


/**
 * The common test case for the all test cases related to the <a
 * href="https://jira.jboss.com:8443/jira/browse/JBIDE-2010"
 * >JBIDE-2010</a>issue.
 */
public abstract class CommonJBIDE2010Test extends VpeTest {
    
    /** The Constant DIR_TEST_PAGE_NAME_3. */
    protected static final String DIR_TEST_PAGE_NAME_3 = "JBIDE/2010/page3.xhtml"; //$NON-NLS-1$

    /** The Constant VALUE_5. */
    protected static final String VALUE_5 = "world"; //$NON-NLS-1$

    /** The Constant KEY_5. */
    protected static final String KEY_5 = "bean1.property3"; //$NON-NLS-1$

    /** The Constant VALUE_4. */
    protected static final String VALUE_4 = "background: red"; //$NON-NLS-1$

    /** The Constant KEY_4. */
    protected static final String KEY_4 = "bean1.property2"; //$NON-NLS-1$

    /** The Constant IMPORT_PROJECT_NAME. */
    public static final String IMPORT_PROJECT_NAME = "jsfTest"; //$NON-NLS-1$

    /** The Constant KEY_3. */
    protected static final String KEY_3 = "facesContext.requestPath"; //$NON-NLS-1$

    /** The Constant KEY_2. */
    protected static final String KEY_2 = "beanA.property2"; //$NON-NLS-1$

    /** The Constant DIR_TEST_PAGE_NAME. */
    protected static final String DIR_TEST_PAGE_NAME_2 = "JBIDE/2010/page2.jsp"; //$NON-NLS-1$

    /** The Constant DIR_TEST_PAGE_NAME. */
    protected static final String DIR_TEST_PAGE_NAME = "JBIDE/2010/page.jsp"; //$NON-NLS-1$

    /** The Constant KEY_1. */
    protected static final String KEY_1 = "beanA.property1"; //$NON-NLS-1$

    /** The Constant elValuesMap. */
    protected static final Map<String, String> elValuesMap = new HashMap<String, String>();

    /** The file. */
    protected IFile file;

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public CommonJBIDE2010Test(String name) {
        super(name);

    }

    /**
     * Sets the up.
     * 
     * @throws Exception the exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        elValuesMap.put(KEY_1, "/path/image/path/to/image/"); //$NON-NLS-1$
        elValuesMap.put(KEY_2, "/path2/"); //$NON-NLS-1$
        elValuesMap.put(KEY_3, "/facesContext/"); //$NON-NLS-1$
        elValuesMap.put(KEY_4, VALUE_4);
        elValuesMap.put(KEY_5, VALUE_5);
        file = (IFile) TestUtil.getComponentPath(getOpenPageName(), getOpenProjectName());
        ResourceReference[] entries = new ResourceReference[elValuesMap.size()];
        int i = 0;
        for (Entry<String, String> string : elValuesMap.entrySet()) {

            entries[i] = new ResourceReference(string.getKey(), ResourceReference.PROJECT_SCOPE);
            entries[i].setProperties(string.getValue());
            i++;

          
        }
        setValues(entries);
    }

    
    protected String getOpenPageName(){
        return DIR_TEST_PAGE_NAME;
    }
    
    protected String getOpenProjectName(){
        return IMPORT_PROJECT_NAME;
    }
    /**
     * Sets the values.
     * 
     * @param key the key
     * @param value the value
     * @param scope the scope
     * @param entries the entries
     */
    protected void setValues(ResourceReference[] entries) {
        ELReferenceList.getInstance().setAllResources(file, entries);
    }

    /**
     * Tear down.
     * 
     * @throws Exception the exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ELReferenceList.getInstance().setAllResources(this.file, new ResourceReference[0]);
        this.file = null;

    }

}
