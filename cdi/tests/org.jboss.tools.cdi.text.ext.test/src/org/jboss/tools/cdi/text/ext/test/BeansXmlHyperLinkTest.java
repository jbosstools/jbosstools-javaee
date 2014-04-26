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

import java.util.ArrayList;

import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestRegion;
import org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;

/**
 * beans.xml OpenOns test
 * 
 * @author Alexey Kazakov
 */
public class BeansXmlHyperLinkTest extends TCKTest {
	
	public void testAlternativesAlternativesStereotypeDecoratorInterceptorClassOpenOns() throws Exception {
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		regionList.add(new TestRegion(/*72*/"org.jboss.jsr299.tck.tests.policy.broken.not.policy.Ca",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.policy.broken.not.policy.Cat'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.policy.broken.incorrect.name.NonExistingClas",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.policy.broken.incorrect.name.NonExistingClass'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.policy.broken.not.policy.stereotype.NotExistingStereotyp",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.policy.broken.not.policy.stereotype.NotExistingStereotype'")}));

		regionList.add(new TestRegion(/*394*/"org.jboss.jsr299.tck.tests.policy.broken.not.policy.stereotype.Moc",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.policy.broken.not.policy.stereotype.Mock'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.jbt.validation.beansxml.AlternativeStereotyp",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.jbt.validation.beansxml.AlternativeStereotype'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.policy.broken.same.type.twice.Do",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.policy.broken.same.type.twice.Dog'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.policy.broken.same.type.twice.Ca",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.policy.broken.same.type.twice.Cat'")}));
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.policy.broken.same.type.twice.Ca",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.policy.broken.same.type.twice.Cat'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.jbt.validation.beansxml.DuplicatedAlternativeStereotyp",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.jbt.validation.beansxml.DuplicatedAlternativeStereotype'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.jbt.validation.beansxml.DuplicatedAlternativeStereotyp",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.jbt.validation.beansxml.DuplicatedAlternativeStereotype'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.policy.EnabledSheepProduce",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.policy.EnabledSheepProducer'")}));
		regionList.add(new TestRegion(/*1159-11*/"org.jboss.jsr299.tck.tests.policy.EnabledPolicyStereotyp",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.policy.EnabledPolicyStereotype'")}));
		
		regionList.add(new TestRegion("com.acme.NonExistantDecoratorClas",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'com.acme.NonExistantDecoratorClass'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.decorators.resolution.BarDecorato",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.decorators.resolution.BarDecorator'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.decorators.definition.broken.enabledDecoratorIsNotDecorator.TimestampLogge",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.decorators.definition.broken.enabledDecoratorIsNotDecorator.TimestampLogger'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.decorators.definition.broken.decoratorListedTwiceInBeansXml.PresentDecorato",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.decorators.definition.broken.decoratorListedTwiceInBeansXml.PresentDecorator'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.decorators.definition.broken.decoratorListedTwiceInBeansXml.PresentDecorato",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.decorators.definition.broken.decoratorListedTwiceInBeansXml.PresentDecorator'")}));
		
		regionList.add(new TestRegion("com.acme.Fo",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'com.acme.Foo'")}));
		regionList.add(new TestRegion(/*1841*/"org.jboss.jsr299.tck.tests.jbt.validation.interceptors.CatIntercepto",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.jbt.validation.interceptors.CatInterceptor'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.interceptors.definition.broken.nonInterceptorClassInBeansXml.Fo",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.interceptors.definition.broken.nonInterceptorClassInBeansXml.Foo'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.interceptors.definition.broken.sameClassListedTwiceInBeansXml.FordIntercepto",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.interceptors.definition.broken.sameClassListedTwiceInBeansXml.FordInterceptor'")}));
		
		regionList.add(new TestRegion("org.jboss.jsr299.tck.tests.interceptors.definition.broken.sameClassListedTwiceInBeansXml.FordIntercepto",  new TestHyperlink[]{new TestHyperlink(ClassHyperlink.class, "Open class 'org.jboss.jsr299.tck.tests.interceptors.definition.broken.sameClassListedTwiceInBeansXml.FordInterceptor'")}));

		CDIHyperlinkTestUtil.checkRegions(tckProject, "WebContent/WEB-INF/beans.xml", regionList, HyperlinkDetector.getInstance());
	}

//	public void testAlternativesClassOpenOns() throws Exception {
//		CDIHyperlinkTestUtil.checkHyperLinkInXml(tckProject, "WebContent/WEB-INF/beans.xml", 73, "org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink");
//	}
//
//	public void testAlternativesStereotypeOpenOns() throws Exception {
//		CDIHyperlinkTestUtil.checkHyperLinkInXml(tckProject, "WebContent/WEB-INF/beans.xml", 395, "org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink");
//	}
//
//	public void testDecoratorClassOpenOns() throws Exception {
//		CDIHyperlinkTestUtil.checkHyperLinkInXml(tckProject, "WebContent/WEB-INF/beans.xml", 1159, "org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink");
//	}
//
//	public void testInterceptorClassOpenOns() throws Exception {
//		CDIHyperlinkTestUtil.checkHyperLinkInXml(tckProject, "WebContent/WEB-INF/beans.xml", 1841, "org.jboss.tools.common.text.ext.hyperlink.ClassHyperlink");
//	}
}