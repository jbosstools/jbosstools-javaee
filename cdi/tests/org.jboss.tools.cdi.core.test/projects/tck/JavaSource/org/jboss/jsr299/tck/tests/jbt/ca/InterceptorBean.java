package org.jboss.jsr299.tck.tests.jbt.ca;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.jsr299.tck.tests.decorators.interceptor.FooBinding;

@FooBinding
@Interceptor
public class InterceptorBean {

	public InterceptorBean() {
	}

	@AroundInvoke
	public Object manage(InvocationContext ic) throws Exception {
		return null;
	}
}