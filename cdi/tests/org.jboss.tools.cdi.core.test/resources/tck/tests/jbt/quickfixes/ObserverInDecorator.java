package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Decorator
public class ObserverInDecorator {
	@Inject @Delegate @AAnnotation String str;
	
	public void method(@Observes String param){
		
	}
}
