package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

final public class FinalClassWithMethodLevelInterceptorBindingBroken {

	@InterceptorMethodStereotype
	public void foo() {
	}
}