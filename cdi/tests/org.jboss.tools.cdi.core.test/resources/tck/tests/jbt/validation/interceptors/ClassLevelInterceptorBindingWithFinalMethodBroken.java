package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

@InterceptorStereotype
public class ClassLevelInterceptorBindingWithFinalMethodBroken {

	final public void foo() {
	}
}