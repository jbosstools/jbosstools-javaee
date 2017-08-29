package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.inject.Named;

@Decorator
@Named("Vasja")
public class TestDecorator3 {
	@Inject @Delegate @AAnnotation String str;

	
	public String produce(){
		return str;
	}
}
