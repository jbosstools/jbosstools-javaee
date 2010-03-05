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

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * @author Alexey Kazakov
 */
public class BeanDefinitionTest extends TCKTest {

	/**
	 * Section 2 - Concepts
	 *   a) A bean comprises of a (nonempty) set of bean types.
	 */
	public void testBeanTypesNonEmpty() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/bean/RedSnapper.java");
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		assertEquals("There should be the only bean in org.jboss.jsr299.tck.tests.definition.bean.RedSnapper", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertTrue("No legal types were found for org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean.", bean.getLegalTypes().size() > 0);
		Set<ITypeDeclaration> declarations = bean.getAllTypeDeclarations();
		assertEquals("There should be two type declarations in org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean.", declarations.size(), 2);
		assertLocationEquals(declarations, 936, 10);
		assertLocationEquals(declarations, 958, 6);
	}

	/**
	 * Section 2 - Concepts
	 *   b) A bean comprises of a (nonempty) set of qualifiers.
	 * Section 11.1 - The Bean interface
	 *   ba) getTypes(), getQualifiers(), getScope(), getName() and getStereotypes() must return the bean types, qualifiers, scope type, EL name and stereotypes of the bean, as defined in Chapter 2, Concepts.
	 */
	public void testQualifiersNonEmpty() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/bean/RedSnapper.java");
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());

		Set<IQualifier> qs = beans.iterator().next().getQualifiers();
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
	 *   
	 */
	public void testHasScopeType() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/bean/RedSnapper.java");
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
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
		IType type = getType("java.lang.Integer");
		Set<IBean> beans = cdiProject.getBeans(true, type, new IType[0]);
		assertNotNull("There should be the only bean with int type", beans);
		assertEquals("There should be the only bean with int type", 1, beans.size());
		type = getType("org.jboss.jsr299.tck.tests.definition.bean.Animal");
		beans = cdiProject.getBeans(true, type, new IType[0]);
		assertNotNull("There should be the only bean with org.jboss.jsr299.tck.tests.definition.bean.Animal type", beans);
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.bean.Animal type", 1, beans.size());
	}

	/**
	 * e) A bean comprises of an optional bean EL name.
	 */
	public void testNonDefaultNamed() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/name/Moose.java");
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		assertEquals("org.jboss.jsr299.tck.tests.definition.name.Moose should have the only bean.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong EL name of org.jboss.jsr299.tck.tests.definition.name.Moose bean.", "aMoose", bean.getName());
		assertLocationEquals(bean.getNameLocation(), 918, 16);
	}

	/**
	 * e) A bean comprises of an optional bean EL name (continue).
	 */
	public void testNotNamedInJava() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/name/SeaBass.java");
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		assertEquals("org.jboss.jsr299.tck.tests.definition.name.SeaBass should have the only bean.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertNull("org.jboss.jsr299.tck.tests.definition.name.Moose bean should not have any EL name.", bean.getName());
	}

	private void assertLocationEquals(Set<? extends ITextSourceReference> references, int startPosition, int length) {
		for (ITextSourceReference reference : references) {
			if(reference.getStartPosition()==startPosition) {
				assertLocationEquals(reference, startPosition, length);
				return;
			}
		}
		StringBuffer message = new StringBuffer("Location [start positopn=").append(startPosition).append(", lengt=").append(length).append("] has not been found among ");
		for (ITextSourceReference reference : references) {
			message.append("[").append(reference.getStartPosition()).append(", ").append(reference.getLength()).append("] ");
		}
		fail(message.toString());
	}

	private void assertLocationEquals(ITextSourceReference reference, int startPosition, int length) {
		assertEquals("Wrong start position", startPosition, reference.getStartPosition());
		assertEquals("Wrong length", length, reference.getLength());
	}
}