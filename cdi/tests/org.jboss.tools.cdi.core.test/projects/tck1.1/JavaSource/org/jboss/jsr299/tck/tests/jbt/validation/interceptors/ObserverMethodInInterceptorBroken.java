package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import javax.enterprise.event.Observes;
import javax.interceptor.Interceptor;

@Interceptor
@CatInterceptorBinding
public class ObserverMethodInInterceptorBroken {

	public void observeSomeEvent(@Observes String someEvent) {
	}
}