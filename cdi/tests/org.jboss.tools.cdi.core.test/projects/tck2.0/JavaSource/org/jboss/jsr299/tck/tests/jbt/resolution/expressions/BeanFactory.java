package org.jboss.jsr299.tck.tests.jbt.resolution.expressions;

import javax.enterprise.inject.Produces;

public class BeanFactory {
	static final long W = 3 + 5 * 1;

	@Produces
	@IntQualifier(width=2-(5 + 3) + 7*2, height= (12 + W) * 2 / 4)
	String s;
}
