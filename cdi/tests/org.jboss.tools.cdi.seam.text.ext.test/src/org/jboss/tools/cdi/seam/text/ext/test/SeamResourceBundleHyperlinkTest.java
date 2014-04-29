/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.seam.text.ext.test;

import java.util.ArrayList;

import org.jboss.tools.cdi.seam.core.test.SeamCoreTest;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil;
import org.jboss.tools.jst.web.ui.internal.text.ext.hyperlink.ELHyperlink;
import org.jboss.tools.jst.web.ui.internal.text.ext.hyperlink.ELHyperlinkDetector;

/**
 * The JUnit test cases for JBIDE-9288 issue 
 * 
 * @author Victor Rubezhny
 */
public class SeamResourceBundleHyperlinkTest extends SeamCoreTest {
	private static final String PAGE_NAME = "WebContent/seam-international.xhtml";
	
	/**
	 * The test case finds the hyperlinks for bundle property and checks that there are only Messages Hyperlinks
	 * @throws Exception 
	 */
	public void testSeamResourceBundleHyperlink () throws Exception {
		ArrayList<CDIHyperlinkTestUtil.TestRegion> regionList = new ArrayList<CDIHyperlinkTestUtil.TestRegion>();
		regionList.add(new CDIHyperlinkTestUtil.TestRegion(/*381, 16*/"bundles.message", 
				new CDIHyperlinkTestUtil.TestHyperlink[]{new CDIHyperlinkTestUtil.TestHyperlink(ELHyperlink.class, "Open bundle 'messages'", (String)null)}));
		regionList.add(new CDIHyperlinkTestUtil.TestRegion(/*398, 11*/"home_heade", 
				new CDIHyperlinkTestUtil.TestHyperlink[]{new CDIHyperlinkTestUtil.TestHyperlink(ELHyperlink.class, "Open property 'home_header' of bundle 'messages'", "messages.properties")}));
		
		CDIHyperlinkTestUtil.checkRegions(getTestProject(), PAGE_NAME, regionList, new ELHyperlinkDetector());
	}
}