package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

@Decorator
@Specializes
public class TestDecorator4 extends TestDecorator3{
	@Inject @Delegate @AAnnotation String str;

	
	public String produce(){
		return str;
	}
}
