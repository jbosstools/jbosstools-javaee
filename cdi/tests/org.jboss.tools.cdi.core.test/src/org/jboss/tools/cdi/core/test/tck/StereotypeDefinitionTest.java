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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * @author Alexey Kazakov
 */
public class StereotypeDefinitionTest extends TCKTest {

	/**
	 * section 2.7.1.1 aa)
	 * section 2.4.3 c)
	 * 
	 * @throws JavaModelException
	 */
	public void testStereotypeWithScopeType() throws CoreException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.Moose");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
		Collection<IStereotypeDeclaration> declarations = bean
				.getStereotypeDeclarations();
		assertEquals("Wrong number of stereotype declarations", 1, declarations
				.size());
		IFile file = (IFile)bean.getResource();
		assertLocationEquals(file, declarations, "@AnimalStereotype", 0/*835*/, 17);
	}

	/**
	 * section 2.7.1.1 aa) section 2.4.4 b)
	 * 
	 * @throws JavaModelException
	 */
	public void testStereotypeWithoutScopeType() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.Reindeer");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type", "javax.enterprise.context.Dependent",
				bean.getScope().getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 2.7 c) section 2.7.1 b)
	 * 
	 * @throws JavaModelException
	 */
	public void testOneStereotypeAllowed() throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.LongHairedDog");
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 2.7.2 e)
	 * section 2.7 d)
	 */
	public void testMultipleStereotypesAllowed() throws CoreException {
		Collection<IBean> beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.definition.stereotype.HighlandCow", "org.jboss.jsr299.tck.tests.definition.stereotype.Tame");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertNull("The bean should not have any EL name.", bean.getName());
		assertContainsQualifierType(bean, "org.jboss.jsr299.tck.tests.definition.stereotype.Tame");
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
		Collection<? extends ITextSourceReference> declarations = bean.getQualifierDeclarations(false);
		assertEquals("Wrong number of qualifier declarations", 1, declarations.size());
		IFile file = (IFile)bean.getResource();
		assertLocationEquals(file, declarations, "@Tame", 0/*877*/, 5);

		declarations = bean.getStereotypeDeclarations();
		assertEquals("Wrong number of stereotype declarations", 2, declarations.size());
		assertLocationEquals(file, declarations, "@HornedMammalStereotype", 0/*835*/, 23);
		assertLocationEquals(file, declarations, "@AnimalStereotype", 0/*859*/, 17);
	}

	/**
	 * section 2.7.2 e) section 2.4.4 e)
	 * 
	 * @throws JavaModelException
	 */
	public void testExplicitScopeOverridesMergedScopesFromMultipleStereotype()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.Springbok");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 ab)
	 * 
	 * @throws JavaModelException
	 */
	public void testStereotypeDeclaredInheritedIsInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.BorderCollie");
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 aba)
	 * 
	 * @throws JavaModelException
	 */
	public void testStereotypeNotDeclaredInheritedIsNotInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.ShetlandPony");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type", "javax.enterprise.context.Dependent",
				bean.getScope().getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 ah)
	 * 
	 * @throws JavaModelException
	 */
	public void testStereotypeDeclaredInheritedIsIndirectlyInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.EnglishBorderCollie");
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 aha)
	 * 
	 * @throws JavaModelException
	 */
	public void testStereotypeNotDeclaredInheritedIsNotIndirectlyInherited()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.MiniatureClydesdale");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type", "javax.enterprise.context.Dependent",
				bean.getScope().getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 hhh)
	 * 
	 * @throws JavaModelException
	 */
	public void testStereotypeScopeIsOverriddenByInheritedScope()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.Chihuahua");
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.SessionScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 hhi)
	 * 
	 * @throws JavaModelException
	 */
	public void testStereotypeScopeIsOverriddenByIndirectlyInheritedScope()
			throws JavaModelException {
		Collection<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.stereotype.MexicanChihuahua");
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.SessionScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}
}