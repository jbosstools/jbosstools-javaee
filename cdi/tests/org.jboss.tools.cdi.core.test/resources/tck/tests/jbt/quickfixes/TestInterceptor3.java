package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Disposes;
import javax.interceptor.Interceptor;

@Interceptor
public class TestInterceptor3 {
	
	public void method(@Disposes String parameter){
		
	}
}
