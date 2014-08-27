/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.jsp.ca.test;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

/**
 * @author Alexey Kazakov
 */
public class CAForNotJsf extends ContentAssistantTestCase{
	TestProjectProvider provider = null;
	private static final String PROJECT_NAME = "CAForNotJSF";
	private static final String HTML_PAGE_NAME = "/WebContent/index.html";
	private static final String XHTML_PAGE_NAME = "/WebContent/index.xhtml";

	@Override
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, true); 
		project = provider.getProject();
	}

	@Override
	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
	
	public void testCAForHtml() throws BadLocationException{
		String[] proposals = {"#{}"};
		checkProposals(XHTML_PAGE_NAME, "content goes here", 1, proposals, false, false);

		openEditor(HTML_PAGE_NAME);

		IRegion reg = new FindReplaceDocumentAdapter(document).find(0, "content goes here", true, true, false, false);
		List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, reg.getOffset() + 1);
		boolean found = compareProposal("person", null, res.toArray(new ICompletionProposal[0]));
		assertFalse(found);
	}
}