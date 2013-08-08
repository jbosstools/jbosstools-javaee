package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.inject.Named;
import javax.interceptor.Interceptor;

import org.jboss.jsr299.tck.tests.decorators.interceptor.FooBinding;

@Interceptor
@FooBinding
@Named
public class TestInterceptor5{

	
	public String produce(){
		return "a";
	}
}
