package org.jboss.jsr299.tck.tests.jbt.validation.sessionbeans;

import javax.ejb.Singleton;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Singleton
public class SessionBeanAnnotatedInterceptorBroken {
	@AroundInvoke
	public Object alwaysReturnThis(InvocationContext ctx) throws Exception {
		return ctx.proceed();
	}
}