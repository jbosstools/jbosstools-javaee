package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Produces;
import javax.interceptor.Interceptor;

import org.jboss.jsr299.tck.tests.decorators.interceptor.FooBinding;

@Interceptor
@FooBinding
public class TestInterceptor4{

	@Produces
	public String produce(){
		return "a";
	}
}
