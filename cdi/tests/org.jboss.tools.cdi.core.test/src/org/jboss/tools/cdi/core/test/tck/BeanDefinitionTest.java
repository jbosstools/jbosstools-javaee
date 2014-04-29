/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.tck;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;

/**
 * @author Alexey Kazakov
 */
public class BeanDefinitionTest extends TCKTest {

	/**
	 * Section 2 - Concepts
	 *   a) A bean comprises of a (nonempty) set of bean types.
	 * @throws JavaModelException 
	 */
	public void testBeanTypesNonEmpty() throws CoreException {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/bean/RedSnapper.java");
		Collection<IBean> beans = cdiProject.getBeans(file.getFullPath());
		assertEquals("There should be the only bean in org.jboss.jsr299.tck.tests.definition.bean.RedSnapper", 1, beans.size());
		beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.definition.bean.RedSnapper");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.bean.RedSnapper type", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertTrue("No legal types were found for org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean.", bean.getLegalTypes().size() > 0);
		Collection<ITypeDeclaration> declarations = bean.getAllTypeDeclarations();
		assertEquals("There should be two type declarations in org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean.", declarations.size(), 2);
		file = (IFile)bean.getResource();
		assertLocationEquals(file, declarations, "lass RedSnapper implement", 5/*914*/, 10);
		assertLocationEquals(file, declarations, "ents Animal", 5/*936*/, 6);
	}

	/**
	 * Section 2 - Concepts
	 *   b) A bean comprises of a (nonempty) set of qualifiers.
	 * Section 11.1 - The Bean interface
	 *   ba) getTypes(), getQualifiers(), getScope(), getName() and getStereotypes() must return the bean types, qualifiers, scope type, EL name and stereotypes of the bean, as defined in Chapter 2, Concepts.
	 * @throws JavaModelException 
	 */
	public void testQualifiersNonEmpty() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.RedSnapper");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.bean.RedSnapper type", 1, beans.size());
		Collection<IQualifier> qs = beans.iterator().next().getQualifiers();
		assertTrue("No qualifiers were found for org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean.", qs.size() > 0);

