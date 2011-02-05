package org.jboss.jsr299.tck.tests.jbt.validation.inject.revalidation;

import javax.inject.Inject;

public abstract class TestBeanBroken {

	@Inject ITestBean foo;

	public TestBeanBroken() {
	}
}