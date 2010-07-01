package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

@CatInterceptorBinding
public class ClassLevelInterceptorBindingOk {

	final private void foo() {
	}

	static final public void foo2() {
	}

	public void foo3() {
	}
}