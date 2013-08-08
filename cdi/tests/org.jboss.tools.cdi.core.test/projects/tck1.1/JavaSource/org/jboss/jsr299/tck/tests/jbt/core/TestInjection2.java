package org.jboss.jsr299.tck.tests.jbt.core;

import javax.inject.Inject;

/**
 * Test contains 10 injection points in methods with the same name and equally named parameters
 * but with different parameter types. Method CDIUtil.findInjectionPoint() is tested to return
 * correct injection point for each parameter. 
 *
 */
public class TestInjection2 {

	@Inject
	public void initialize(TestBean children) {
	}

	@Inject
	public void initialize(FooBean children) {
	}

	@Inject
	public void initialize(TestBean children, TestBean children2) {
	}

	@Inject
	public void initialize(TestBean children, FooBean children2) {
	}

	@Inject
	public void initialize(FooBean children, FooBean children2) {
	}

	@Inject
	public void initialize(FooBean children, TestBean children2) {
	}
	
	@Inject TestBean children;
	@Inject FooBean children2;

	static class TestBean {
		@Inject TestBean children;
		@Inject FooBean children2;
	}

	static class FooBean {
		@Inject TestBean children2;
		@Inject FooBean children;
	}

}
