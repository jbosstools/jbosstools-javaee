package org.jboss.jsr299.tck.tests.jbt.validation.producers;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
public class InterceptorHasProducerMethodBroken {

	@AroundInvoke
	public Object alwaysReturnThis(InvocationContext ctx) throws Exception {
		return ctx.proceed();
	}

	@Produces
	public FunnelWeaver<String> create2(InjectionPoint point) {
		return null;
	}
}