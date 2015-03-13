/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.core.itest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.jboss.tools.batch.internal.core.el.JobPropertiesELCompletionEngine;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JobPropertiesELCompletionEngineTest extends TestCase {
	public static String PROJECT_NAME = "BatchTestProject"; //$NON-NLS-1$
	private IProject project;

	public JobPropertiesELCompletionEngineTest() {}

	@Override
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	public void testEL() throws Exception {
		IResource resource = project.findMember("/src/META-INF/batch-jobs/job-ca-4.xml"); //$NON-NLS-1$
		assertTrue(resource instanceof IFile);
		IFile file = (IFile)resource;
		String content = FileUtil.getContentFromEditorOrFile(file);
		ELContext context = PageContextFactory.createPageContext(file);

		checkProposals(context, content, "#{job", "name=\"x\" value=\"#{job", new String[]{"Parameters", "Properties"});
		checkProposals(context, content, "#{jobProperties['", "name=\"x\" value=\"#{jobProperties['", new String[]{"p1'", "p2'", "y'"});
		checkProposals(context, content, "#{jobProperties['p", "name=\"x\" value=\"#{jobProperties['", new String[]{"1'", "2'"});
	}

	void checkProposals(ELContext context, String content, String el, String search, String[] expectedProposals) {
		int offset = content.indexOf(search);
		assertTrue(offset > 0);
		offset += search.length();
		
		JobPropertiesELCompletionEngine resolver = new JobPropertiesELCompletionEngine();
		List<TextProposal> proposals = resolver.getProposals(context, el, offset);
		compareProposals(proposals, expectedProposals);
	}

	void compareProposals(List<TextProposal> actualProposals, String[] expectedProposals) {
		Set<String> expectedSet = new HashSet<String>();
		for (String s: expectedProposals) {
			expectedSet.add(s);
		}
		Set<String> actualSet = new HashSet<String>();
		assertEquals(expectedProposals.length, actualProposals.size());
		for (TextProposal p: actualProposals) {
			String s = p.getReplacementString();
			actualSet.add(s);
		}
		if(!actualSet.containsAll(expectedSet) || actualProposals.size() != expectedProposals.length) {
			StringBuilder sb = new StringBuilder();
			sb.append("Expected: ");
			for (String s: expectedProposals) {
				sb.append(" ").append(s);
			}
			sb.append(" Found: ");
			for (TextProposal p: actualProposals) {
				String s = p.getReplacementString();
				sb.append(" ").append(s);
			}
			fail(sb.toString());
		}
	}
}
