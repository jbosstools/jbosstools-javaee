/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.text.ext.test;


/**
 * beans.xml OpenOns test
 * 
 * @author Alexey Kazakov
 */
public class BeansXmlHyperLinkTest extends HyperlinkDetectorTest {

	public void testAlternativesClassOpenOns() throws Exception {
		checkHyperLinkInXml("WebContent/WEB-INF/beans.xml", 73, "org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink");
	}

	public void testAlternativesStereotypeOpenOns() throws Exception {
		checkHyperLinkInXml("WebContent/WEB-INF/beans.xml", 395, "org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink");
	}

	public void testDecoratorClassOpenOns() throws Exception {
		checkHyperLinkInXml("WebContent/WEB-INF/beans.xml", 1159, "org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink");
	}

	public void testInterceptorClassOpenOns() throws Exception {
		checkHyperLinkInXml("WebContent/WEB-INF/beans.xml", 1761, "org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink");
	}
}