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
package org.jboss.tools.cdi.seam.solder.core.test.v30;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducerMethod;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class BeanNamingTest extends SeamSolderTest {

	public BeanNamingTest() {}

	public void testNamedPackage() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getTestProject(), true);

		//1. package @Named; class not annotated
		Set<IBean> bs = cdi.getBeans(new Path("/CDISolderTest30/src/org/jboss/named/Dog.java"));
		assertFalse(bs.isEmpty());
		IBean b = findBeanByMemberName(bs, "Dog");
		assertNotNull(b);
		assertEquals("dog", b.getName());

		//2. package@Named; class @Named("little")
		bs = cdi.getBeans(new Path("/CDISolderTest30/src/org/jboss/named/Racoon.java"));
		assertFalse(bs.isEmpty());
		b = bs.iterator().next();
		assertEquals("little", b.getName());
	}

	public void testFullyQualifiedPackage() throws CoreException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getTestProject(), true);
		
		//1. package @FullyQualified and @Named; class not annotated
		Set<IBean> bs = cdi.getBeans(new Path("/CDISolderTest30/src/org/jboss/fullyqualified/Cat.java"));
		assertFalse(bs.isEmpty());
		IBean b = bs.iterator().next();
		assertEquals("org.jboss.fullyqualified.cat", b.getName());

		//2. package @FullyQualified and @Named; class @Named("rodent")
		bs = cdi.getBeans(new Path("/CDISolderTest30/src/org/jboss/fullyqualified/Mouse.java"));
		assertFalse(bs.isEmpty());
		b = bs.iterator().next();
		assertEquals("org.jboss.fullyqualified.rodent", b.getName());
		
		//3. package @FullyQualified and @Named; class @FullyQualified(Dog.class)
		bs = cdi.getBeans(new Path("/CDISolderTest30/src/org/jboss/fullyqualified/Elephant.java"));
		assertFalse(bs.isEmpty());
		b = findBeanByMemberName(bs, "Elephant");
		assertNotNull(b);
		assertEquals("org.jboss.named.elephant", b.getName());
	}

	public void testFullyQualifiedProducers() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getTestProject(), true);
		//1. package @FullyQualified
		Set<IBean> bs = cdi.getBeans(new Path("/CDISolderTest30/src/org/jboss/fullyqualified/Elephant.java"));
		
		//1.1 producer method @Named
		IBean b = findBeanByMemberName(bs, "getTail");
		assertNotNull(b);
		assertEquals("org.jboss.fullyqualified.tail", b.getName());

		//1.2 producer method @Named and @FullyQualified(Dog.class)
		b = findBeanByMemberName(bs, "getTrunk");
		assertNotNull(b);
		assertEquals("org.jboss.named.trunk", b.getName());

		//1.3 producer field @Named
		b = findBeanByMemberName(bs, "ear");
		assertNotNull(b);
		assertEquals("org.jboss.fullyqualified.ear", b.getName());

		//1.4 producer field @Named and @FullyQualified(Dog.class)
		b = findBeanByMemberName(bs, "eye");
		assertNotNull(b);
		assertEquals("org.jboss.named.eye", b.getName());

		//2. package has not @FullyQualified
		bs = cdi.getBeans(new Path("/CDISolderTest30/src/org/jboss/named/Dog.java"));

		//2.1 producer method @Named
		b = findBeanByMemberName(bs, "getHair");
		assertNotNull(b);
		assertEquals("hair", b.getName());

		//2.2 producer method @Named and @FullyQualified(Elephant.class)
		b = findBeanByMemberName(bs, "getNose");
		assertNotNull(b);
		assertEquals("org.jboss.fullyqualified.nose", b.getName());

		//2.3 producer field @Named
		b = findBeanByMemberName(bs, "jaws");
		assertNotNull(b);
		assertEquals("jaws", b.getName());

		//2.4 producer field @Named and @FullyQualified(Elephant.class)
		b = findBeanByMemberName(bs, "eye");
		assertNotNull(b);
		assertEquals("org.jboss.fullyqualified.black-eye", b.getName());
	}

	private IBean findBeanByMemberName(Set<IBean> bs, String memberName) {
		for (IBean b: bs) {
			if(b instanceof IClassBean) {
				if(memberName.equals(((IClassBean)b).getBeanClass().getElementName())) {
					return b;
				}
			} else if(b instanceof IBeanMember) {
				if(memberName.equals(((IBeanMember)b).getSourceMember().getElementName())) {
					return b;
				}
			}
		}
		return null;
	}

	public void testAnnotatedPackagesInJars() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getTestProject(), true);
		
		// Package @FullyQualified and @Named

		// 1. Class @Named("bird-of-prey")
		Set<IBean> bs = cdi.getBeans("org.jboss.birds.bird-of-prey", false);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IClassBean);
		IClassBean cb = (IClassBean)b;
		IType t = cb.getBeanClass();
		assertEquals("org.jboss.birds.Eagle", t.getFullyQualifiedName());

		// 2. Class not annotated
		bs = cdi.getBeans("org.jboss.birds.nightingale", false);
		assertEquals(1, bs.size());
		b = bs.iterator().next();
		assertTrue(b instanceof IClassBean);
		cb = (IClassBean)b;
		t = cb.getBeanClass();
		assertEquals("org.jboss.birds.Nightingale", t.getFullyQualifiedName());
		
		// 3. Producer method @Named
		bs = cdi.getBeans("org.jboss.birds.song", false);
		assertEquals(1, bs.size());
		b = bs.iterator().next();
		assertTrue(b instanceof IProducerMethod);
		IProducerMethod mb = (IProducerMethod)b;
		IMember m = mb.getSourceMember();
		assertEquals("getSong", m.getElementName());
	}
}