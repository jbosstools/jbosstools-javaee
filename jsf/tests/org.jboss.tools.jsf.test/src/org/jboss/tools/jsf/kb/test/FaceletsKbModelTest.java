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
package org.jboss.tools.jsf.kb.test;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbQuery;
import org.jboss.tools.jst.web.kb.KbQuery.Type;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.PageProcessor;
import org.jboss.tools.jst.web.kb.internal.scanner.LoadedDeclarations;
import org.jboss.tools.jst.web.kb.internal.scanner.ScannerException;
import org.jboss.tools.jst.web.kb.internal.scanner.XMLScanner;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.TestProjectProvider;

public class FaceletsKbModelTest extends TestCase {

	private IProject testProject;
	TestProjectProvider provider = null;
	boolean makeCopy = true;

	public FaceletsKbModelTest() {
		super("Kb Model Test");
	}

	protected void setUp() throws Exception {
		if(testProject==null) {
			testProject = ResourcesPlugin.getWorkspace().getRoot().getProject("TestKbModel");
			assertNotNull("Can't load TestKbModel", testProject); //$NON-NLS-1$
		}
	}

	private IKbProject getKbProject() {
		IKbProject kbProject = null;
		try {
			kbProject = (IKbProject)testProject.getNature(IKbProject.NATURE_ID);
		} catch (Exception e) {
			JUnitUtils.fail("Cannot get seam nature.",e);
		}
		return kbProject;
	}

	public void testTldXMLScanner() {
		IKbProject kbProject = getKbProject();
		
		IFile f = testProject.getFile("WebContent/WEB-INF/faces-config.xml");
		assertNotNull(f);
		XMLScanner scanner = new XMLScanner();
		List<ITagLibrary> ls = null;		
		try {
			LoadedDeclarations ds = scanner.parse(f, kbProject);
			ls = ds.getLibraries();
		} catch (ScannerException e) {
			JUnitUtils.fail("Error in xml scanner",e);
		}
		assertEquals(1, ls.size());
		assertTrue(ls.get(0).getComponents().length > 0);

		ls = null;
		f = testProject.getFile("WebContent/facelet-taglib.xml");
		assertNotNull(f);
		try {
			LoadedDeclarations ds = scanner.parse(f, kbProject);
			ls = ds.getLibraries();
		} catch (ScannerException e) {
			JUnitUtils.fail("Error in xml scanner",e);
		}
		assertEquals(1, ls.size());
		assertTrue(ls.get(0).getComponents().length > 0);
		
		ls = null;
		f = testProject.getFile("WebContent/facelet-taglib2.xml");
		assertNotNull(f);
		try {
			LoadedDeclarations ds = scanner.parse(f, kbProject);
			ls = ds.getLibraries();
		} catch (ScannerException e) {
			JUnitUtils.fail("Error in xml scanner",e);
		}
		assertEquals(1, ls.size());
		assertTrue(ls.get(0).getComponents().length > 0);
	}

	/**
	 * https://jira.jboss.org/jira/browse/JBIDE-6284
	 * https://jira.jboss.org/browse/JBIDE-7210
	 */
	public void testFFacet() {
		IFile file = testProject.getFile("WebContent/pages/inputUserName.xhtml");
		ELContext context = PageContextFactory.createPageContext(file);
		KbQuery query = new KbQuery();
		query.setMask(true);
		query.setOffset(356);
		query.setType(Type.TAG_NAME);
		query.setPrefix("f");
		query.setUri("http://java.sun.com/jsf/core");
		query.setValue("f:facet");

		TextProposal[] proposals = PageProcessor.getInstance().getProposals(query, context, true);
		boolean ok = false;
		for (TextProposal proposal : proposals) {
			if("<f:facet name=\"\">".equals(proposal.getReplacementString())) {
				ok = true;
			}
			if(proposal.getReplacementString().endsWith("/>")) {
				fail("<f:facet /> proposal found.");
			}
		}
		assertTrue("Can't find <f:facet name=\"\"> proposal.", ok);
	}

