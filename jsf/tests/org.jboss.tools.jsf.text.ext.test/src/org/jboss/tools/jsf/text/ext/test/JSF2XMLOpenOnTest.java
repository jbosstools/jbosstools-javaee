/*******************************************************************************
 * Copyright (c) 2011 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlink;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlinkDetector;
import org.jboss.tools.jsf.text.ext.test.JSFHyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.jsf.text.ext.test.JSFHyperlinkTestUtil.TestRegion;
import org.jboss.tools.test.util.JobUtils;

public class JSF2XMLOpenOnTest extends TestCase {
	private static final String PROJECT_NAME = "JSF2CompositeOpenOn";
	private static final String PAGE_NAME =  "/WebContent/WEB-INF/faces-config.xml";
	
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	public JSF2XMLOpenOnTest() {
		super("JSF2 OpenOn on messages test");
	}
	
	public void testELHyperlink() throws Exception{

		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(992, 5, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open 'Person - demo'", "Person.java")}));
		regionList.add(new TestRegion(999, 3, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open 'Person.getName() - demo'", "Person.java")}));
		
		JSFHyperlinkTestUtil.checkRegions(project, PAGE_NAME, regionList, new ELHyperlinkDetector());
		
	}

}

