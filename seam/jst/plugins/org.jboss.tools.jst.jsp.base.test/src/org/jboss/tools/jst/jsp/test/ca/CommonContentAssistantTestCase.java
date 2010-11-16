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


package org.jboss.tools.jst.jsp.test.ca;


import org.jboss.tools.test.util.TestProjectProvider;


/**
 * Abstract class contains of common methods.
 * 
 * @author Eugene Stherbin
 * 
 */
public abstract class CommonContentAssistantTestCase extends ContentAssistantTestCase {
    /** The make copy. */
    protected boolean makeCopy;

    /** The provider. */
    protected TestProjectProvider provider;

    protected abstract String getSetUpProjectName();

    @Override
    public void setUp() throws Exception {
        provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", null, getSetUpProjectName(), makeCopy); //$NON-NLS-1$
        project = provider.getProject();
    }

    @Override
    public void tearDown() throws Exception {
        if (provider != null) {
            provider.dispose();
        }
    }
}
