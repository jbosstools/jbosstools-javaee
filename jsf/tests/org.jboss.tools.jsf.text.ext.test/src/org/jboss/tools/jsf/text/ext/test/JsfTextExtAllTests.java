/*******************************************************************************
 * Copyright (c) 2011-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfTextExtAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(JsfTextExtAllTests.class.getName());

		suite.addTest(new ProjectImportTestSetup(new TestSuite(OpenOnsTest.class),
				"org.jboss.tools.jsf.text.ext.test",
				new String[]{"projects/HiperlinksTestProject"},
				new String[]{"HiperlinksTestProject"}));
		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				JSF2CompositeOpenOnTest.class, 
				JSF2CompositeLibOpenOnTest.class,
				JSF2MessagesOpenOnTest.class,
				JSF2CSSStylesheetOpenOnTest.class,
				JSF2CCAttrsOpenOnTest.class,
				JSF2XMLOpenOnTest.class,
				JSPELHyperlinkUIParamTest.class,
				JSF2BeanMapValuesOpenOnTest.class),
			"org.jboss.tools.jsf.text.ext.test",
			new String[]{"projects/JSF2CompositeOpenOn"},
			new String[]{"JSF2CompositeOpenOn"}));

		suite.addTest(new ProjectImportTestSetup(new TestSuite(
				JSPStylesheetRelLinkHyperlinkTest.class,
				JSFLinksReferencedWithELOpenOnTest.class,
				JSPELHyperlinkTestForELInTagBodyTest.class), 
			"org.jboss.tools.jsf.text.ext.test",
			new String[]{"projects/jsfHyperlinkTests"},
			new String[]{"jsfHyperlinkTests"}));

		return suite;
	}
}