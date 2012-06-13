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

import org.jboss.tools.cdi.bot.test.annotations.ValidationType;
import org.jboss.tools.cdi.bot.test.quickfix.validators.BeanValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.IValidationProvider;
import org.jboss.tools.cdi.seam3.bot.test.base.Seam3TestBase;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author jjankovi
 *
 */
public class LoggerSupportTest extends Seam3TestBase {

	private static String projectName = "logger1";
	private IValidationProvider validationProvider = new BeanValidationProvider();
	
	@BeforeClass
	public static void setup() {
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
	}
	
	@Test
	public void testLoggerSupport() {
		
		/* test there is not any validation error */
		assertLoggerIsInjectable();
		
	}
	
	private void assertLoggerIsInjectable() {
		assertNull("There is not bean eligible for injection to injection point Logger", 
				quickFixHelper.getProblem(
						ValidationType.NO_BEAN_ELIGIBLE, projectName, validationProvider));
	}
	
}
