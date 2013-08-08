package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.interceptor.Interceptor;

@Interceptor
public class TestInterceptor3 {
	@Produces
	public String produce(){
		return "";
	}
	
	public void method(@Disposes String parameter){
		
	}
}
