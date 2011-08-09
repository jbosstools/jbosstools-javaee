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

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.jboss.tools.cdi.seam.solder.core.test.SeamSolderTest;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.GenericInjectedPointHyperlink;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.GenericInjectedPointHyperlinkDetector;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.GenericInjectedPointListHyperlink;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class SeamGenericInjectedPointHyperlinkTest extends SeamSolderTest {
	static final String HYPERLINK_NAME = GenericInjectedPointHyperlink.class.getName();
	static final String LIST_HYPERLYNK_NAME = GenericInjectedPointListHyperlink.class.getName();
	public SeamGenericInjectedPointHyperlinkTest() {}

	public void testFieldInjection() throws Exception {
		IHyperlink hyperlink = SeamConfigInjectedPointHyperlinkTest.checkHyperLinkInJava(
				"src/org/jboss/generic/MyBeanInjections.java", 
				getTestProject(), 
				"first1", 1, 
				new GenericInjectedPointHyperlinkDetector(), 
				HYPERLINK_NAME);
		hyperlink.open();
		
		SeamConfigInjectedPointHyperlinkTest.checkResult("MyConfigurationProducer.java", "getOneConfig");
	}

	public void testGenericInjection() throws Exception {
		IHyperlink hyperlink = SeamConfigInjectedPointHyperlinkTest.checkHyperLinkInJava(
				"src/org/jboss/generic/MyGenericBean.java", 
				getTestProject(), 
				"config", 1, 
				new GenericInjectedPointHyperlinkDetector(), 
				LIST_HYPERLYNK_NAME);
//		hyperlink.open();
//		
//		SeamConfigInjectedPointHyperlinkTest.checkResult("MyConfigurationProducer.java", "getOneConfig");
	}
}