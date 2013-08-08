package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.decorator.Decorator;
import javax.enterprise.inject.Disposes;

@Decorator
public class TestDecorator {
	
	public void method(@Disposes String parameter){
		
	}
}
