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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.internal.scanner.LoadedDeclarations;
import org.jboss.tools.jst.web.kb.internal.scanner.ScannerException;
import org.jboss.tools.jst.web.kb.internal.scanner.XMLScanner;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.TestProjectProvider;

import junit.framework.TestCase;

public class FaceletsKbModelTest extends TestCase {

	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = true;

	public FaceletsKbModelTest() {
		super("Kb Model Test");
	}

	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.test",
				null,"TestKbModel" ,true);
		project = provider.getProject();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		this.project.build(IncrementalProjectBuilder.FULL_BUILD, null);
	}

	private IKbProject getKbProject() {
		IKbProject kbProject = null;
		try {
			kbProject = (IKbProject)project.getNature(IKbProject.NATURE_ID);
		} catch (Exception e) {
			JUnitUtils.fail("Cannot get seam nature.",e);
		}
		return kbProject;
	}

	public void testTldXMLScanner() {
		IKbProject kbProject = getKbProject();
		
		IFile f = project.getFile("WebContent/WEB-INF/faces-config.xml");
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
		f = project.getFile("WebContent/facelet-taglib.xml");
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
		f = project.getFile("WebContent/facelet-taglib2.xml");
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

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
}
