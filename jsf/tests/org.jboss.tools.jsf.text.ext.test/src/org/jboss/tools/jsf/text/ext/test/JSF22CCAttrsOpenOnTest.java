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
package org.jboss.tools.jsf.text.ext.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.jsf.text.ext.test.JSFHyperlinkTestUtil.TestRegion;
import org.jboss.tools.jsf.text.ext.test.JSFHyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.jst.web.ui.internal.text.ext.hyperlink.ELHyperlink;
import org.jboss.tools.jst.web.ui.internal.text.ext.hyperlink.ELHyperlinkDetector;
import org.jboss.tools.test.util.JobUtils;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JSF22CCAttrsOpenOnTest extends TestCase {
	private static final String PROJECT_NAME = "JSF22CompositeOpenOn";
	private static final String PAGE_NAME =  "/WebContent/resources/demo/input.xhtml";
	private static final String PAGE2_NAME =  "/WebContent/resources/demo/input2.xhtml";
	
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	public void testCCAttrsHyperlink1() throws Exception{

		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(658+2, 7, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		regionList.add(new TestRegion(667+2, 4, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		
		regionList.add(new TestRegion(706+2, 7, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		regionList.add(new TestRegion(715+2, 4, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));

		regionList.add(new TestRegion(759+2, 7, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		regionList.add(new TestRegion(768+2, 5, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));

		regionList.add(new TestRegion(786+2, 7, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		regionList.add(new TestRegion(795+2, 10, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		
		JSFHyperlinkTestUtil.checkRegions(project, PAGE_NAME, regionList, new ELHyperlinkDetector());
		
	}

	public void testCCAttrsHyperlink2() throws Exception{

		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(610+2, 7, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		regionList.add(new TestRegion(619+2, 4, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		
		regionList.add(new TestRegion(658+2, 7, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		regionList.add(new TestRegion(667+2, 4, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));

		regionList.add(new TestRegion(711+2, 7, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		regionList.add(new TestRegion(720+2, 5, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));

		regionList.add(new TestRegion(738+2, 7, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		regionList.add(new TestRegion(747+2, 10, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open a Custom Component Attribute definition")}));
		
		JSFHyperlinkTestUtil.checkRegions(project, PAGE2_NAME, regionList, new ELHyperlinkDetector());
		
	}

}

