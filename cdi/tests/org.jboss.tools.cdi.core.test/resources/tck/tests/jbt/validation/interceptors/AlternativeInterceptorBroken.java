package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import javax.enterprise.inject.Alternative;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Alternative
public class AlternativeInterceptorBroken {

	@AroundInvoke
	public Object alwaysReturnThis(InvocationContext ctx) throws Exception {
		return ctx.proceed();
	}
}