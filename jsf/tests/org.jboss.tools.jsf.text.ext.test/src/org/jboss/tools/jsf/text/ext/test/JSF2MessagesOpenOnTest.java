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
import org.jboss.tools.jst.text.ext.test.HyperlinkTestUtil;
import org.jboss.tools.jst.text.ext.test.HyperlinkTestUtil.TestRegion;
import org.jboss.tools.jst.text.ext.test.HyperlinkTestUtil.TestHyperlink;

/**
 * 
 * @author jeremy
 *
 */
public class JSF2MessagesOpenOnTest extends TestCase {
	private static final String PROJECT_NAME = "JSF2CompositeOpenOn";
	private static final String PAGE_NAME =  "/WebContent/pages/inputname.xhtml";
	
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	public JSF2MessagesOpenOnTest() {
		super("JSF2 OpenOn on messages test");
	}
	
	public void testJSF2MessagesHyperlink() throws Exception{

		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion("${", new TestHyperlink[]{}));
		regionList.add(new TestRegion("registeredMsg"/*881, 13*/, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open bundle 'resources'", "resources.properties")}));
		regionList.add(new TestRegion("promp"/*896, 5*/, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open property 'prompt' of bundle 'resources'", "resources.properties")}));

		regionList.add(new TestRegion("${", new TestHyperlink[]{}));
		regionList.add(new TestRegion("registeredMsg"/*1004, 13*/, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open bundle 'resources'", "resources.properties")}));
		regionList.add(new TestRegion("demo.long.named.propert"/*1019, 25*/, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open property 'demo.long.named.property' of bundle 'resources'", "resources.properties")}));

		regionList.add(new TestRegion("${", new TestHyperlink[]{}));
		regionList.add(new TestRegion("pageMsgs"/*1078, 7*/, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open bundle 'resources'", "resources.properties")}));
		regionList.add(new TestRegion("prompt"/*1087, 5*/, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open property 'prompt' of bundle 'resources'", "resources.properties")}));

		regionList.add(new TestRegion("${", new TestHyperlink[]{}));
		regionList.add(new TestRegion("pageMsg"/*1125, 7*/, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open bundle 'resources'", "resources.properties")}));
		regionList.add(new TestRegion("demo.long.named.propert"/*1134, 25*/, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open property 'demo.long.named.property' of bundle 'resources'", "resources.properties")}));
		
		HyperlinkTestUtil.checkRegions(project, PAGE_NAME, regionList, new ELHyperlinkDetector());
		
	}

}

