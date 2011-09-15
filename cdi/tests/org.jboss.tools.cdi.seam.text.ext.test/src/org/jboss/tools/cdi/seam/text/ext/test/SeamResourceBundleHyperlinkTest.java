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

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.jboss.tools.cdi.seam.core.test.SeamCoreTest;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlink;

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
		regionList.add(new CDIHyperlinkTestUtil.TestRegion(398, 11, 
				new CDIHyperlinkTestUtil.TestHyperlink[]{new CDIHyperlinkTestUtil.TestHyperlink(ELHyperlink.class, "Open property 'home_header' of bundle 'messages'", "messages.properties")}));
		regionList.add(new CDIHyperlinkTestUtil.TestRegion(389, 8, 
				new CDIHyperlinkTestUtil.TestHyperlink[]{new CDIHyperlinkTestUtil.TestHyperlink(ELHyperlink.class, "Open bundle 'messages'", (String)null)}));
		regionList.add(new CDIHyperlinkTestUtil.TestRegion(381, 7, 
				new CDIHyperlinkTestUtil.TestHyperlink[]{new CDIHyperlinkTestUtil.TestHyperlink(ELHyperlink.class, "Open bundle 'messages'", (String)null)}));
		regionList.add(new CDIHyperlinkTestUtil.TestRegion(381, 16, 
				new CDIHyperlinkTestUtil.TestHyperlink[]{new CDIHyperlinkTestUtil.TestHyperlink(ELHyperlink.class, "Open bundle 'messages'", (String)null)}));

		for (CDIHyperlinkTestUtil.TestRegion testRegion : regionList) {
			IHyperlink[] hyperlinks = CDIHyperlinkTestUtil.detectELHyperlinks(PAGE_NAME, getTestProject(), testRegion.getRegion().getOffset());
			assertNotNull("Hyperlink not found!", hyperlinks);
			for (IHyperlink hyperlink : hyperlinks) {
				assertTrue("Hyperlink found is not EL Hyperlink", (hyperlink instanceof ELHyperlink));
			}

			CDIHyperlinkTestUtil.checkTestRegion(hyperlinks, testRegion);
		}
	}
}