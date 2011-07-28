package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.event.Observes;
import javax.interceptor.Interceptor;

import org.jboss.jsr299.tck.tests.decorators.interceptor.FooBinding;

@Interceptor
@FooBinding
public class ObserverInInterceptor {
	
	public void method(@Observes String param){
		
	}
}
