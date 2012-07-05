/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam3.bot.test.tests;

import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.seam3.bot.test.base.Seam3TestBase;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibrary;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author jjankovi
 *
 */
public class SeamConfigInjectOpenOnTest extends Seam3TestBase {

	private static String projectName = "seamConfigInjectOpenOn";
	private final String SEAM_CONFIG = "seam-beans.xml";
	
	@BeforeClass
	public static void setup() {
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
	}
	
	@Before
	public void openSeamConfig() {
		packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"test", "Report.java");		
	}
	
	@Test
	public void testBasicInjectOpenOn() {
		
		assertTrue(openOnUtil.openOnByOption("path1", 
				"Report.java", "Open Resource in seam-beans.xml"));
		assertExpectedOpenedClass(SEAM_CONFIG, "<r:Resource path=\"value\">");
		
	}
	
	@Test
	public void testQualifierInjectOpenOn() {
		
		assertTrue(openOnUtil.openOnByOption("path2", 
				"Report.java", "Open Resource in seam-beans.xml"));
		assertExpectedOpenedClass(SEAM_CONFIG, "<r:Resource>");
		
	}
	
	private void assertExpectedOpenedClass(String className,
			String selectedString) {
		assertEquals(className, bot.activeEditor().getTitle());
		assertEquals(selectedString, bot.activeEditor().toTextEditor().getSelection());
	}
	
}
