package cdi20;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.ObservesAsync;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Log
@Interceptor
@Dependent
public class LoggingInterceptor {

	public LoggingInterceptor() {
		// TODO Auto-generated constructor stub
	}

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		return null;
	}
	
	public void interceptorObserver(@ObservesAsync PaymentEvent event) {
		
	}

}
