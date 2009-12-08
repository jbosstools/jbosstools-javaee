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
package org.jboss.tools.seam.ui.test.jbide;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.jst.jsp.test.ca.CommonContentAssistantTestCase;

/**
 * @author Eugene Stherbin
 *
 */
public class JBide2277Test extends CommonContentAssistantTestCase {
    protected static final String JBIDE_2277_PAGE="/WebContent/jbide2277/jbide2277.xhtml";
    
	public static final String EL_END_EXPRESSION = "}";
    public static final String EL_START_EXPRESSION = "#{";
	

    public static Test suite() {
        return new TestSuite(JBide2277Test.class);
    }

    /**
     * @see org.jboss.tools.jst.jsp.test.ca.CommonContentAssistantTestCase#getSetUpProjectName()
     */
    @Override
    protected String getSetUpProjectName() {
        return JBide2227TestCase.IMPORT_PROJECT_NAME;
    }
    
    public void testJbide2227(){
        final String[] proposals = new String[]{};
        final ICompletionProposal[] rst = checkProposals(JBIDE_2277_PAGE,574, proposals, false);
        
        checkResult(rst);
    }

    /**
     * @param rst
     */
    private void checkResult(ICompletionProposal[] rst) {
        assertTrue(rst.length > 5);
        for (ICompletionProposal p : rst) {
            //Check stars with  #{
            assertTrue(p.getDisplayString().startsWith(EL_START_EXPRESSION));
        }
    }
}