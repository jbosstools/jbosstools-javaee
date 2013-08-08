package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

public class ClassWithMethodLevelInterceptorBindingOk {

	@CatInterceptorBinding
	final private void foo() {
	}

	@CatInterceptorBinding
	static final public void foo2() {
	}

	@CatInterceptorBinding
	public void foo3() {
	}
}