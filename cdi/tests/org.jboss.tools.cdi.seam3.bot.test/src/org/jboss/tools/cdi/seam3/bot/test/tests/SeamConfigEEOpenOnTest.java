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
public class SeamConfigEEOpenOnTest extends Seam3TestBase {

	private static String projectName = "seamConfigEEOpenOn";
	private final String SEAM_CONFIG = "seam-beans.xml";
	
	@BeforeClass
	public static void setup() {
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
	}
	
	@Before
	public void openSeamConfig() {
		packageExplorer.openFile(projectName, CDIConstants.WEBCONTENT, 
				CDIConstants.WEB_INF, SEAM_CONFIG).toTextEditor();
		bot.cTabItem("Source").activate();
	}
	
	@Test
	public void testAlternativeOpenOn() {
		
		/* open on bean class */
		openOnUtil.openOnDirect("s:Alternative", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("Alternative.class", "javax.enterprise.inject.Alternative");
		
	}
	
	@Test
	public void testDecoratorOpenOn() {
		
		/* open on bean class */
		openOnUtil.openOnDirect("s:Decorator", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("Decorator.class", "javax.decorator.Decorator");
		
	}
	
	@Test
	public void testInjectOpenOn() {
		
		/* open on bean class */
		openOnUtil.openOnDirect("s:Inject", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("Inject.class", "javax.inject.Inject");
		
	}
	
	@Test
	public void testInterceptorOpenOn() {
		
		/* open on bean class */
		openOnUtil.openOnDirect("s:Interceptor", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("Interceptor.class", "javax.interceptor.Interceptor");
		
	}
	
	@Test
	public void testInterceptorBindingOpenOn() {
		
		/* open on bean class */
		openOnUtil.openOnDirect("s:InterceptorBinding", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("InterceptorBinding.class", "javax.interceptor.InterceptorBinding");
		
	}
	
	@Test
	public void testObservesOpenOn() {
		
		/* open on bean class */
		openOnUtil.openOnDirect("s:Observes", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("Observes.class", "javax.enterprise.event.Observes");
		
	}
	
	@Test
	public void testProducesOpenOn() {
		
		/* open on bean class */
		openOnUtil.openOnDirect("s:Produces", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("Produces.class", "javax.enterprise.inject.Produces");
		
	}
	
	@Test
	public void testQualifierOpenOn() {
		
		/* open on bean class */
		openOnUtil.openOnDirect("s:Qualifier", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("Qualifier.class", "javax.inject.Qualifier");
		
	}
	
	@Test
	public void testSpecializesOpenOn() {
		
		/* open on bean class */
		openOnUtil.openOnDirect("s:Specializes", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("Specializes.class", "javax.enterprise.inject.Specializes");
		
	}
	
	@Test
	public void testStereotypeOpenOn() {
	
		/* open on bean class */
		openOnUtil.openOnDirect("s:Stereotype", SEAM_CONFIG);
		
		/* test opened object */
		assertExpectedOpenedClass("Stereotype.class", "javax.enterprise.inject.Stereotype");
		
	}
	
	private void assertExpectedOpenedClass(String className,
			String packageName) {
		assertEquals(className, bot.activeEditor().getTitle());
		assertContains(packageName, bot.activeEditor().toTextEditor().getText());
	}
	
}