// Use for next, more sophisticated test
//		IQualifier any = cdiProject.getQualifier(CDIConstants.ANY_QUALIFIER_TYPE_NAME);
//		assertTrue("No @Any found for RedSnapper ", qs.contains(any));
//		IQualifier def = cdiProject.getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
//		assertTrue("No @Default found for RedSnapper bean", qs.contains(def));
//		
//		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/bean/SpiderProducer.java");
//		beans = cdiProject.getBeans(file.getFullPath());
//		IBean b = null;
//		for (IBean b1: beans) {
//			if (b1 instanceof IProducer) {
//				b = b1;
//				break;
//			}
//		}
//		qs = b.getQualifiers();
//		assertTrue("No qualifiers were found for org.jboss.jsr299.tck.tests.definition.bean.SpiderProducer bean.", qs.size() > 0);
//		assertTrue("No @Any found for SpiderProducer bean", qs.contains(any));
	}

	/**
	 * Section 2 - Concepts 
	 *   c) A bean comprises of a scope.
	 * Section 2.4 - Scopes
	 *   a) All beans have a scope.
	 * Section 3.1.3 - Declaring a managed bean
	 *   ba) Test a bean with a scope.
	 * Section 11.1 - The Bean interface
	 *   ba) getTypes(), getQualifiers(), getScope(), getName() and getStereotypes() must return the bean types, qualifiers, scope type, EL name and stereotypes of the bean, as defined in Chapter 2, Concepts.
	 * @throws JavaModelException 
	 *   
	 */
	public void testHasScopeType() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.RedSnapper");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.bean.RedSnapper type", 1, beans.size());
		IBean bean = beans.iterator().next();
		IScope scope = bean.getScope();
		assertNotNull("org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean desn't have a scope.", scope);
		assertNotNull("Scope of org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean doesn't have a link to IType.", scope.getSourceType());
		assertEquals("Wrong scope type for org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean.", "javax.enterprise.context.RequestScoped", scope.getSourceType().getFullyQualifiedName());
	}

	/**
	 * Section 2.2.1 - Legal bean types
	 *   j) A bean type may be a primitive type. Primitive types are considered to be identical to their corresponding wrapper types in java.lang.
	 *   
	 * @throws JavaModelException
	 */
	public void testPrivitiveTypes() throws JavaModelException {
		Collection<IBean> beans = getBeans("java.lang.Integer");
		assertFalse("Cannot find bean java.lang.Integer", beans.isEmpty());
		beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.Animal");
		assertFalse("Cannot find bean org.jboss.jsr299.tck.tests.definition.bean.Animal", beans.isEmpty());
	}

	/**
	 * section 3.1.2 a)
	 * section 2.2 a)
	 * section 2.2.1 a)
	 * section 2.2.1 d)
	 * section 2.2.1 e)
	 * section 2.2 l)
	 * section 11.1 ba)
	 */
	public void testBeanTypes() throws CoreException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.Tarantula");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.bean.Tarantula type", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertContainsBeanTypes(bean, "org.jboss.jsr299.tck.tests.definition.bean.Tarantula",
									 "org.jboss.jsr299.tck.tests.definition.bean.Spider",
									 "org.jboss.jsr299.tck.tests.definition.bean.Animal",
									 "java.lang.Object",
									 "org.jboss.jsr299.tck.tests.definition.bean.DeadlySpider",
									 "org.jboss.jsr299.tck.tests.definition.bean.DeadlyAnimal");

		Collection<ITypeDeclaration> declarations = bean.getAllTypeDeclarations();
		assertEquals("There should be three type declarations in org.jboss.jsr299.tck.tests.definition.bean.Tarantula bean.", declarations.size(), 3);
		//TODOs
		IFile file = (IFile)bean.getResource();
		assertLocationEquals(file, declarations, "lass Tarantula exte", 5/*841*/, 9);
		assertLocationEquals(file, declarations, "ends Spider implement", 5/*859*/, 6);
		assertLocationEquals(file, declarations, "ents DeadlySpider", 5/*877*/, 12);
	}

	/**
	 * section 2.2.1 c)
	 *
	 * @throws JavaModelException
	 */
	public void testAbstractApiType() throws CoreException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.FriendlyAntelope");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.bean.FriendlyAntelope type", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertContainsBeanTypes(bean, "org.jboss.jsr299.tck.tests.definition.bean.FriendlyAntelope",
									 "org.jboss.jsr299.tck.tests.definition.bean.AbstractAntelope",
									 "org.jboss.jsr299.tck.tests.definition.bean.Animal",
									 "java.lang.Object");

		Collection<ITypeDeclaration> declarations = bean.getAllTypeDeclarations();
		assertEquals("There should be three type declarations in org.jboss.jsr299.tck.tests.definition.bean.FriendlyAntelope bean.", declarations.size(), 2);
		IFile file = (IFile)bean.getResource();
		assertLocationEquals(file, declarations, "lass FriendlyAntelope extends A", 5/*842*/, 16);
		assertLocationEquals(file, declarations, "ends AbstractAntelope", 5/*867*/, 16);
	}

	/**
	 * section 2.2.1, d)
	 *
	 * @throws JavaModelException 
	 */
	public void testFinalApiType() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.DependentFinalTuna");
		assertFalse("No beans found for org.jboss.jsr299.tck.tests.definition.bean.DependentFinalTuna type", beans.isEmpty());
	}

	/**
	 * section 3.1.3, bd)
	 * section 11.1, ba)
	 *
	 * @throws JavaModelException 
	 */
	public void testMultipleStereotypes() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.ComplicatedTuna");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.bean.ComplicatedTuna type", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type of org.jboss.jsr299.tck.tests.definition.bean.ComplicatedTuna bean", "javax.enterprise.context.RequestScoped", bean.getScope().getSourceType().getFullyQualifiedName());
		assertEquals("Wrong EL name of org.jboss.jsr299.tck.tests.definition.bean.ComplicatedTuna bean", "complicatedTuna", bean.getName());
	}

	/**
	 * section 3.1.3 c)
	 *
	 * @throws JavaModelException 
	 */
	public void testBeanExtendsAnotherBean() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.Spider");
		assertFalse("No beans found for org.jboss.jsr299.tck.tests.definition.bean.Spider type", beans.isEmpty());
		beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.Tarantula");
		assertFalse("No beans found for org.jboss.jsr299.tck.tests.definition.bean.Tarantula type", beans.isEmpty());
	}

	/**
	 * section 11.1 bb)
	 *
	 * @throws JavaModelException 
	 */
	public void testBeanClassOnSimpleBean() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.Horse");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.bean.Horse type", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong Bean Class type of org.jboss.jsr299.tck.tests.definition.bean.Horse bean", "org.jboss.jsr299.tck.tests.definition.bean.Horse", bean.getBeanClass().getFullyQualifiedName());
	}

	public void testCyclicParametedType() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.bean.AbstractJavaSource");
		assertFalse(beans.isEmpty());
		IBean bean = beans.iterator().next();
		IParametedType t = ((ClassBean)bean).getDefinition().getParametedType();
		assertNotNull(t);
		List<? extends IParametedType> ps = t.getParameters();
		assertFalse(ps.isEmpty());
		IParametedType p = ps.get(0);
		ps = p.getParameters();
		assertFalse(ps.isEmpty());
		IParametedType p1 = ps.get(0);
		ps = p1.getParameters();
		assertFalse(ps.isEmpty());
		IParametedType p2 = ps.get(0);
		IType type = p2.getType();
		assertEquals("org.jboss.jsr299.tck.tests.definition.bean.JavaSource", type.getFullyQualifiedName());
		assertTrue(p1 == p2);
	}
}