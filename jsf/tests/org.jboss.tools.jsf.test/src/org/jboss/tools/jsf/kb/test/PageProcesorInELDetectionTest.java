/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.kb.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.web.kb.KbQuery;
import org.jboss.tools.jst.web.kb.KbQuery.Type;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.PageProcessor;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * Test Case for issue https://jira.jboss.org/jira/browse/JBIDE-15489 
 * 
 * @author Victor Rubezhny
 */
public class PageProcesorInELDetectionTest extends TestCase {
	static String PROJECT_NAME = "jsf2pr";
	IProject project;

	public PageProcesorInELDetectionTest() {
		super("PageProcessor in-EL Detection Test");
	}

	@Override
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	/**
	 * https://jira.jboss.org/jira/browse/JBIDE-15489
	 */
	public void testPageProcessorInELDetection() {
		IFile file = project.getFile("WebContent/varAttributes.xhtml");
		ELContext context = PageContextFactory.createPageContext(file);
		KbQuery query = new KbQuery();
		query.setMask(true);
		query.setOffset(368);
		query.setType(Type.ATTRIBUTE_VALUE);
		query.setValue("#{test.name}");
		query.setStringQuery("#{test.name} ");
		query.setParentTags(new String[] {"html", "h:dataTable", "h:dataTable", "h:outputText"});
		query.setParent("value");
		
		TextProposal[] proposals = PageProcessor.getInstance().getProposals(query, context, true);
		
		for (TextProposal proposal : proposals) {
			if("name : String - TestBeanForVarAttributes$Value".endsWith(proposal.getLabel()) && "".equals(proposal.getReplacementString()) && 
					0 == proposal.getPosition() && -1 == proposal.getStart() && -1 == proposal.getEnd()) {
				fail("Wrong EL proposal in non-EL context: \"" + proposal.getLabel() + "\".");
			}
		}
	}
}
