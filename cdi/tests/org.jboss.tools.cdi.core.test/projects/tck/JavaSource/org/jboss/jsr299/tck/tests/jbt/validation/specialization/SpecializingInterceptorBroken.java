package org.jboss.jsr299.tck.tests.jbt.validation.specialization;

import javax.enterprise.inject.Specializes;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Specializes
public class SpecializingInterceptorBroken extends Farmer {

	@AroundInvoke
	public Object alwaysReturnThis(InvocationContext ctx) throws Exception {
		return ctx.proceed();
	}
}