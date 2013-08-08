package org.jboss.jsr299.tck.tests.jbt.lookup;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
public class PaymentInterceptor implements PaymentProcessor {

	public PaymentInterceptor() {
	}

	@AroundInvoke
	public Object manage(InvocationContext ic) throws Exception {
		return null;
	}
}