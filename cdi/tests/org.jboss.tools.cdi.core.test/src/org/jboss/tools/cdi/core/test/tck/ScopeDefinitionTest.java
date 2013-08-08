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
package org.jboss.tools.cdi.core.test.tck;

import java.util.Collection;

import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IScopeDeclaration;

/**
 * @author Alexey Kazakov
 */
public class ScopeDefinitionTest extends TCKTest {

	/**
	 * section 2.4 c)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypesAreExtensible() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.Mullet");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"org.jboss.jsr299.tck.tests.definition.scope.AnotherScopeType",
				bean.getScope().getSourceType().getFullyQualifiedName());
		Collection<IScopeDeclaration> declarations = bean.getScopeDeclarations();
		assertEquals("Wrong number of scope declarations", 1, declarations
				.size());
		assertLocationEquals(declarations, 830, 17);
	}

	/**
	 * section 2.4.3 a)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeDeclaredInJava() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.SeaBass");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
		Collection<IScopeDeclaration> declarations = bean.getScopeDeclarations();
		assertEquals("Wrong number of scope declarations", 1, declarations
				.size());
		assertLocationEquals(declarations, 878, 14);
	}

	/**
	 * section 2.4.4 aa)
	 * 
	 * @throws JavaModelException
	 */
	public void testDefaultScope() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.Order");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type", "javax.enterprise.context.Dependent",
				bean.getScope().getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 2.4.4 e), section 2.7.2 a)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeSpecifiedAndStereotyped() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.Minnow");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
		Collection<IScopeDeclaration> declarations = bean.getScopeDeclarations();
		assertEquals("Wrong number of scope declarations", 1, declarations
				.size());
		assertLocationEquals(declarations, 899, 14);
	}

	/**
	 * section 2.4.4 da)
	 * 
	 * @throws JavaModelException
	 */
	public void testMultipleIncompatibleScopeStereotypesWithScopeSpecified()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.Pollock");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type", "javax.enterprise.context.Dependent",
				bean.getScope().getSourceType().getFullyQualifiedName());
		Collection<IScopeDeclaration> declarations = bean.getScopeDeclarations();
		assertEquals("Wrong number of scope declarations", 1, declarations
				.size());
		assertLocationEquals(declarations, 908, 10);
	}

	/**
	 * section 2.4.4 c)
	 * 
	 * @throws JavaModelException
	 */
	public void testMultipleCompatibleScopeStereotypes()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.Grayling");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.ApplicationScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 2.7.2 db), section 4.1 ab)
	 * 
	 * @throws JavaModelException
	 */
	public void testWebBeanScopeTypeOverridesStereotype()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.RedSnapper");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
		Collection<IScopeDeclaration> declarations = bean.getScopeDeclarations();
		assertEquals("Wrong number of scope declarations", 1, declarations
				.size());
		assertLocationEquals(declarations, 894, 14);
	}

	/**
	 * section 4.1 ba)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeDeclaredInheritedIsInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.BorderCollie");
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 baa)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeNotDeclaredInheritedIsNotInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.ShetlandPony");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type", "javax.enterprise.context.Dependent",
				bean.getScope().getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 ba)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeDeclaredInheritedIsBlockedByIntermediateScopeTypeMarkedInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.GoldenRetriever");
		assertEquals("Wrong number of beans.", 1, beans.size());
	}

	/**
	 * section 4.1 ba)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeDeclaredInheritedIsBlockedByIntermediateScopeTypeNotMarkedInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.GoldenLabrador");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type", "javax.enterprise.context.Dependent",
				bean.getScope().getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 bc)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeDeclaredInheritedIsIndirectlyInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.EnglishBorderCollie");
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 bca)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeNotDeclaredInheritedIsNotIndirectlyInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.MiniatureClydesdale");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type", "javax.enterprise.context.Dependent",
				bean.getScope().getSourceType().getFullyQualifiedName());
	}
}