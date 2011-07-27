package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Specializes;
import javax.interceptor.Interceptor;

import org.jboss.jsr299.tck.tests.decorators.interceptor.FooBinding;

@Interceptor
@FooBinding
@Specializes
public class TestInterceptor6 extends TestInterceptor5{

	
	public String produce(){
		return "a";
	}
}
