package org.jboss.jsr299.tck.tests.jbt.validation.interceptors;

import javax.inject.Inject;

public class InjectInterceptorBroken {

	@Inject CatInterceptor cat;
}