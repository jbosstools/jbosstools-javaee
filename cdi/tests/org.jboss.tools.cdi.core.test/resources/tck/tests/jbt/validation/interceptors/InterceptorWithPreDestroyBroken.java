package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import javax.annotation.PreDestroy;
import javax.interceptor.Interceptor;

@Interceptor
@CatInterceptorBinding
public class InterceptorWithPreDestroyBroken {

	@PreDestroy
	public void destroy() {		
	}
}