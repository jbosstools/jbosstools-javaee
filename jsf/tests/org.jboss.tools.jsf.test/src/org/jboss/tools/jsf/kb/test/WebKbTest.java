/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
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
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.internal.proposal.CustomProposalType;
import org.jboss.tools.jst.web.kb.internal.taglib.CustomTagLibAttribute;
import org.jboss.tools.jst.web.kb.taglib.CustomTagLibManager;
import org.jboss.tools.jst.web.kb.taglib.IAttribute;
import org.jboss.tools.jst.web.kb.taglib.IContextComponent;
import org.jboss.tools.jst.web.kb.taglib.ICustomTagLibrary;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;

/**
 * @author Alexey Kazakov
 */
public class WebKbTest extends TestCase {

	private IProject testProject;
	private static final String[] CUSTOM_TAG_LIB_URIS = {"http://richfaces.org/a4j", "http://richfaces.org/rich", "http://java.sun.com/jsf/core", "http://java.sun.com/jsf/html", "http://java.sun.com/jsf/facelets", "http://www.w3.org/1999/xhtml/facelets", "http://jboss.com/products/seam/taglib", "http://java.sun.com/JSP/Page", "http://struts.apache.org/tags-html", "http://jboss.com/products/seam/pdf", "http://jboss.com/products/seam/mail", "jQueryMobile"};

	@Override
	protected void setUp() throws Exception {
		if(testProject==null) {
			testProject = ResourcesPlugin.getWorkspace().getRoot().getProject("TestKbModel");
			assertNotNull("Can't load TestKbModel", testProject); //$NON-NLS-1$
		}
	}

	public void testCustomTagLibs() {
		ICustomTagLibrary[] libs = CustomTagLibManager.getInstance().getLibraries();
		for (String uri : CUSTOM_TAG_LIB_URIS) {
			boolean found = false;
			for (ICustomTagLibrary lib : libs) {
				if(uri.equals(lib.getURI())) {
					found = true;
					break;
				}
			}
			assertTrue("Custom tag lib " + uri + " is not loaded.", found);
		}
	}

	public void testCustomExtensions() {
		CustomTagLibAttribute[] attributes = CustomTagLibManager.getInstance().getComponentExtensions();
		assertNotNull("Can't load component extensions.", attributes);
		assertFalse("Can't load component extensions.", attributes.length==0);
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-8953
	 */
	public void testLinksComponents() {
		ICustomTagLibrary[] libs = CustomTagLibManager.getInstance().getLibraries();
		ICustomTagLibrary facelets = null;
		ICustomTagLibrary h = null;
		ICustomTagLibrary a4j = null;
		for (ICustomTagLibrary lib : libs) {
			if("http://www.w3.org/1999/xhtml/facelets".equals(lib.getURI())) {
				facelets = lib;
			} else if("http://java.sun.com/jsf/html".equals(lib.getURI())) {
				h = lib;
			} else if("http://richfaces.org/a4j".equals(lib.getURI())) {
				a4j = lib;
			}
		}
		assertNotNull(facelets);
		assertNotNull(h);
		assertNotNull(a4j);

		IContextComponent link = (IContextComponent)facelets.getComponent("link");
		assertNotNull(link);

		IAttribute[] href = link.getAttributes(null, null, "href");
		assertFalse(href.length == 0);

		CustomProposalType[] proposals = ((CustomTagLibAttribute)href[0]).getProposals();
		boolean found = false;
		for (CustomProposalType proposalType : proposals) {
			found = found || "file".equals(proposalType.getType());
		}
		assertTrue(found);

		IContextComponent hLink = (IContextComponent)h.getComponent("link");
		assertNotNull(hLink);

		IAttribute[] value = hLink.getAttributes(null, null, "value");
		assertFalse(value.length==0);

		proposals = ((CustomTagLibAttribute)value[0]).getProposals();
		found = false;
		for (CustomProposalType proposalType : proposals) {
			found = found || "file".equals(proposalType.getType());
		}
		assertTrue(found);

		IContextComponent aLoadStyle = (IContextComponent)a4j.getComponent("loadStyle");
		assertNotNull(aLoadStyle);

		IAttribute[] src = aLoadStyle.getAttributes(null, null, "src");
		assertFalse(src.length==0);

		proposals = ((CustomTagLibAttribute)src[0]).getProposals();
		found = false;
		for (CustomProposalType proposalType : proposals) {
			found = found || "file".equals(proposalType.getType());
		}
		assertTrue(found);
	}

	/**
	 * JBIDE-8926
	 */
	public void testDuplicateLibs() {
		IFile f = testProject.getFile("WebContent/pages/template.xhtml");
		assertTrue(f.exists());

		ELContext context = PageContextFactory.createPageContext(f);

		assertTrue(context instanceof IPageContext);

		ITagLibrary[] templateLibs = ((IPageContext)context).getLibraries();

		f = testProject.getFile("WebContent/pages/duplicateLibs.xhtml");
		context = PageContextFactory.createPageContext(f);

		assertTrue(context instanceof IPageContext);

		ITagLibrary[] pageLibs = ((IPageContext)context).getLibraries();

		assertEquals(templateLibs.length, pageLibs.length);
	}
}