package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@StereotypeAndBinding
public class InterceptorWithStereotypeThatIsBinding {

	@AroundInvoke
	public Object intercept(InvocationContext ctx) throws Exception {
	   return ctx.proceed();
	}

}