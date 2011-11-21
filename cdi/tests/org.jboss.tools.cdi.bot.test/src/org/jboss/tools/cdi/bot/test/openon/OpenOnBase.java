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

import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;

public class OpenOnBase extends CDITestBase{
	
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
	
	
	protected boolean checkBeanXMLDecoratorOpenOn(String packageName, String className) {
		wizard.createCDIComponent(CDIWizardType.DECORATOR, className, packageName,
				"java.util.Set");
		bot.editorByTitle("beans.xml").show();
		bot.cTabItem("Source").activate();
		openOnUtil.openOnDirect(packageName + "." + className, "beans.xml");
		return getEd().getTitle().equals(className + ".java");
	}
	
	protected boolean checkBeanXMLInterceptorOpenOn(String packageName, String className) {
		wizard.createCDIComponent(CDIWizardType.INTERCEPTOR, className, packageName,
				null);
		bot.editorByTitle("beans.xml").show();
		bot.cTabItem("Source").activate();
		openOnUtil.openOnDirect(packageName + "." + className, "beans.xml");
		return getEd().getTitle().equals(className + ".java");
	}
	
	protected boolean checkBeanXMLAlternativeOpenOn(String packageName, String className) {
		wizard.createCDIComponent(CDIWizardType.BEAN, className, packageName,
				"alternative+beansxml");
		bot.editorByTitle("beans.xml").show();
		bot.cTabItem("Source").activate();
		openOnUtil.openOnDirect(packageName + "." + className, "beans.xml");
		return getEd().getTitle().equals(className + ".java");
	}

}
