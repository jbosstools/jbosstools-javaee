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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IStereotype;

/**
 * @author Alexey Kazakov
 */
public class InterceptorDefinitionTest extends TCKTest {

	/**
	 * Section 9.1.2 - Interceptor bindings for stereotypes
	 *   b) An interceptor binding declared by a stereotype are inherited by any bean that declares that stereotype.
	 * @throws JavaModelException 
	 */
	public void testStereotypeInterceptorBindings() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.interceptors.definition.SecureTransaction");
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertTrue("The bean should be an interceptor", bean instanceof IClassBean);
		IClassBean interceptor = (IClassBean)bean;
		assertFalse("The interceptor should inherites interceptor bindings", interceptor.getInterceptorBindings().isEmpty());
	}

	/**
	  * 4.1. Inheritance of type-level metadata.
	  * Suppose a class X is extended directly or indirectly by the bean class of a managed 
	  * bean or session bean Y. If X is annotated with interceptor binding type Z then Y 
	  * inherits the annotation if and only if Z declares the @Inherited meta-annotation 
	  * and neither Y nor any intermediate class that is a subclass of X and a superclass of Y 
	  * declares an annotation of type Z.
	 * @throws JavaModelException
	 */
	public void testInterceptorBindingInheritance() throws JavaModelException {
		//Y inherits X indirectly through Q. 
		//X declares inheritable BindingA and BindingC and non-inheritable BindingB
		//Q overrides declaring BindingC with another value.
		Set<IBean> beans = getBeans(false, "org.jboss.jsr299.tck.tests.interceptors.definition.inheritance.Y");
		assertEquals("Wrong number of the beans", 1, beans.size());
		IClassBean bean = (IClassBean)beans.iterator().next();

		Set<IInterceptorBinding> bs = bean.getInterceptorBindings();
		assertEquals("Wrong number of interceptor bindings", 2, bs.size());
		assertContainsBindings(bs, "org.jboss.jsr299.tck.tests.interceptors.definition.inheritance.BindingA", "org.jboss.jsr299.tck.tests.interceptors.definition.inheritance.BindingC");
		assertNotContainsBindings(bs, "tck.tests.interceptors.definition.inheritance.BindingB");

		Set<IInterceptorBindingDeclaration> ds = bean.getInterceptorBindingDeclarations(true);
		assertEquals("Wrong number of interceptor binding declarations", 2, ds.size());
		assertContainsBindingDeclarationWithValue(ds, "org.jboss.jsr299.tck.tests.interceptors.definition.inheritance.BindingC", "c2");

		//Y1 inherits X directly.
		beans = getBeans(false, "org.jboss.jsr299.tck.tests.interceptors.definition.inheritance.Y1");
		assertEquals("Wrong number of the beans", 1, beans.size());
		bean = (IClassBean)beans.iterator().next();

		bs = bean.getInterceptorBindings();
		assertEquals("Wrong number of interceptor bindings", 2, bs.size());
		assertContainsBindings(bs, "org.jboss.jsr299.tck.tests.interceptors.definition.inheritance.BindingA", "org.jboss.jsr299.tck.tests.interceptors.definition.inheritance.BindingC");
		assertNotContainsBindings(bs, "tck.tests.interceptors.definition.inheritance.BindingB");

		ds = bean.getInterceptorBindingDeclarations(true);
		assertEquals("Wrong number of interceptor binding declarations", 2, ds.size());
		assertContainsBindingDeclarationWithValue(ds, "org.jboss.jsr299.tck.tests.interceptors.definition.inheritance.BindingC", "c1");
	}

	public void testStereotypeCanBeInterceptorBinding() throws Exception {
		IStereotype s = cdiProject.getStereotype("org.jboss.jsr299.tck.tests.jbt.validation.interceptors.StereotypeAndBinding");
		assertNotNull(s);
		IInterceptorBinding b = cdiProject.getInterceptorBinding("org.jboss.jsr299.tck.tests.jbt.validation.interceptors.StereotypeAndBinding");
		assertNotNull(b);

		Set<IBean> beans = getBeans(false, "org.jboss.jsr299.tck.tests.jbt.validation.interceptors.InterceptorWithStereotypeThatIsBinding");
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertTrue("The bean should be an interceptor", bean instanceof IClassBean);
		IClassBean interceptor = (IClassBean)bean;
		assertFalse("The interceptor should inherites interceptor bindings", interceptor.getInterceptorBindings().isEmpty());
	}

	public void testInterceptorIsNotInjected() throws CoreException {
		IInjectionPointField f = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/InjectInterceptorBroken.java", "cat");
		/*
		 * Invocation getBeans(false, f) returns all beans that match type and qualifiers.
		 */
		Set<IBean> bs = cdiProject.getBeans(false, f);
		assertEquals(1, bs.size());
		assertTrue(bs.iterator().next() instanceof IInterceptor);
		/*
		 * Invocation getBeans(true, f) filters away all beans that are not available for injection.
		 */
		bs = cdiProject.getBeans(true, f);
		assertTrue(bs.isEmpty());
	}

	void assertContainsBindings(Set<IInterceptorBinding> bs, String... classNames) {
		Set<String> bsn = new HashSet<String>();
		for (IInterceptorBinding b: bs) {
			bsn.add(b.getSourceType().getFullyQualifiedName());
		}
		for (String cn: classNames) {
			assertTrue("Set of interceptor bindings should include " + cn, bsn.contains(cn));
		}
	}

	void assertNotContainsBindings(Set<IInterceptorBinding> bs, String... classNames) {
		Set<String> bsn = new HashSet<String>();
		for (IInterceptorBinding b: bs) {
			bsn.add(b.getSourceType().getFullyQualifiedName());
		}
		for (String cn: classNames) {
			assertFalse("Set of interceptor bindings should not include " + cn, bsn.contains(cn));
		}
	}

	void assertContainsBindingDeclarationWithValue(Set<IInterceptorBindingDeclaration> bs, String className, String value) throws JavaModelException {
		for (IInterceptorBindingDeclaration b: bs) {
			if(className.equals(b.getInterceptorBinding().getSourceType().getFullyQualifiedName())) {
				IMemberValuePair[] ps = b.getMemberValuePairs();
				for (IMemberValuePair p: ps) {
					if(p.getMemberName().equals("value")) {
						assertEquals(value, p.getValue());
						return;
					}
				}
				fail("Value " + value + " is not found at declaration.");
			}
		}
		fail("Set of interceptor bindings should include " + className);
	}

	public void testCustomInterceptor() throws CoreException {
		String path = "JavaSource/org/jboss/jsr299/tck/tests/interceptors/definition/custom/SimpleInterceptorWithoutAnnotations.java";
		IClassBean bean = getClassBean(path);
		assertNotNull("Can't find the bean.", bean);
		assertTrue("The bean is not a decorator.", bean instanceof IInterceptor);
	}
}