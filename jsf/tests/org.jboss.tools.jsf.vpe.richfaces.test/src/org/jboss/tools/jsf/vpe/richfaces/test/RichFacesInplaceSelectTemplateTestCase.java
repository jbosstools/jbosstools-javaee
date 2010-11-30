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


package org.jboss.tools.jsf.vpe.richfaces.test;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;


/**
 * The Class RichFacesInplaceSelectTemplateTestCase.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesInplaceSelectTemplateTestCase extends VpeTest {

    /** The Constant COMPONENTS_INPLACE_SELECT_INPLACE_SELECT_XHTML. */
    private static final String COMPONENTS_INPLACE_SELECT_INPLACE_SELECT_XHTML = "components/inplaceSelect/inplaceSelect.xhtml";

    /**
     * The Constructor.
     * 
     * @param name the name
     */
    public RichFacesInplaceSelectTemplateTestCase(String name) {
        super(name);
    }

    /**
     * Test simple.
     * 
     * @throws CoreException the core exception
     * @throws PartInitException the part init exception
     * @throws Throwable the throwable
     */
    public void testSimple() throws PartInitException, CoreException, Throwable {
        performTestForVpeComponent((IFile) TestUtil.getComponentPath(COMPONENTS_INPLACE_SELECT_INPLACE_SELECT_XHTML,
        		RichFacesAllTests.IMPORT_PROJECT_NAME));
    }

}
