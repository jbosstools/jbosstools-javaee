package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Decorator
public class TestDecorator2 {
	@Inject @Delegate @AAnnotation String str;

	@Produces
	public String produce(){
		return "a";
	}
}
