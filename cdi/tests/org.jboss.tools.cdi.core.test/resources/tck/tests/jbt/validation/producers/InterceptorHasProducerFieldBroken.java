package org.jboss.jsr299.tck.tests.jbt.validation.producers;

import javax.enterprise.inject.Produces;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
public class InterceptorHasProducerFieldBroken {

	@AroundInvoke
	public Object alwaysReturnThis(InvocationContext ctx) throws Exception {
		return ctx.proceed();
	}

	@Produces public FunnelWeaver<String> anotherFunnelWeaver;
}