	/**
	 * https://jira.jboss.org/browse/JBIDE-7210
	 */
	public void testActionparam() {
		IFile file = testProject.getFile("WebContent/pages/actionparam.xhtml");
		ELContext context = PageContextFactory.createPageContext(file);
		KbQuery query = new KbQuery();
		query.setMask(true);
		query.setOffset(400);
		query.setType(Type.TAG_NAME);
		query.setPrefix("a4j");
		query.setUri("http://richfaces.org/a4j");
		query.setValue("a4j:actionpara");

		TextProposal[] proposals = PageProcessor.getInstance().getProposals(query, context, true);
		boolean ok = false;
		for (TextProposal proposal : proposals) {
			if("<a4j:actionparam name=\"\" value=\"\" />".equals(proposal.getReplacementString())) {
				ok = true;
			}
			if(proposal.getReplacementString().endsWith(" >")) {
				fail("Not closed <a4j:actionparam > proposal found.");
			}
		}
		assertTrue("Can't find <a4j:actionparam name=\"\" value=\"\" /> proposal.", ok);
	}

	/**
	 * https://jira.jboss.org/jira/browse/JBIDE-5231
	 */
	public void testSeamPdf() {
		IFile file = testProject.getFile("WebContent/pages/testSeamPdfAndMail.xhtml");
		ELContext context = PageContextFactory.createPageContext(file);
		KbQuery query = new KbQuery();
		query.setMask(true);
		query.setOffset(356);
		query.setType(Type.ATTRIBUTE_NAME);
		query.setParentTags(new String[]{"p:document"});
		query.setPrefix("p");
		query.setUri("http://jboss.com/products/seam/pdf");
		query.setValue("ori");

		TextProposal[] proposals = PageProcessor.getInstance().getProposals(query, context);
		for (TextProposal proposal : proposals) {
			if("orientation".equals(proposal.getReplacementString())) {
				return;
			}
		}
		fail("Can't find <p:document orientation=\"\"> proposal.");
	}

	/**
	 * https://jira.jboss.org/jira/browse/JBIDE-5198
	 */
	public void testSeamMail() {
		IFile file = testProject.getFile("WebContent/pages/testSeamPdfAndMail.xhtml");
		ELContext context = PageContextFactory.createPageContext(file);
		KbQuery query = new KbQuery();
		query.setMask(true);
		query.setOffset(356);
		query.setType(Type.ATTRIBUTE_NAME);
		query.setParentTags(new String[]{"m:message"});
		query.setPrefix("m");
		query.setUri("http://jboss.com/products/seam/mail");
		query.setValue("pre");

		TextProposal[] proposals = PageProcessor.getInstance().getProposals(query, context);
		for (TextProposal proposal : proposals) {
			if("precedence".equals(proposal.getReplacementString())) {
				return;
			}
		}
		fail("Can't find <m:message precedence=\"\"> proposal.");
	}

	/**
	 * https://jira.jboss.org/jira/browse/JBIDE-3875
	 */
	public void testFacetNames() {
		IFile file = testProject.getFile("WebContent/pages/facetname.xhtml");
		ELContext context = PageContextFactory.createPageContext(file);
		KbQuery query = new KbQuery();
		query.setMask(true);
		query.setOffset(302);
		query.setType(Type.ATTRIBUTE_VALUE);
		query.setPrefix("f");
		query.setUri("http://java.sun.com/jsf/core");
		query.setValue("h");
		query.setParentTags(new String[]{"rich:page", "f:facet"});
		query.setParent("name");
		query.setStringQuery("h");

		TextProposal[] proposals = PageProcessor.getInstance().getProposals(query, context);
		for (TextProposal proposal : proposals) {
			if("header".equals(proposal.getReplacementString())) {
				return;
			}
		}
		fail("Can't find \"header\" proposal.");
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
}