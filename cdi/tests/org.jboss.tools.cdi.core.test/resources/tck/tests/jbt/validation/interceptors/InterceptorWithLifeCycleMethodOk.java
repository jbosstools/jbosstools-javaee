package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.Interceptor;

@TypeInterceptorBinding
@Interceptor
public class InterceptorWithLifeCycleMethodOk {

    @PostConstruct
    public void initialize() {
    }

	@PreDestroy
	public void destroy() {
	}
}