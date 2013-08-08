package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@NamedStereotype
public class NamedStereotypedInterceptorBroken {

	@AroundInvoke
	public Object alwaysReturnThis(InvocationContext ctx) throws Exception {
		return ctx.proceed();
	}
}