package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import javax.enterprise.inject.Disposes;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Transactional
class FordInterceptor {
	@AroundInvoke
	public Object alwaysReturnThis(InvocationContext ctx) throws Exception {
		return ctx.proceed();
	}

	public static void destorySpider(@Disposes Spider spider) {
	}
}