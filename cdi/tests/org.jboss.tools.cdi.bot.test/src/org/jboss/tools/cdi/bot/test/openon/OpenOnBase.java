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

package org.jboss.tools.cdi.bot.test.openon;

import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;

/**
 * test base for OpenOn-like CDI tests
 * 
 * @author jjankovi
 *
 */

public class OpenOnBase extends CDITestBase {
	
	protected static final String[] events = { "myBean1Q1Event", "myBean1AnyEvent",
			"myBean2Q1Event", "myBean2AnyEvent", "myBean1Q2Event",
			"myBean2Q2Event", "myBean1Q1Event.fire(new MyBean1());",
			"myBean1AnyEvent.fire(new MyBean1())",
			"myBean2Q1Event.fire(new MyBean2())",
			"myBean2AnyEvent.fire(new MyBean2())",
			"myBean1Q2Event.fire(new MyBean1())",
			"myBean2Q2Event.fire(new MyBean2())",
			"myBean1AnyEvent.fire(new MyBean2())" };
	
	protected static final String[] observers = { "observeNoQualifierMyBean1",
			"observeAnyMyBean1", "observeQ1MyBean1",
			"observeNoQualifierMyBean2", "observeAnyMyBean2",
			"observeQ1MyBean2", "observeQ2MyBean1", "observeQ2MyBean2" };
	
	/**
	 * Method creates Decorator component with entered name and package name.
	 * Then it opens beans.xml and simulates direct openOn through method openOnDirect.
	 * Finally it checks if the class which was opened-on to is correct.
	 * @param packageName
	 * @param className
	 * @return
	 */
	protected boolean checkBeanXMLDecoratorOpenOn(String packageName, String className) {
		wizard.createCDIComponent(CDIWizardType.DECORATOR, className, packageName,
				"java.util.Set");
		bot.editorByTitle(CDIConstants.BEANS_XML).show();
		bot.cTabItem("Source").activate();
		openOnUtil.openOnDirect(packageName + "." + className, CDIConstants.BEANS_XML);
		return getEd().getTitle().equals(className + ".java");
	}
	
	
	/**
	 * Method creates Interceptor component with entered name and package name.
	 * Then it opens beans.xml and simulates direct openOn through method openOnDirect.
	 * Finally it checks if the class which was opened-on to is correct.
	 * @param packageName
	 * @param className
	 * @return
	 */
	protected boolean checkBeanXMLInterceptorOpenOn(String packageName, String className) {
		wizard.createCDIComponent(CDIWizardType.INTERCEPTOR, className, packageName,
				null);
		bot.editorByTitle(CDIConstants.BEANS_XML).show();
		bot.cTabItem("Source").activate();
		openOnUtil.openOnDirect(packageName + "." + className, CDIConstants.BEANS_XML);
		return getEd().getTitle().equals(className + ".java");
	}
	
	/**
	 * Method creates Alternative Bean component with entered name and package name.
	 * Then it opens beans.xml and simulates direct openOn through method openOnDirect.
	 * Finally it checks if the class which was opened-on to is correct.
	 * @param packageName
	 * @param className
	 * @return
	 */
	protected boolean checkBeanXMLAlternativeOpenOn(String packageName, String className) {
		wizard.createCDIComponent(CDIWizardType.BEAN, className, packageName,
				"alternative+beansxml");
		bot.editorByTitle(CDIConstants.BEANS_XML).show();
		bot.cTabItem("Source").activate();
		openOnUtil.openOnDirect(packageName + "." + className, CDIConstants.BEANS_XML);
		return getEd().getTitle().equals(className + ".java");
	}

}
