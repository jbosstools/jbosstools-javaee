package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

public class ClassWithMethodLevelInterceptorBindingWithFinalMethodBroken {

	@CatInterceptorBinding
	public final void foo() {
	}
}
